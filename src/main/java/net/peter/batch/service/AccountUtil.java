package net.peter.batch.service;
public interface AccountUtil {
	/**
	 * method to format account no.
	 * 
	 * @param value
	 * @return
	 */
	@SuppressWarnings("PMD.AvoidCatchingGenericException")	//legacy code
	static String formatAcctNo(String value) {
		try {
			return value.trim().substring(0, 3).concat("-") + value.substring(3, 4).concat("-") + value.substring(4, 12);

		} catch (Exception e) {
			return value;
		}
	}
}