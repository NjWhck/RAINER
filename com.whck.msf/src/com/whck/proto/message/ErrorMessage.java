package com.whck.proto.message;

import com.whck.proto.domain.ErrorData;

public class ErrorMessage extends AbstractMessage{
	public static final int TYPE=9;
	private byte info;
	
	public byte getInfo() {
		return info;
	}

	public void setInfo(byte info) {
		this.info = info;
	}

	@Override
	public boolean checkCRC() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] appendCRC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorData convert() {
		// TODO Auto-generated method stub
		return null;
	}

}
