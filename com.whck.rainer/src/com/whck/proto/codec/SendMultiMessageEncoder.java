package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.SendMulti;

public class SendMultiMessageEncoder extends AbstractMessageEncoder<SendMulti> {

	public SendMultiMessageEncoder() {
	}

	@Override
	protected void encodeBody(IoSession session, SendMulti message, IoBuffer out) {
		byte[] data = message.appendCrc();
		out.put(data);
	}
}
