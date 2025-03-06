package com.eglobal.tools.analytics.category;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eglobal.tools.analytics.category.interfaces.AInterfaceLoader;
import com.eglobal.tools.analytics.category.interfaces.pojo.FieldValue;
import com.eglobal.tools.parser.pojo.Message;

/***
 * @author egldt1029
 */
public interface IMessageCategorizer {

	public String categorize(Message message, String... key);

	public default String categorize(Message message, AInterfaceLoader loader, String... key) throws NullPointerException{
		Logger log = LoggerFactory.getLogger(IMessageCategorizer.class);

		if (message.getMti().charAt(2) % 2 == 0) {
			StringBuilder category = new StringBuilder();
			Map<String, FieldValue> map = loader.getFields();

			for (String s : key) {
				if (message.getField(s) != null) {
					String data = message.getField(s).toString();
					FieldValue fv = map.get(s);
					if (fv.isOnlyPresent()) {
						String name = fv.getName();
						category.append(name);
						category.append("/");
					} else {
						String name = fv.getValues().get(data);
						if (name == null) {
							log.warn(fv.getName() + " " + data + " has no name");
							category.append("Categoria no identificada: ").append(data)
									.append("/");
						} else {
						         category.append(name);
						         category.append("/");
						}
					}
				}
			}
			return category.toString();
		}
		return null;
	}
}
