/**
*   Este codigo fuente es Confidencial y tambien puede contener informacion privilegiada,    *
*   es para uso exclusivo de E-Global. Tenga en cuenta que cualquier distribucion, copia     *
*   o uso de esta informacion sin autorizacion esta estrictamente prohibida. Si ha           *
*   identificado algun mal uso de este codigo fuente por favor notifiquelo a la direccion    *
*   de correo seginfo@eglobal.com.mx                                                         *
**/

package com.eglobal.tools.analytics.matchers;

import com.eglobal.tools.parser.pojo.Message;

/**
 * @author egldt1029
 */
public class MessageMatcher {
	@java.lang.SuppressWarnings("all")
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageMatcher.class);
	String[] fieldsToMatch;

	/**
	 * if message1 and message2 match then return true
	 * 
	 * @return true if both messages match with the fields
	 */
	public boolean match(Message message1, Message message2) {
		if (Math.abs(Integer.parseInt(message2.getMti()) - Integer.parseInt(message1.getMti())) != 10) {
			return false;
		}
		boolean matched = true;
		for (String key : fieldsToMatch) {
			String field1 = message1.getField(key) != null ? message1.getField(key).toString() : null;
			String field2 = message2.getField(key) != null ? message2.getField(key).toString() : null;
			log.debug("field1: " + field1);
			log.debug("field2: " + field2);
			if (field1 != null && !field1.equals(field2)) {
				matched = false;
			}
		}
		log.debug("Messages matched: " + matched);
		return matched;
	}

	@java.lang.SuppressWarnings("all")
	public String[] getFieldsToMatch() {
		return this.fieldsToMatch;
	}

	@java.lang.SuppressWarnings("all")
	public void setFieldsToMatch(final String[] fieldsToMatch) {
		this.fieldsToMatch = fieldsToMatch;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof MessageMatcher)) return false;
		final MessageMatcher other = (MessageMatcher) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (!java.util.Arrays.deepEquals(this.getFieldsToMatch(), other.getFieldsToMatch())) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof MessageMatcher;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + java.util.Arrays.deepHashCode(this.getFieldsToMatch());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "MessageMatcher(fieldsToMatch=" + java.util.Arrays.deepToString(this.getFieldsToMatch()) + ")";
	}

	@java.lang.SuppressWarnings("all")
	public MessageMatcher(final String[] fieldsToMatch) {
		this.fieldsToMatch = fieldsToMatch;
	}
}
