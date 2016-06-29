package com.whck.proto.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import com.whck.proto.message.AbstractMessage;
import com.whck.proto.message.Constants;

public abstract class AbstractMessageEncoder<T extends AbstractMessage> implements MessageEncoder<T> {

    protected AbstractMessageEncoder() {
    }

    public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buf = IoBuffer.allocate(Constants.BASE_MSG_LENGTH);
        buf.setAutoExpand(true); 
        encodeBody(session, message, buf);
        buf.flip();
        out.write(buf);
    }

    protected abstract void encodeBody(IoSession session, T message, IoBuffer out);
	
}
