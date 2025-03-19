/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/
package com.eglobal.tools.parser.parsers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.eglobal.tools.parser.pojo.AParsedObject;
import com.eglobal.tools.parser.pojo.Message;

/**
 * @author 6
 */
public class DescAtm34Parser extends JsonLayout implements IDescParser {
	private static DescAtm34Parser __INSTANCE;

	private DescAtm34Parser() {
		init("resources/parsers/descATM3.4.json");
	}

	public static DescAtm34Parser getInstance() {
		if (__INSTANCE == null) {
			__INSTANCE = new DescAtm34Parser();
		}
		return __INSTANCE;
	}

	@Override
	public AParsedObject parse(String data) {
		if (data == null) {
			return null;
		}
		Message message = new Message();
		Set<Entry<String, Map<String, String>>> fields = layout.entrySet();
		for (Map.Entry<String, Map<String, String>> field : fields) {
			Map<String, String> attributes = (Map<String, String>) field.getValue();
			String name = field.getKey();
			int start = Integer.parseInt(attributes.get("START")) - 1;
			int end = Integer.parseInt(attributes.get("END"));
			if (start >= 0 && end <= data.length() && start < end) {
				String value = data.substring(start, end);
				message.setField(name, value);
			} else 
			//System.out.println(name + ": " + value);
			{
				System.out.println("Rango inválido para el campo: " + name);
			}
		}
		return message;
	}

	@Override
	public String format(AParsedObject y) {
		return null;
	}
}
