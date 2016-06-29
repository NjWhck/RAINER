package com.whck.proto.model;

import com.whck.rainer.util.AlgorithmUtil;
import com.whck.rainer.util.CRC16M;

public class Receive extends ReceiveAbstractMessage{
	private byte dataLen;
	private byte[] data;  	//按照协议此字节数组的长度应该是4的倍数，等于dataLen
	
	public Receive() {
		super();
	}

	public Receive(byte dataLen, byte[] data) {
		super();
		this.dataLen = dataLen;
		this.data = data;
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

	public boolean checkCRC(){
		byte[] prePart=new byte[3];
		prePart[0]=getAdr();
		prePart[1]=getFuncCode();
		prePart[2]=dataLen;
		byte[] total=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(prePart, data), getCrc());
		return CRC16M.checkBuf(total);
	}

}
