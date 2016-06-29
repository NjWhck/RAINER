package com.whck.proto.model;

import com.whck.rainer.util.CRC16M;

public class SendSingle extends SendAbstractMessage {
	private byte[] registerAdr;
	private byte[] data;

	public SendSingle() {
		super();
	}

	public byte[] getRegisterAdr() {
		return registerAdr;
	}

	public void setRegisterAdr(byte[] registerAdr) {
		this.registerAdr = registerAdr;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public byte[] appendCrc() {
		byte[] total = { getAdr(), getFuncCode(), registerAdr[0], registerAdr[1], data[0], data[1], data[2], data[3] };
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
	}
}
