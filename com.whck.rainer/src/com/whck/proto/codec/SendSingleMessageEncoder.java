package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import com.whck.proto.model.SendSingle;
import com.whck.rainer.util.CRC16M;

public class SendSingleMessageEncoder extends AbstractMessageEncoder<SendSingle>{

	public SendSingleMessageEncoder() {
	}

	@Override
	protected void encodeBody(IoSession session, SendSingle message, IoBuffer out) {
		out.put(message.appendCrc());
		byte[] data=message.appendCrc();
		for(int i=0;i<data.length;i++){
			System.out.println(data[i]);
		}
		System.out.println(CRC16M.getBufHexStr(data));
	}
}
