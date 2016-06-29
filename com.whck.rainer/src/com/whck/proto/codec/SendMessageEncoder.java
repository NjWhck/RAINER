package com.whck.proto.codec;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.Send;

public class SendMessageEncoder extends AbstractMessageEncoder<Send>{

	public SendMessageEncoder() {
	}

	@Override
	protected void encodeBody(IoSession session, Send message, IoBuffer out) {
		out.expand(6);
		
		out.put(message.appendCrc());
		byte[] data=message.appendCrc();
		
		
		for(int i=0;i<data.length;i++){
			System.out.println(data[i]);
		}
		System.out.println(Arrays.toString(data));
	}
}
