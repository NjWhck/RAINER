package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.message.AbstractMessage;
import com.whck.proto.message.Constants;
import com.whck.proto.message.FFCMessage;

public class FFCMessageDecoder extends AbstractMessageDecoder{

	public FFCMessageDecoder() {
		super(FFCMessage.HEADER);
	}

	@Override
	protected AbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.hasRemaining()){
			byte dataLen=in.get();
			if(in.remaining()>=dataLen+Constants.CRC_LEN+FFCMessage.ENDER.length){
				FFCMessage m = new FFCMessage();
				byte[] temp=new byte[4];
				in.get(temp);
				m.setFlowrate(temp);
				in.get(temp);
				m.setFlowaccm(temp);
				in.get(temp);
				m.setWaterlvl(temp);
				byte[] crc=new byte[Constants.CRC_LEN];
				in.get(crc);
				m.setCrc(crc);
				in.get(new byte[FFCMessage.ENDER.length]);
				System.out.println(m);
				return m;
			}
		}
		return null;
	}

}
