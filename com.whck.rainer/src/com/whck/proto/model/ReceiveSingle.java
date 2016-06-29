package com.whck.proto.model;

import com.whck.rainer.util.CRC16M;

public class ReceiveSingle extends ReceiveAbstractMessage{
	private byte[] registerAdr;
	private byte[] data;
	
	public ReceiveSingle() {
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
	public boolean checkCRC() {
		byte[] total=new byte[]{getAdr(),getFuncCode(),
								registerAdr[0],registerAdr[1],
								data[0],data[1],data[2],data[3],
								getCrc()[0],getCrc()[0]};
		return CRC16M.checkBuf(total);
	}
	
}
