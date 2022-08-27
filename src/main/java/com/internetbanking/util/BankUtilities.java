package com.internetbanking.util;

import java.util.List;

public class BankUtilities {

	public static boolean isEmptyList(List<?> find) {
		if(find.isEmpty() || find==null)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

}
