package com.eglobal.tools.analytics.category;

import com.eglobal.tools.analytics.category.interfaces.VisaLoader;
import com.eglobal.tools.parser.pojo.Message;

/**
 * @author egldt1029
 */
public class VisaCategorizer implements IMessageCategorizer {
	private String[] defaultKey;

	public VisaCategorizer() {
		this(new String[] { "MTI", "3.1", "22.1", "70" });
	}

	public VisaCategorizer(String... key) {
		this.defaultKey = key;
	}

	public String categorize(Message message, String... key) {
		if (key != null && key.length != 0) {
			return categorize(message, VisaLoader.getInstance(), key);
		}
		return categorize(message, VisaLoader.getInstance(), defaultKey);
	}
}
