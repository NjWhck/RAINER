package com.whck.proto.message;

public class Constants {
//	public static final byte[] HEADER={(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB};
//	public static final byte[] ENDER={(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA};
	public static final int CRC_LEN=2;
	public static final int BYTELEN_OF_DATALEN=1;
	public static final int ADDRESS_LEN=1;
	public static final int TYPE_LEN=1;
	public static final int BASE_MSG_LENGTH=5+ADDRESS_LEN+TYPE_LEN+BYTELEN_OF_DATALEN+CRC_LEN+5;
}
