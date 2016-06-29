package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.whck.proto.message.FlowMessage;

public class FlowMessageEncoder extends AbstractMessageEncoder<FlowMessage>{

	@Override
	protected void encodeBody(IoSession session, FlowMessage message, IoBuffer out) {
		out.put(message.appendCRC());
	}
}
