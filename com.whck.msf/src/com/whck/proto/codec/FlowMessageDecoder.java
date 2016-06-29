package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.message.AbstractMessage;
import com.whck.proto.message.Constants;
import com.whck.proto.message.FlowMessage;

public class FlowMessageDecoder extends AbstractMessageDecoder{

	public FlowMessageDecoder() {
		super(FlowMessage.HEADER);
	}

	@Override
	protected AbstractMessage decodeBody(IoSession session, IoBuffer in) {
		if(in.hasRemaining()){
			byte dataLen=in.get();
			if(in.remaining()>=dataLen+Constants.CRC_LEN+FlowMessage.ENDER.length){
				FlowMessage m = new FlowMessage();
				byte[] rate=new byte[4];
				in.get(rate);
				m.setFlowrate(rate);
				byte[] accm=new byte[4];
				in.get(accm);
				m.setFlowaccm(accm);
				byte[] crc=new byte[Constants.CRC_LEN];
				in.get(crc);
				m.setCrc(crc);
				in.get(new byte[FlowMessage.ENDER.length]);
				System.out.println("FlowMessage:"+m);
				return m;
				
			}
		}
		return null;
	}

}
