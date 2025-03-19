package com.eglobal.tools.parser.parsers;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import com.eglobal.tools.parser.pojo.AParsedObject;

/**
 * 
 * @author egldt1029
 *
 */
public interface IParser {

	public AParsedObject parse(ByteBuffer buffer, Object... objects);

	public default AParsedObject parse(byte[] buffer, Object... objects) {
		ByteBuffer bf = ByteBuffer.wrap(buffer);
		return parse(bf, objects);
	}

	public byte[] format(AParsedObject object);

	default String toBitmap(List<Integer> fieldsPresent) {
		Collections.sort(fieldsPresent);
		StringBuilder bitmap = new StringBuilder();
		StringBuilder bitmapBit = new StringBuilder(String.format("%064d", 0));
		int max = 0;
		for (int i = 0; i < fieldsPresent.size(); i++) {
			if (max < fieldsPresent.get(i)) {
				max = fieldsPresent.get(i);
			}
		}
		if (max > 64) {
			bitmapBit.append(String.format("%064d", 0));
			bitmapBit.setCharAt(0, '1');
		}

		for (Integer field : fieldsPresent) {
			bitmapBit.setCharAt(field - 1, '1');
		}

		for (int i = 0; i < bitmapBit.length(); i += 4) {
			int val = Integer.parseInt(bitmapBit.substring(i, i + 4), 2);
			bitmap.append(Integer.toHexString(val));
		}
		return bitmap.toString().toUpperCase();
	}

}
