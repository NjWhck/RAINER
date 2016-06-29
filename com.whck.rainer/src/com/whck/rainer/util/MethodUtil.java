package com.whck.rainer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodUtil {

	public static boolean isValidIp(String ip) {
		System.out.println("ipAdress:"+ip);
		String ipRex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ipRex);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
	public static boolean isNumeric(String str)
    {
          Pattern pattern = Pattern.compile("[0-9]*");
          Matcher isNum = pattern.matcher(str);
          if( !isNum.matches() )
          {
                return false;
          }
          return true;
    }
	public static boolean isValidFileName(String fileName) { 
	    if (fileName == null || fileName.length() > 255) 
	        return false; 
	    else 
	        return fileName.matches( 
	           "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"); 
	}
}
