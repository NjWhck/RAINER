package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.ReceiveAbstractMessage;
import com.whck.proto.model.ReceiveMulti;

public class ReceiveMultiMessageDecoder extends AbstractMessageDecoder{

	public ReceiveMultiMessageDecoder(byte funcCode) {
		super(funcCode);
	}
	@Override
	protected ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.remaining()<11){
			return null;
		}
		ReceiveMulti m = new ReceiveMulti();
		byte[] temp=new byte[2];
		in.get(temp);
		m.setFromPos(temp);
		in.get(temp);
		m.setDataNum(temp);
		byte[] crc=new byte[2];
		in.get(crc);
		m.setCrc(crc);
		in.get(new byte[5]);//Pass Ender
		return m;
	}
}
