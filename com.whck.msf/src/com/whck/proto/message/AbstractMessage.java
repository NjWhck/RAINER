package com.whck.proto.message;

import com.whck.proto.domain.AbstractEntity;

public abstract class AbstractMessage {
	private byte adr;
	private byte[] crc;
	
	public byte getAdr() {
		return adr;
	}
	public void setAdr(byte adr) {
		this.adr = adr;
	}

	public byte[] getCrc() {
		return crc;
	}
	public void setCrc(byte[] crc) {
		this.crc = crc;
	}
	public abstract boolean checkCRC();
	public abstract byte[] appendCRC();
	public abstract AbstractEntity convert();
}
