package com.whck.msf.panel;

import java.util.HashMap;

public class Constants {
	public final static String[] DEFAULT_VALUE={"雨型","风向","水层"};//后续补充
	public final static String[] RAIN_TYPES={"小雨","中雨","大雨","暴雨"};
	public final static String[] WIND_ORIENTIONS_12={"正北","东北偏北","东北偏东","正东",
												"东南偏东","东南偏南","正南","西南偏南",
												"西南偏西","正西","西北偏西","西北偏北"};
	public final static String[] WIND_ORIENTIONS_8={"正北","东北","正东","东南",
													"正南","西南","正西","西北"};
	public final static String[] WIND_ORIENTIONS_4={"正北","正东","正南","正西"};

	public final static String[] PUMP_LEVEL={"上层","中层","底层"};
	
	public static HashMap<String, String> cn_en_map = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("泥沙含量", "sedcharge");
			put("累积含量", "sedaccm");
			put("累积流量", "flowaccm");
			put("流速","flowrate");
			put("水位","waterlvl");
		}
	};
	public static HashMap<String, String> en_cn_map = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("name","姓名");
			put("id","ID");
			put("date_time","日期");
			put( "sedcharge","泥沙含量");
			put( "sedaccm","累积含量");
			put( "flowaccm","累积流量");
			put("flowrate","流速");
			put("waterlvl","水位");
		}
	};
	
}
