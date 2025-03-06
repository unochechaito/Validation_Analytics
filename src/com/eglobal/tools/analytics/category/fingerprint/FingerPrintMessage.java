package com.eglobal.tools.analytics.category.fingerprint;

import java.util.ArrayList;
import java.util.List;

import com.eglobal.tools.analytics.category.IMessageCategorizer;
import com.eglobal.tools.analytics.category.MessageCategorizerFactory;
import com.eglobal.tools.parser.pojo.Message;

public class FingerPrintMessage {
	public static String calculateFingerPrint(Message message) {
		IMessageCategorizer categorizer = MessageCategorizerFactory.getCategorizer(message.getType());
		
		return calculateFingerPrint(message, categorizer);
	}
	
	public static String calculateFingerPrint(Message message, IMessageCategorizer categorizer) {
		StringBuilder fingerprint = new StringBuilder();
		fingerprint.append(categorizer.categorize(message)); //1st category
		
		List<Integer> fieldsPresent = getFieldsOrdered(message);
		for (Integer field : fieldsPresent) {
			if(field != 1 && field != 64) {
				fingerprint.append(field);
				fingerprint.append("/");
			}
		}	
		//TODO Version preliminar, este codigo debe ser mejorado
		if("ISO_ATM".equalsIgnoreCase(message.getType())) {
			String[] tokens = new String[] {"25", "30", "B1", "B2", "B3", "B4", "B5", "B6", "QM"};
			for (String tk : tokens) {
				String fieldName = "126." + tk;
				if(message.getField(fieldName) != null) {
					fingerprint.append(fieldName);
					fingerprint.append("/");
				}
			}
		}
		return fingerprint.toString();
	}
	
	private static List<Integer> getFieldsOrdered(Message message) {
		List<Integer> list = new ArrayList<>();
		for(int i=1; i<128; i++) {
			if(message.getField("" + i) != null ) {
				list.add(i);
			}
		}
		return list;
	}
}
