package com.whck.proto.model;

import com.whck.rainer.util.CRC16M;

public class ReceiveError extends ReceiveAbstractMessage{
	private byte infoCode;
	
	public ReceiveError() {
		super();
	}
	public ReceiveError(byte infoCode) {
		super();
		this.infoCode = infoCode;
	}
	public byte getInfoCode() {
		return infoCode;
	}
	public void setInfoCode(byte infoCode) {
		this.infoCode = infoCode;
	}
	public boolean checkCRC(){
		byte[] data=new byte[5];
		data[0]=getAdr();
		data[1]=getFuncCode();
		data[2]=infoCode;
		data[3]=getCrc()[0];
		data[4]=getCrc()[1];
		
		return CRC16M.checkBuf(data);
	}
}
