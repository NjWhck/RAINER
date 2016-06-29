package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.whck.proto.model.ReceiveAbstractMessage;
import com.whck.proto.model.ReceiveError;
import com.whck.rainer.util.CRC16M;

public class ReceiveErrorMessageDecoder extends AbstractMessageDecoder{

	public ReceiveErrorMessageDecoder(byte funcCode) {
		super(funcCode);
	}
	@Override
	protected ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.remaining()<8){
			return null;
		}
		ReceiveError m = new ReceiveError();
		m.setInfoCode(in.get());
		byte[] crc=new byte[2];
		in.get(crc);
		m.setCrc(crc);
		byte[] temp=new byte[5];
		
		in.get(temp);//Pass ENDER
		System.out.println("whats wrong:----"+CRC16M.getBufHexStr(temp));
		return m;
	}
}
