package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.whck.proto.message.MSFMessage;

public class MSFMessageEncoder extends AbstractMessageEncoder<MSFMessage>{

	@Override
	protected void encodeBody(IoSession session, MSFMessage message, IoBuffer out) {
		out.put(message.appendCRC());
	}
}
