package com.whck.proto.model;

import com.whck.rainer.util.CRC16M;

/**
 * 	@author JSexy
 */
public class Send extends SendAbstractMessage{
	private byte[] fromPos;
	private byte[] dataNum;
	
	public Send() {
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



	@Override
	public byte[] appendCrc() {
		
		byte[] total={getAdr(),getFuncCode(),fromPos[0],fromPos[1],dataNum[0],dataNum[1]};
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
		
	}
	
}
