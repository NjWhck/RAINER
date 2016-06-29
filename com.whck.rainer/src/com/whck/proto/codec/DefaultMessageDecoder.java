package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.DefaultMessage;
import com.whck.proto.model.ReceiveAbstractMessage;

public class DefaultMessageDecoder extends AbstractMessageDecoder{

	public DefaultMessageDecoder(byte funcCode) {
		super(funcCode);
	}
	@Override
	protected ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.hasRemaining()){
			byte dataLen=in.get();			//按理说dataLen需要无符化处理,此处dataLen不会超过128
			if(in.remaining()>=dataLen+2){
				DefaultMessage m = new DefaultMessage();
				m.setDataLen(dataLen);
				byte[] data=new byte[dataLen];
				in.get(data);
				m.setData(data);
				byte[] crc=new byte[2];
				in.get(crc);
				m.setCrc(crc);
				in.get(new byte[5]);//Pass ENDER
				System.out.println("时间："+data[20]+","+data[21]+","+data[22]+","+data[23]);
				return m;
			}
		}
		return null;
	}
}
