/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.parser.parsers;

import com.eglobal.tools.parser.report.LogFieldParserListener;
import com.eglobal.tools.parser.report.observer.EventManager;
import com.eglobal.tools.parser.report.observer.EventType;

/**
 * @author egldt1029
 */
public class ParserFactory {

    static EventManager eventManager;

    public static enum Type {
        ISO, ISO_ATM, MASTERCARD, VISA, DISCOVER, AMEX_TPVS, TPV_BBVA, STRATUS, DESC_POS, ADD1, JCB
    }
    static {
        eventManager = new EventManager(EventType.PARSE_FIELD_PROBLEM);
        eventManager.suscribe(EventType.PARSE_FIELD_PROBLEM, new LogFieldParserListener());
    }

    public static IParser getParser(Type type) {
        IParser parser = null;
        if (type != null) {
            switch (type) {
                case ISO:
                    parser = IsoParser.getInstance();
                    break;
                case ISO_ATM:
                    parser = IsoATMParser.getInstance();
                    break;
                case MASTERCARD:
                    parser = MastercardParser.getInstance();
                    break;
                case VISA:
                    parser = VisaParser.getInstance();
                    break;
                case DISCOVER:
                    parser = DiscoverIsoParser.getInstance(eventManager);
                    break;
                case AMEX_TPVS:
                    parser = AmexParser.getInstance();
                    break;
                case TPV_BBVA:
                    parser = TpvBbvaParser.getInstance();
                    break;
                case STRATUS:
                    parser = StratusParser.getInstance();
                    break;
                case JCB:
                    parser = JCBParserOne.getInstance();
                    break;
                case DESC_POS:
                    parser = LayoutParserFactory.getLayoutParser(LayoutParserFactory.Type.DESC_BASE);
                    break;
                case ADD1:
                    parser = LayoutParserFactory.getLayoutParser(LayoutParserFactory.Type.ADD1);
                    break;
//        case "DESC_ATM":
//           parser = DescAtm34Parser.getInstance();
//           break;
                default:
                    break;
            }
        }
        return parser;
    }


    public static IDataParser<?, ?> getDataParser(String type) {
        IDataParser<?, ?> parser = null;
        if (type != null) {
            type = type.toUpperCase();
            switch (type) {
                case "DESC ATM 4.5":
                case "DESC ATM 3.4":
                    parser = DescAtm34Parser.getInstance();
                    break;
                default:
                    break;
            }
        }
        return parser;
    }


    /**
     * Returns the suitable parser
     */
    public static IParser getSuitableParser(String cad) {
        IParser parser = null;
        if (cad.startsWith("ISO")) {
            parser = IsoATMParser.getInstance();
        } else if (cad.startsWith("1601")) {
            parser = VisaParser.getInstance();
        } else if (cad.startsWith("F0")) {
            parser = MastercardParser.getInstance();
        }
        return parser;
    }


}