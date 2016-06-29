package com.whck.rainer.model;

public class ArgsCache {
	public static int[] rainer_args=new int[5];
	public static boolean latest=false;
	
	public static void refreshArgs(int[] args){		
		rainer_args=args;
		latest=true;
	}
}
