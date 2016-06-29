package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.message.FFCMessage;

public class FFCMessageEncoder extends AbstractMessageEncoder<FFCMessage>{

	@Override
	protected void encodeBody(IoSession session, FFCMessage message, IoBuffer out) {
		out.put(message.appendCRC());
	}
}
