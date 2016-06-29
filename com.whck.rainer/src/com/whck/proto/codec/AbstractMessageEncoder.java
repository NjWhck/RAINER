package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import com.whck.proto.model.SendAbstractMessage;

public abstract class AbstractMessageEncoder<T extends SendAbstractMessage> implements MessageEncoder<T> {


    protected AbstractMessageEncoder() {
    }

    public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buf = IoBuffer.allocate(2);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding

        // 编码消息体,由子类实现
        encodeBody(session, message, buf);
        buf.flip();
        out.write(buf);
    }

    // 子类实现编码消息体
    protected abstract void encodeBody(IoSession session, T message, IoBuffer out);
	
}
