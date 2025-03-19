/**
 *   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
 *   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
 *   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
 *   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
 *   de correo seginfo@eglobal.com.mx                                                         *
 **/
package com.eglobal.tools.parser.parsers;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.eglobal.tools.parser.pojo.AParsedObject;
import com.eglobal.tools.parser.pojo.FieldExt;
import com.eglobal.tools.parser.pojo.FieldFormat;
import com.eglobal.tools.parser.pojo.Message;
import com.eglobal.tools.parser.pojo.util.Converter;
import com.google.gson.Gson;

public class AmexParser extends FieldLoader implements IParser {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AmexParser.class);
    private static AmexParser __INSTANCE;
    private boolean isLastField = false;
    private boolean isLastSubField = false;
    private boolean isBCD = false;


    private AmexParser() {
        init("resources/parsers/fieldAmex.json");
    }


    public static AmexParser getInstance() {
        if (__INSTANCE == null) {
            __INSTANCE = new AmexParser();
        }
        return __INSTANCE;
    }


    @Override
    public Message parse(ByteBuffer buffer, Object... objects) {
        Message message = new Message();
        log.debug("Starting parse with buffer: " + buffer);
        FieldFormat fieldFormat = _fieldFormats.get(0); // header
        String bitmap = null;
        String mti = null;
        if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
            Object value = null;
            try {
                value = parseField(buffer, fieldFormat);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            if (value instanceof FieldExt) {
                mti = (String) ((FieldExt) value).getField("MTI");
                log.debug("MTI: " + mti);
                message.setMti(mti);
                bitmap = (String) ((FieldExt) value).getField("Bitmap");
            }
//                cargarConfiguracionPorMTI(mti);
            message.setField(fieldFormat.getName(), value);
        }
        assert bitmap != null;
        List<Integer> fieldsPresent = getFieldsPresent(bitmap, 0);
        log.debug("Fields present after header: " + fieldsPresent);
        cargarConfiguracionPorMTI(mti, fieldsPresent);
        if (fieldsPresent.get(0) == 1) {
            // secondary bitmap
            fieldFormat = _fieldFormats.get(1);
            Object value = null;
            try {
                value = parseField(buffer, fieldFormat);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            List<Integer> _fieldsPresent = getFieldsPresent(value.toString(), 64);
            for (Integer integer : _fieldsPresent) {
                fieldsPresent.add(integer);
            }
            fieldsPresent.remove(0); // removing secondary bitmap
        }
        boolean hasField28 = false;
        for (int i = 0; i < fieldsPresent.size(); i++) {
            int fieldIndex = fieldsPresent.get(i);
            fieldFormat = _fieldFormats.get(fieldIndex);
            if (fieldFormat == null) {
                log.error("Field " + fieldIndex + " is not recognized");
                break;
            }
            if (i == fieldsPresent.size() - 1) {
                isLastField = true;
            }
//            if (fieldFormat.getIndex() == 55) {
//                buffer = expandBuffer(buffer, 2);
//            }
            Object value = null;
            try {
                value = parseField(buffer, fieldFormat);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            message.setField("" + fieldFormat.getIndex(), value);
            if (fieldIndex == 28) {
                hasField28 = true;
            }
            if (value instanceof FieldExt) {
                FieldExt fieldExt = (FieldExt) value;
                log.debug("Parsed field: {} with subfields:", fieldFormat.getName());
                for (Map.Entry<String, Object> entry : fieldExt.getSubfields().entrySet()) {
                    log.debug("Subfield: {} = {}", entry.getKey(), entry.getValue());
                }
            } else {
                log.debug("Parsed field: {} with value: {}", fieldFormat.getName(), value);
            }
            isLastField = false;
        }
        if (hasField28) {
            message.setType("AmexOptBlue");
            log.debug("Field 28 detected, setting message type to 'AmexOptBlue'");
        } else {
            message.setType("Amex");
        }
        return message;
    }


    private void cargarConfiguracionPorMTI(String mti, List<Integer> fieldsPresent) {
        if ("1110".equals(mti)) {
            init("resources/parsers/fieldAmex_1110.json");
        } else if ("1210".equals(mti)) {
            init("resources/parsers/fieldAmex_1210.json");
        } else if ("1420".equals(mti) || "1430".equals(mti)) {
            init("resources/parsers/fieldAmex_1420.json");
        } else {
            init("resources/parsers/fieldAmex.json");
            log.warn("MTI no soportado: " + mti + ", utilizando configuración por defecto.");
        }
    }


    private List<Integer> getFieldsPresent(String bitmap, int offset) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitmap.length(); i += 2) {
            String hex = bitmap.substring(i, i + 2);
            int value = Integer.parseInt(hex, 16);
            String binary = Integer.toBinaryString(value);
            binary = String.format("%8s", binary).replace(' ', '0');
            sb.append(binary);
        }
        if (log.isDebugEnabled()) {
            log.debug("Hex bitmap: " + bitmap + " -> Binary String: " + sb.toString());
        }
        List<Integer> fieldList = new ArrayList<>();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '1') {
                fieldList.add(i + 1 + offset);
            }
        }
        if (log.isDebugEnabled()) {
            Gson gson = new Gson();
            log.debug("fields present: " + gson.toJson(fieldList));
        }
        return fieldList;
    }


    private Object parseField(ByteBuffer buffer, FieldFormat fieldFormat) throws UnsupportedEncodingException {
        log.debug("Parsing field: " + fieldFormat.getIndex() + " (" + fieldFormat.getName() + ") with buffer remaining: " + buffer.remaining());
        int numBytes = 0;
        boolean bcdFlag = false;
        if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
            int length = fieldFormat.getLength();
            isBCD = "BCD".equalsIgnoreCase(fieldFormat.getFormat());
            boolean divideLength = isBCD && (fieldFormat.getDivideLengthForBCD() == null || fieldFormat.getDivideLengthForBCD());
            if (isLastField && isLastSubField || "Rest of Data".equalsIgnoreCase(fieldFormat.getName())) {
                numBytes = buffer.remaining();
            } else {
                numBytes = isBCD ? (int) Math.ceil(length / 2.0) : length;
            }
        } else
//            numBytes = isBCD ? (int) Math.ceil(length / 2.0) : length;
        {
            Pattern pattern = Pattern.compile("L+var");
            isBCD = "BCD".equalsIgnoreCase(fieldFormat.getFormat());
            Matcher matcher = pattern.matcher(fieldFormat.getLengthIndicator());
            if (matcher.find()) {
                int counter = 0;
                for (int i = 0; i < fieldFormat.getLengthIndicator().length(); i++, counter++) {
                    if (fieldFormat.getLengthIndicator().charAt(i) != 'L') {
                        break;
                    }
                }
                log.debug(counter + " position to length");
                byte[] lengthBytes = new byte[counter];
                if (buffer.remaining() < lengthBytes.length && !isLastField) {
                    log.error("Buffer underflow: expected length bytes " + lengthBytes.length + " but remaining " + buffer.remaining());
                    throw new BufferUnderflowException();
                }
                if (buffer.remaining() >= lengthBytes.length) {
                    buffer.get(lengthBytes);
                    log.debug("length bytes: " + Converter.byteArrayAsHexString(lengthBytes));
                    if ("Application Cryptogram".equalsIgnoreCase(fieldFormat.getName()) || "Header Version Number".equalsIgnoreCase(fieldFormat.getName()) || "Issuer Authentication Data".equalsIgnoreCase(fieldFormat.getName())) {
                        numBytes = Integer.parseInt(Converter.bcdValue(lengthBytes)) / 2;
                    } else
//                    } else if  ("BCD".equalsIgnoreCase(fieldFormat.getFormat()) && !"Application Cryptogram".equalsIgnoreCase(fieldFormat.getName())) {
//                        numBytes = Integer.parseInt(Converter.bcdValue(lengthBytes)) / 2;
//                    } else if  ("BCD".equalsIgnoreCase(fieldFormat.getFormat()) && !"Header Version Number".equalsIgnoreCase(fieldFormat.getName())) {
//                        numBytes = Integer.parseInt(Converter.bcdValue(lengthBytes)) / 2;
                        if ("IAD".equalsIgnoreCase(fieldFormat.getName()) || "Unpredictable Number".equalsIgnoreCase(fieldFormat.getName())) {
                            numBytes = Integer.parseInt(Converter.bcdValue(lengthBytes));
                        } else
//                    } else if ("Issuer Authentication Data".equalsIgnoreCase(fieldFormat.getName())) {
//                        numBytes = Integer.parseInt(Converter.bcdValue(lengthBytes));
                        {
                            numBytes = Integer.parseInt(new String(Converter.ebcdicToAscii(lengthBytes)));
                        }
                    if (!isLastField && numBytes > buffer.remaining()) {
                        log.error("Buffer underflow: expected numBytes " + numBytes + " but remaining " + buffer.remaining());
                        throw new BufferUnderflowException();
                    }
                } else if (isLastField) {
                    numBytes = buffer.remaining();
                }
            }
        }
        log.debug("Buffer before reading field data: {}", Converter.byteArrayAsHexString(Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit())));
        if (buffer.remaining() < numBytes) {
            log.error("Buffer underflow: expected numBytes " + numBytes + " but remaining " + buffer.remaining());
            throw new BufferUnderflowException();
        }
        byte[] bytes = new byte[numBytes];
        buffer.get(bytes);
        Object value = convertParseField(fieldFormat.getFormat(), bytes);
        log.debug("Parsed value: " + value + " for field: " + fieldFormat.getName());
        if (fieldFormat.getName().equalsIgnoreCase("Date And Time, Transmission")) {
            value = formatDateAndTime(value.toString());
        }
        if (fieldFormat.getName().equalsIgnoreCase("Systems Trace Audit Number") || fieldFormat.getName().equalsIgnoreCase("Retrieval Reference Number") || fieldFormat.getName().equalsIgnoreCase("Card Acceptor Terminal ID") || fieldFormat.getName().equalsIgnoreCase("Card Acceptor ID Code")) {
            validateAlphaNumericAndSpecialCharacters(value.toString());
        }
        if (fieldFormat.getName().equalsIgnoreCase("Date And Time, Local Transaction")) {
            validateDateTime(value.toString());
        }
        if (fieldFormat.getName().equalsIgnoreCase("Date, Effective") || fieldFormat.getName().equalsIgnoreCase("Date, Expiration")) {
            validateEffectiveDate(value.toString());
        }
        if (fieldFormat.getName().equalsIgnoreCase("Track 1 Data") || fieldFormat.getName().equalsIgnoreCase("Track 2 Data")) {
            value = new String(bytes, "Cp1047");
        }
        if (fieldFormat.getSubfields() != null) {
            FieldExt fieldExt = new FieldExt();
            fieldExt.setSubfields(new LinkedHashMap<>());
            ByteBuffer b = ByteBuffer.wrap(bytes);
            if ("fixed".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
                for (FieldFormat subFieldFormat : fieldFormat.getSubfields()) {
                    Object subfieldValue = parseField(b, subFieldFormat);
                    fieldExt.setField(subFieldFormat.getName(), subfieldValue);
                }
            } else if ("Lvar".equalsIgnoreCase(fieldFormat.getLengthIndicator()) && "BCD".equalsIgnoreCase(fieldFormat.getFormat())) {
                int pos = 0;
                int i = 0;
                while (pos < value.toString().length()) {
                    String _value = value.toString().substring(pos, pos += fieldFormat.getSubfields().get(i).getLength());
                    fieldExt.setField(fieldFormat.getSubfields().get(i++).getName(), _value);
                }
            } else if ("LLvar".equalsIgnoreCase(fieldFormat.getLengthIndicator()) && "Card Acceptor Name/Location".equalsIgnoreCase(fieldFormat.getName())) {
                String valueString = new String(Converter.ebcdicToAscii(bytes));
                String[] subfieldValues = valueString.split("\\\\");
                for (int i = 0; i < subfieldValues.length && i < fieldFormat.getSubfields().size(); i++) {
                    FieldFormat subFieldFormat = fieldFormat.getSubfields().get(i);
                    String subfieldValue = subfieldValues[i];
                    fieldExt.setField(subFieldFormat.getName(), subfieldValue);
                }
            } else if ("LLvar".equalsIgnoreCase(fieldFormat.getLengthIndicator()) && !"Card Acceptor Name/Location".equalsIgnoreCase(fieldFormat.getName())) {
                for (int i = 0; i < fieldFormat.getSubfields().size(); i++) {
                    FieldFormat subFieldFormat = fieldFormat.getSubfields().get(i);
                    int subfieldLength = subFieldFormat.getLength();
                    isLastSubField = (i == fieldFormat.getSubfields().size() - 1);
                    if (isLastSubField && isLastField && "LLvar".equals(subFieldFormat.getLengthIndicator())) {
                        int lengthBytesSize = 2;
                        if (b.remaining() <= lengthBytesSize) {
                            log.error("Buffer underflow: no hay suficiente espacio después de descontar los {} bytes de LLvar.", lengthBytesSize);
                            throw new BufferUnderflowException();
                        }
                        log.debug("Asignando el resto del buffer ({}) al último subcampo \'{}\'", subfieldLength, subFieldFormat.getName());
                        byte[] subfieldBytesToExtract = new byte[lengthBytesSize];
                        b.get(subfieldBytesToExtract);
                        subfieldLength = b.remaining();
                    }
                    if (isLastSubField && isLastField && !"LLvar".equals(subFieldFormat.getLengthIndicator())) {
                        subfieldLength = b.remaining();
                        log.debug("Asignando el resto del buffer al ultimo subcampo: {}", subfieldLength);
                    }
                    if (b.remaining() < subfieldLength) {
                        log.error("Buffer underflow: expected to read {} bytes, but remaining {}", subfieldLength, b.remaining());
                        throw new BufferUnderflowException();
                    }
                    byte[] subfieldBytes = new byte[subfieldLength];
                    b.get(subfieldBytes);
                    String subfieldValue = convertParseField(subFieldFormat.getFormat(), subfieldBytes);
                    fieldExt.setField(subFieldFormat.getName(), subfieldValue);
                    log.debug("Subfield {} = {}", subFieldFormat.getName(), subfieldValue);
                }
            } else if ("LLLvar".equalsIgnoreCase(fieldFormat.getLengthIndicator())) {
                ByteBuffer dataBuffer = ByteBuffer.wrap(b.array());
                if (dataBuffer.remaining() > 0 && fieldFormat.getSubfields() != null) {
                    for (int i = 0; i < fieldFormat.getSubfields().size(); i++) {
                        FieldFormat subFieldFormat = fieldFormat.getSubfields().get(i);
                        int subfieldLength = subFieldFormat.getLength();
                        isBCD = "BCD".equalsIgnoreCase(subFieldFormat.getFormat());
//                        boolean divideLength = isBCD && (fieldFormat.getDivideLengthForBCD() == null || fieldFormat.getDivideLengthForBCD());
                        isLastSubField = (i == fieldFormat.getSubfields().size() - 1);
                        if (isLastSubField || "Rest of Data".equalsIgnoreCase(fieldFormat.getName())) {
                            subfieldLength = b.remaining();
                            log.debug("Asignando el resto del buffer al ultimo subcampo: {}", subfieldLength);
                        } else {
                            subfieldLength = isBCD ? (int) Math.ceil(subfieldLength / 2.0) : subfieldLength;
                        }
                        if (b.remaining() < subfieldLength && !isLastField && !"55".equalsIgnoreCase(String.valueOf(fieldFormat.getIndex()))) {
                            log.error("Buffer underflow: expected to read {} bytes, but remaining {}", subfieldLength, b.remaining());
                            throw new BufferUnderflowException();
                        }
//                        byte[] subfieldBytes = new byte[subfieldLength];
//                        b.get(subfieldBytes);
                        Object subfieldValue = parseField(dataBuffer, subFieldFormat);
                        fieldExt.setField(subFieldFormat.getName(), subfieldValue);
//                        byte[] subfieldBytes = new byte[subfieldLength];
                        byte[] subfieldBytes = new byte[0];
                        if (isBCD) {
                            subfieldBytes = new byte[((String) subfieldValue).length() / 2];
                        } else {
                            subfieldBytes = new byte[((String) subfieldValue).length()];
                        }
                        b.get(subfieldBytes);
                        log.debug("Subfield {} = {}", subFieldFormat.getName(), subfieldValue);
                    }
                }
            }
//                    for (FieldFormat subFieldFormat : fieldFormat.getSubfields()) {
//                        if (dataBuffer.remaining() < subFieldFormat.getLength()) {
//                            log.error("Buffer underflow: expected to read " + subFieldFormat.getLength() + " bytes but remaining " + dataBuffer.remaining());
//                            throw new BufferUnderflowException();
//                        }
//                        Object subfieldValue = parseField(dataBuffer, subFieldFormat);
//                        fieldExt.setField(subFieldFormat.getName(), subfieldValue);
//                    }
            log.debug("Buffer before reading field data: {}", Converter.byteArrayAsHexString(Arrays.copyOfRange(buffer.array(), buffer.position(), buffer.limit())));
            value = fieldExt;
        } else {
            if (bcdFlag) {
                value = value.toString();
            }
        }
        return value;
    }


    private void validateEffectiveDate(String date) {
        if (date.equals("****")) {
            return;
        }
        if (!date.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid date format");
        }
        int year = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(2, 4));
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month value");
        }
    }


    private void validateAlphaNumericAndSpecialCharacters(String value) {
        if (!value.matches("[A-Za-z0-9\\s\\-\\.!@#\\$%\\^&\\*\\(\\)\\+\\=\\{\\}\\[\\]\\|;:\'\",<\\.>/?]+")) {
            throw new IllegalArgumentException("Invalid characters in Systems Trace Audit Number or Retrieval Reference Number or Card Acceptor Terminal Identification");
        }
    }


    private void validateDateTime(String dateTime) {
        if (!dateTime.matches("\\d{12}")) {
            throw new IllegalArgumentException("Invalid date and time format");
        }
        int month = Integer.parseInt(dateTime.substring(2, 4));
        int day = Integer.parseInt(dateTime.substring(4, 6));
        int hour = Integer.parseInt(dateTime.substring(6, 8));
        int minute = Integer.parseInt(dateTime.substring(8, 10));
        int second = Integer.parseInt(dateTime.substring(10, 12));
        if (month < 1 || month > 12 || day < 1 || day > 31 || hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
            throw new IllegalArgumentException("Invalid date and time value");
        }
    }


    private String formatDateAndTime(String value) {
        if (value.length() != 10) {
            throw new IllegalArgumentException("Invalid Date And Time, Transmission format");
        }
        return value;
    }


    private boolean isNumericField(String fieldName) {
        Set<String> numericFields = new HashSet<>(Arrays.asList("Amount, Transaction", "Primary Account Number", "Processing Code", "Date And Time, Transmission", "Date And Time, Local Transaction", "Date, Effective", "Date, Expiration", "Country Code, Acquiring Institution", "Function Code", "Message Reason Code", "Card Acceptor Business Code", "Approval Code Length"));
        // Agregar otros nombres de campos que deban ser tratados como numéricos
        return numericFields.contains(fieldName);
    }


    private String convertParseField(String format, byte[] rawData) {
        String value = null;
        switch (format) {
            case "BCD":
            case "Binary":
            case "HEXA":
                value = Converter.byteArrayAsHexString(rawData);
                break;
            case "ASCII":
                value = new String(rawData);
                break;
            case "EBCDIC":
                value = new String(Converter.ebcdicToAscii(rawData));
                break;
            case "Alphanumeric":
                value = Converter.asciiValue(rawData);
                break;
            case "Cp1047":
                try {
                    value = new String(rawData, "Cp1047");
                } catch (UnsupportedEncodingException e) {
                    log.error("Unsupported encoding exception: {}", e.getMessage(), e);
                }
                break;
        }
        return value;
    }


    @Override
    public byte[] format(AParsedObject object) {
        Set<Integer> recognizedFields = _fieldFormats.keySet();
        Message message = (Message) object;
        ByteBuffer buffer = ByteBuffer.allocate(2500);
        List<Integer> fieldsPresent = new ArrayList<>();
        for (Integer recognizedField : recognizedFields) {
            if (message.getField("" + recognizedField) != null) {
                fieldsPresent.add(recognizedField);
            }
        }
        Gson gson = new Gson();
        log.debug("Fields present" + gson.toJson(fieldsPresent));
        String bitmap = toBitmap(fieldsPresent);
        log.debug("bitmap: " + bitmap);
        FieldExt header = ((FieldExt) message.getField("Header"));
        header.setField("Bitmap", bitmap);
        FieldFormat format = _fieldFormats.get(fieldFormats.get("Header"));
        byte[] formattedField = formatField(header, format);
        log.debug("Formated Field: " + new String(formattedField));
        buffer.put(formattedField);
        for (Integer _field : fieldsPresent) {
            Object field = message.getField("" + _field);
            format = _fieldFormats.get(_field);
            if (field != null) {
                formattedField = formatField(field, format);
                if (formattedField == null) {
                    log.error("Can not format field " + _field);
                    break;
                }
                buffer.put(formattedField);
            }
        }
        byte[] bytes = new byte[buffer.position()];
        buffer.clear();
        buffer.get(bytes);
        return bytes;
    }


    private byte[] formatField(Object field, FieldFormat format) {
        log.debug("formatting " + format.getIndex());
        ByteBuffer buffer = ByteBuffer.allocate(1500);
        buffer.mark();
        if (field instanceof FieldExt) {
            FieldExt _field = (FieldExt) field;
            for (FieldFormat subField : format.getSubfields()) {
                if (_field.getField(subField.getName()) != null) {
                    buffer.put(formatField(_field.getField(subField.getName()), subField));
                }
            }
        } else {
            byte[] fieldBytes = new byte[0];
            if ("ASCII".equalsIgnoreCase(format.getFormat())) {
                buffer.put(field.toString().getBytes());
                log.debug("put " + field.toString());
            } else if ("EBCDIC".equalsIgnoreCase(format.getFormat())) {
                buffer.put(Converter.asciiToEbcdic(field.toString().getBytes()));
            } else if ("BCD".equalsIgnoreCase(format.getFormat()) || "HEXA".equalsIgnoreCase(format.getFormat())) {
                buffer.put(Converter.hexStringBytetoByteArray(field.toString()));
            }
            if (format.getName().equalsIgnoreCase("Track 1 Data") || format.getName().equalsIgnoreCase("Track 2 Data")) {
                fieldBytes = Converter.asciiToEbcdic(fieldBytes);
            }
        }
        String lengthIndicator = null;
        if (!"fixed".equalsIgnoreCase(format.getLengthIndicator())) {
            Pattern pattern = Pattern.compile("L+var");
            Matcher matcher = pattern.matcher(format.getLengthIndicator());
            if (matcher.matches()) {
                int counter = 0;
                for (int i = 0; i < format.getLengthIndicator().length(); i++, counter++) {
                    if (format.getLengthIndicator().charAt(i) != 'L') {
                        break;
                    }
                }
                log.debug("buffer position: " + buffer.position());
                if (format.getIndex() == 55 || format.getIndex() == 59 || format.getIndex() == 60 || format.getIndex() == 62 || format.getIndex() == 63 || format.getIndex() == 44 || format.getIndex() == 104 || format.getIndex() == 121 || format.getIndex() == 126) {
                    lengthIndicator = String.format("%" + counter + "s", Integer.toHexString(buffer.position()));
                } else {
                    lengthIndicator = String.format("%" + counter + "s", Integer.toHexString(buffer.position() * 2));
                }
                lengthIndicator = lengthIndicator.replace(' ', '0');
            } else if ("tag55".equalsIgnoreCase(format.getLengthIndicator())) {
                StringBuilder prefix = new StringBuilder();
                prefix.append(format.getName());
                prefix.append(String.format("%02d", field.toString().length() / 2));
                lengthIndicator = prefix.toString();
            }
        }
        byte[] bytes = new byte[buffer.position()];
        buffer.clear();
        buffer.get(bytes);
        if (lengthIndicator != null) {
            ByteBuffer bf = null;
            if ("tag55".equalsIgnoreCase(String.valueOf(format.getIndex()))) {
                bf = ByteBuffer.allocate(lengthIndicator.getBytes().length / 2 + bytes.length);
                bf.put(Converter.hexStringBytetoByteArray(lengthIndicator));
            } else {
                if (lengthIndicator.length() % 2 != 0) {
                    lengthIndicator = '0' + lengthIndicator;
                }
                byte[] b = Converter.hexStringBytetoByteArray(lengthIndicator);
                bf = ByteBuffer.allocate(b.length + bytes.length);
                bf.put(Converter.hexStringBytetoByteArray(lengthIndicator));
            }
            bf.put(bytes);
            bytes = bf.array();
        }
        return bytes;
    }


//    public static String bcdToString(byte[] bcdBytes) {
//        StringBuilder result = new StringBuilder();
//        for (byte b : bcdBytes) {
//            int highNibble = (b & 240) >> 4; // Alto 4 bits
//            int lowNibble = b & 15; // Bajo 4 bits
//            result.append(highNibble).append(lowNibble);
//        }
//        return result.toString();
//    }


//    private ByteBuffer expandBuffer(ByteBuffer originalBuffer, int multiplier) {
//        ByteBuffer expandedBuffer = ByteBuffer.allocate(originalBuffer.capacity() * multiplier);
//        originalBuffer.flip();
//        expandedBuffer.put(originalBuffer);
//        return expandedBuffer;
//    }
//    public static void main(String[] args) {
//        byte[] ebcdicData = new byte[] {(byte) 240, (byte) 245, (byte) 247};
//        byte[] asciiData = Converter.ebcdicToAscii(ebcdicData);
//        System.out.println(new String(asciiData)); // Debería imprimir "1100" o "AGNS" para el campo 55
//    }
//    public static void main(String[] args) {
//        byte[] ebcdicData = new byte[]{(byte) 0xC1, (byte) 0xE7, (byte) 0xC1, (byte) 0xC1};
//        try {
//            String asciiData = new String(ebcdicData, "Cp1047");
//            System.out.println(asciiData);  // Should print "AGNS"
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
}
