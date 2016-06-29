package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.message.AbstractMessage;
import com.whck.proto.message.Constants;
import com.whck.proto.message.MSFMessage;

public class MSFMessageDecoder extends AbstractMessageDecoder{

	public MSFMessageDecoder() {
		super(MSFMessage.HEADER);
	}

	@Override
	protected AbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.hasRemaining()){
			byte dataLen=in.get();
			if(in.remaining()>=dataLen+Constants.CRC_LEN+MSFMessage.ENDER.length){
				MSFMessage m = new MSFMessage();
				byte[] charge=new byte[4];
				in.get(charge);
				m.setSedcharge(charge);
				byte[] sAccm=new byte[4];
				in.get(sAccm);
				m.setSedaccm(sAccm);
				byte[] fAccm=new byte[4];
				in.get(fAccm);
				m.setFlowaccm(fAccm);
				byte[] crc=new byte[Constants.CRC_LEN];
				in.get(crc);
				m.setCrc(crc);
				in.get(new byte[5]);
				System.out.println("MSFMessage:"+m);
				return m;
			}
		}
		return null;
	}
}
