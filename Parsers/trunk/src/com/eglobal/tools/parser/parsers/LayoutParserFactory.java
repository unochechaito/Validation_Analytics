/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

/**
 * @author egldt1029
 */
public class LayoutParserFactory {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LayoutParserFactory.class);

	private LayoutParserFactory() {
	}


	public enum Type {
		DESC_BASE, ADD1, ADD2, ADD3, ADD4, ADD5, ADD6, ADD7, ADD8, ADD9, ADD10, ADD11, ADD12, ADD13, ADD14, ADD15, ADD16, ADD17, ADD18, ADD19, ADD20, ADD21, ADD22, ADD23, ADD24, ADD25, ADD26, ADD27, ADD28, ADD29, ADD30, ADD31, ADD32, ADD33, ADD34, ADD35, ADD36, ADD37, ADD38, ADD39, ADD40, ADD41, ADD42, ADD43, ADD44, ADD45, ADD46, ADD22_1, ADD22_2;

		public String getFileName() {
			if (this.name().startsWith("A")) return super.name().charAt(0) + super.name().substring(1).toLowerCase();
			 else return super.name();
		}

		public static Type getAddType(int num) {
			return Type.valueOf("ADD" + num);
		}
	}

	public static Type getTypeForname(String type) {
		int index = Integer.parseInt(type);
		return Type.values()[index];
	}

	public static LayoutParser getLayoutParser(Type type) {
		if (type == null) {
			return null;
		}
		LayoutParser parser = null;
		if (Type.DESC_BASE.equals(type)) {
			parser = new LayoutParser("resources/parsers/fieldDescBase.json");
		} else {
			parser = new LayoutParser("resources/parsers/fieldDesc" + type.getFileName() + ".json");
		}
		return parser;
	}
}
