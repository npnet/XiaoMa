package com.xiaoma.service.order.model;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CityBean> {

	public int compare(CityBean o1, CityBean o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return -1;

		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return 1;

		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}
