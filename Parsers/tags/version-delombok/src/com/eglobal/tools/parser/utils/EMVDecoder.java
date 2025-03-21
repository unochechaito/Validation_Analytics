// Generated by delombok at Mon Feb 10 12:42:16 CST 2025
package com.eglobal.tools.parser.utils;

import com.eglobal.tools.parser.pojo.Tlv;
import java.util.ArrayList;
import java.util.List;

public class EMVDecoder {
    @java.lang.SuppressWarnings("all")
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(EMVDecoder.class);

    public static List<Tlv> decodeHexString(String hexString) {
        java.util.ArrayList<com.eglobal.tools.parser.pojo.Tlv> listTlv = new ArrayList<Tlv>();
        int index = 0;
        while (index < hexString.length()) {
            // Obtiene el tag
            java.lang.String tag = hexString.substring(index, index + 2);
            index += 2;
            if ((Integer.parseInt(tag, 16) & 31) == 31) {
                tag += hexString.substring(index, index + 2);
                index += 2;
            }
            //Tag
            // Obtiene el tamano de la data
            int length = Integer.parseInt(hexString.substring(index, index + 2), 16);
            index += 2;
            // Length
            // Obtiene el valor
            java.lang.String value = hexString.substring(index, index + length * 2);
            index += length * 2;
            log.debug("=================================================================== \nTag: {}, Length: {}, Value: {}", tag, length, value);
            //Value
            listTlv.add(new Tlv(tag, length, value));
        }
        return listTlv;
    }

    @java.lang.SuppressWarnings("all")
    private EMVDecoder() {
    }
}
