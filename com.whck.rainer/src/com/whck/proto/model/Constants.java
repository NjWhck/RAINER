package com.whck.proto.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whck.rainer.util.XmlUtil;

public class Constants {
	/**头尾*/
	public static final byte[] HREADER={(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB};
	public static final byte[] ENDER={(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA};
	/** 功能码*/
	public static final byte READ_REGISTER=0x03;
	public static final byte SINGLE_REGISTER=0x06;
	public static final byte MULTI_REGISTER=0x10;
	public static final byte SINGLE_RESPONSE_ERROR=(byte) 0xaa;
	public static final byte MULTI_RESPONSE_ERROR=(byte) 0xbb;
	public static final byte DEFAULT_CODE=0x20;
	
	/** 信息码*/
	public static final byte ILLEGAL_FUNCTION=0X01;
	public static final byte ILLEGAL_REGISTER_POSITION=0X02;
	public static final byte ILLEGAL_DATA_VALUE=0X03;
	public static final byte WRONG_CRC=0X04;
	public static final byte RESPONSE_CORRECT=0X05;
	public static final byte RESPONSE_ERROR=0X06;
	public static final byte ILLEGAL_ARGS=0X07;
	
	/**变量对应的寄存器地址 ,用list index表示寄存器地址*/
	public static List<String> VALUE_IN_REGISTER=new ArrayList<String>(){
		private static final long serialVersionUID = 9214527226864669484L;

		{
			add("mode");
			add("status");
			add("rainintn");
			add("pressure");
			add("rainfall");
			add("timedur");
			add("raintype");
			add("termpress");
			add("dblpress");
			add("trppress");
			add("altrpress");
			add("rappress");
			add("rainfallarg");
		}
	};
	public static Map<String,String> DEVICE_NAMES_IDS=new HashMap<>();
	static {
		XmlUtil xmlUtil=null;
		try {
			xmlUtil=XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> zoneNames=xmlUtil.getZoneNames();
		List<String> devIds=xmlUtil.getDevIds(zoneNames.get(0));
		List<String> devNames=xmlUtil.getDevNames(zoneNames.get(0));
		for(int i=0;i<devIds.size();i++){
			DEVICE_NAMES_IDS.put(devIds.get(i), devNames.get(i));
		}
	}
}
