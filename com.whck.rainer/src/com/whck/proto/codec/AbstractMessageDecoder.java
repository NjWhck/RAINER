package com.whck.proto.codec;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.whck.proto.model.Constants;
import com.whck.proto.model.ReceiveAbstractMessage;

public abstract class AbstractMessageDecoder implements MessageDecoder {

	private byte adr;

	private final byte funcCode;

	private boolean readHeader;

	protected AbstractMessageDecoder(byte funcCode) {
		this.funcCode = funcCode;
	}

	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// Return NEED_DATA if the whole header is not read yet.
		if (in.remaining() < Constants.HREADER.length) {
			return MessageDecoderResult.NEED_DATA;
		}

		// Return OK if type and bodyLength matches.
		byte[] header=new byte[Constants.HREADER.length];
		in.get(header);
		if(Arrays.equals(header, Constants.HREADER)){
			adr = in.get();
			if (funcCode == in.get()) {
				return MessageDecoderResult.OK;
			}
		}
		// Return NOT_OK if not matches.
		return MessageDecoderResult.NOT_OK;
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// Try to skip header if not read.
		if (!readHeader) {
			in.get(new byte[Constants.HREADER.length]);//Skip 'Header'
			in.get(); // Skip 'adr'.
			in.get(); // Skip 'funCode'.
			readHeader = true;
		}

		// Try to decode body
		ReceiveAbstractMessage m = decodeBody(session, in);
		// Return NEED_DATA if the body is not fully read.
		if (m == null) {
			return MessageDecoderResult.NEED_DATA;
		} else {
			readHeader = false; // reset readHeader for the next decode
		}
		m.setAdr(adr);
		m.setFuncCode(funcCode);
		out.write(m);

		return MessageDecoderResult.OK;
	}

	/**
	 * @return <tt>null</tt> if the whole body is not read yet
	 */
	protected abstract ReceiveAbstractMessage decodeBody(IoSession session, IoBuffer in);

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
	}

}
