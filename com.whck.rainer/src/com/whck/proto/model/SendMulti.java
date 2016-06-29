package com.whck.proto.model;

import com.whck.rainer.util.AlgorithmUtil;
import com.whck.rainer.util.CRC16M;

public class SendMulti extends SendAbstractMessage{
	private byte[] fromPos;
	private byte[] dataNum;
	private byte dataLen;
	private byte[] data;
	
	public SendMulti() {
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
	public byte getDataLen() {
		return dataLen;
	}
	public void setDataLen(byte dataLen) {
		this.dataLen = dataLen;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public byte[] appendCrc() {
		byte[] prePart=new byte[]{getAdr(),getFuncCode(),fromPos[0],fromPos[1],
				dataNum[0],dataNum[1],dataLen};
		byte[] total=AlgorithmUtil.byteArrsMerger(prePart, data);
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
	}
}
	
