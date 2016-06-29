package com.whck.rainer.panel;

import java.util.HashMap;

public class Constants {

	public static HashMap<String, String> cn_en_map = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("雨强", "rainintn");
			put("压强", "pressure");
			put("雨量", "rainfall");
			put("雨型", "raintype");
			put("水位", "wtrlevel");
			put("倒计时", "timedur");
		}
	};
	public static final String[] MODES={"手动模式","雨量模式","定时模式","同步手动","同步雨量","同步定时"};
}
