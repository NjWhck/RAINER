package com.whck.proto.codec;

import java.util.Arrays;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import com.whck.proto.message.AbstractMessage;

public abstract class AbstractMessageDecoder implements MessageDecoder {
	
	private final byte[] HEADER;
	private  byte type;
	private byte address;
	private boolean readHeader;

	protected AbstractMessageDecoder(byte[] HEADER) {
		this.HEADER = HEADER;
	}

	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		System.out.println(in);
		if (in.remaining() <HEADER.length+2) {
			return MessageDecoderResult.NEED_DATA;
		}

		byte[] header=new byte[HEADER.length];
		in.get(header);
		if(Arrays.equals(header,HEADER)){
			address = in.get();
			type = in.get();
			return MessageDecoderResult.OK;
		}
		return MessageDecoderResult.NOT_OK;
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// Try to skip header if not read.
		if (!readHeader) {
			in.get(new byte[HEADER.length]);//Skip 'Header'
			in.get(); // Skip 'Address'.
			in.get(); // Skip 'type'.
			readHeader = true;
		}

		AbstractMessage m = decodeBody(session, in);
		if (m == null) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			readHeader = false;
		}
		m.setAdr(address);
		out.write(m);

		return MessageDecoderResult.OK;
	}

	/**
	 * @return <tt>null</tt> if the whole body is not read yet
	 */
	protected abstract AbstractMessage decodeBody(IoSession session, IoBuffer in);

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
	}

}
