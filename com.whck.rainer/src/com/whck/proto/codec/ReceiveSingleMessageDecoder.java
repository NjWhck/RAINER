package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.ReceiveAbstractMessage;
import com.whck.proto.model.ReceiveSingle;

public class ReceiveSingleMessageDecoder extends AbstractMessageDecoder{

	public ReceiveSingleMessageDecoder(byte funcCode) {
		super(funcCode);
	}
	@Override
	protected ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.remaining()<13){
			return null;
		}
		ReceiveSingle m = new ReceiveSingle();
		byte[] rAdr=new byte[2];
		in.get(rAdr);
		m.setRegisterAdr(rAdr);
		byte[] data=new byte[4];
		in.get(data);
		m.setData(data);
		byte[] crc=new byte[2];
		in.get(crc);
		m.setCrc(crc);
		in.get(new byte[5]);
		return m;
	}
}
