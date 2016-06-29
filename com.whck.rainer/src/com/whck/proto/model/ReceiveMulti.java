package com.whck.proto.model;

import com.whck.rainer.util.CRC16M;

public class ReceiveMulti extends ReceiveAbstractMessage{
	private byte[] fromPos;
	private byte[] dataNum;
	
	public ReceiveMulti() {
		super();
	}
	
	public byte[] getFromPos() {
		return fromPos;
	}

	public void setFromPos(byte[] fromPos) {
		this.fromPos = fromPos;
	}

	public byte[] getDataNum() {
		return dataNum;
	}

	public void setDataNum(byte[] dataNum) {
		this.dataNum = dataNum;
	}

	public boolean checkCRC(){
		byte[] total=new byte[]{getAdr(),getFuncCode(),
									fromPos[0],fromPos[1],
									dataNum[0],dataNum[1],
									getCrc()[0],getCrc()[1]};
		return CRC16M.checkBuf(total);
	}
}
