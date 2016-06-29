package com.whck.proto.model;

public abstract class ReceiveAbstractMessage {
	private byte adr;
	private byte funcCode;
	private byte[] crc;
	
	public byte getAdr() {
		return adr;
	}
	public void setAdr(byte adr) {
		this.adr = adr;
	}
	public byte getFuncCode() {
		return funcCode;
	}
	public void setFuncCode(byte funcCode) {
		this.funcCode = funcCode;
	}
	
	public byte[] getCrc() {
		return crc;
	}
	public void setCrc(byte[] crc) {
		this.crc = crc;
	}
	public abstract boolean checkCRC();
	
}
