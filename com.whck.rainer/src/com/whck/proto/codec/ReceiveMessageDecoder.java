package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.whck.proto.model.Receive;
import com.whck.proto.model.ReceiveAbstractMessage;

public class ReceiveMessageDecoder extends AbstractMessageDecoder{

	public ReceiveMessageDecoder(byte funCode) {
		super(funCode);
	}
	@Override
	protected ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.hasRemaining()){
			byte dataLen=in.get();			//按理说dataLen需要无符化处理,此处dataLen不会超过128
			if(in.remaining()>=dataLen+2){
				Receive m = new Receive();
				m.setDataLen(dataLen);
				byte[] data=new byte[dataLen];
				in.get(data);
				m.setData(data);
				byte[] crc=new byte[2];
				in.get(crc);
				m.setCrc(crc);
				in.get(new byte[5]);//Pass Ender
				return m;
			}
		}
		return null;
	}
}
