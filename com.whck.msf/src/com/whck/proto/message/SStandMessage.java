package com.whck.proto.message;

import com.whck.proto.domain.AbstractEntity;

/**
 * 
 * @author JSexy
 * @Description Swan Stand
 */
public class SStandMessage extends AbstractMessage{
	public static final byte TYPE=0x40;

	@Override
	public boolean checkCRC() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public byte[] appendCRC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractEntity convert() {
		// TODO Auto-generated method stub
		return null;
	}
}
