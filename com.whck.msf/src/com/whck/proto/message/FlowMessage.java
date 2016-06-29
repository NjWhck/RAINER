package com.whck.proto.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.whck.msf.util.AlgorithmUtil;
import com.whck.msf.util.CRC16M;
import com.whck.proto.domain.FlowData;

public class FlowMessage extends AbstractMessage{
	public static final String NAME="流量计";
	public static final byte[] HEADER={(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,(byte)0xAB,};
	public static final byte[] ENDER={(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA,(byte)0xBA};
	public static Map<String, String> param_map = new HashMap<>();
	static{
		param_map.put("所有", "all");
		param_map.put("流速", "flowrate");
		param_map.put("累积流量", "flowaccm");
	}
	public static final byte TYPE=0x20;
	private byte[] flowrate;
	private byte[] flowaccm;
	
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

	@Override
	public boolean checkCRC() {
		byte[] prePart=new byte[]{getAdr(),TYPE,8};
		byte[] data=AlgorithmUtil.byteArrsMerger(flowrate,flowaccm);
		byte[] total=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(prePart, data), getCrc());
		return CRC16M.checkBuf(total);
	}

	@Override
	public byte[] appendCRC() {
		byte[] data=AlgorithmUtil.byteArrsMerger(flowrate,flowaccm);
		byte[] prePart=new byte[]{getAdr(),TYPE,(byte) data.length};
		byte[] total=AlgorithmUtil.byteArrsMerger(prePart, data);
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
	}

	@Override
	public FlowData convert() {
		FlowData fd=new FlowData();
		fd.setName(NAME);
		fd.setId(Byte.toUnsignedInt(getAdr()));
		IoBuffer ioBuffer=IoBuffer.wrap(flowrate);
		fd.setFlowrate(ioBuffer.getFloat());
		ioBuffer=IoBuffer.wrap(flowaccm);
		fd.setFlowaccm(ioBuffer.getFloat());
		fd.setDate_time(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
		return fd;
	}

	@Override
	public String toString() {
		IoBuffer ioBuffer=IoBuffer.wrap(flowrate);
		String rslt="FlowMessage [flowrate=";
		rslt+=ioBuffer.getFloat()+",flowaccm=";
		ioBuffer=IoBuffer.wrap(flowaccm);
		rslt+=ioBuffer.getFloat()+"]";
		return rslt;
	}
}
