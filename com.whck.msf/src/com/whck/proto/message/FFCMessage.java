package com.whck.proto.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.whck.msf.util.AlgorithmUtil;
import com.whck.msf.util.CRC16M;
import com.whck.proto.domain.AbstractEntity;
import com.whck.proto.domain.FFCData;
/**
 * 
 * @author JSexy
 * @Description Free Flow Channel
 *
 */
public class FFCMessage extends AbstractMessage{
	public static final String NAME="水位计";
	public static final byte[] HEADER={(byte)0xEF,(byte)0xEF,(byte)0xEF,(byte)0xEF,(byte)0xEF};
	public static final byte[] ENDER={(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE};
	public static Map<String, String> param_map = new HashMap<>();
	static{
		param_map.put("所有", "all");
		param_map.put("流速", "flowrate");
		param_map.put("流量", "flowaccm");
		param_map.put("水位", "waterlvl");
	}
	public static final byte TYPE=0x20;
	private byte[] flowrate;
	private byte[] flowaccm;
	private byte[] waterlvl;

	public byte[] getFlowrate() {
		return flowrate;
	}

	public void setFlowrate(byte[] flowrate) {
		this.flowrate = flowrate;
	}

	public byte[] getFlowaccm() {
		return flowaccm;
	}

	public void setFlowaccm(byte[] flowaccm) {
		this.flowaccm = flowaccm;
	}

	public byte[] getWaterlvl() {
		return waterlvl;
	}

	public void setWaterlvl(byte[] waterlvl) {
		this.waterlvl = waterlvl;
	}

	@Override
	public boolean checkCRC() {
		return true;
	}

	@Override
	public byte[] appendCRC() {
		byte[] data=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(flowrate,flowaccm),waterlvl);
		byte[] prePart=new byte[]{getAdr(),TYPE,(byte) data.length};
		byte[] total=AlgorithmUtil.byteArrsMerger(prePart, data);
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
	}

	@Override
	public AbstractEntity convert() {
		FFCData fd=new FFCData();
		fd.setId(Byte.toUnsignedInt(getAdr()));
		IoBuffer ioBuffer=IoBuffer.wrap(flowrate);
		fd.setFlowaccm(ioBuffer.getFloat());
		ioBuffer=IoBuffer.wrap(flowaccm);
		fd.setFlowrate(ioBuffer.getFloat());
		ioBuffer=IoBuffer.wrap(waterlvl);
		fd.setWaterlvl(ioBuffer.getFloat());
		fd.setDate_time(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
		return fd;
	}

	@Override
	public String toString() {
		IoBuffer ioBuffer=IoBuffer.wrap(flowrate);
		String rslt="Message [flowrate=";
		rslt+=ioBuffer.getFloat()+",flowaccm=";
		ioBuffer=IoBuffer.wrap(flowaccm);
		rslt+=ioBuffer.getFloat()+",waterlvl=";
		ioBuffer=IoBuffer.wrap(waterlvl);
		rslt+=ioBuffer.getFloat()+"]";
		return rslt;
	}

}
