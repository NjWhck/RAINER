package com.whck.proto.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.whck.msf.util.AlgorithmUtil;
import com.whck.msf.util.CRC16M;
import com.whck.proto.domain.MSFData;
/**
 * 
 * @author JSexy
 * @Description Mud_Sand Flow 泥沙流测量
 */
public class MSFMessage extends AbstractMessage{
	public static final String NAME="泥沙测量仪";
	public static final byte[] HEADER={(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD,(byte)0xCD};
	public static final byte[] ENDER={(byte)0xDC,(byte)0xDC,(byte)0xDC,(byte)0xDC,(byte)0xDC};
	public static Map<String, String> param_map = new HashMap<>();
	static{
		param_map.put("所有", "all");
		param_map.put("泥沙含量", "sedcharge");
		param_map.put("累积含量", "sedaccm");
		param_map.put("累积流量", "flowaccm");
	}
	
	public static final byte TYPE=0x20;
	private byte[] sedcharge; 	//泥沙含量 
	private byte[] sedaccm;	 	//累积含量
	private byte[] flowaccm;	//累积流量
	
	public byte[] getSedcharge() {
		return sedcharge;
	}

	public void setSedcharge(byte[] sedcharge) {
		this.sedcharge = sedcharge;
	}

	public byte[] getSedaccm() {
		return sedaccm;
	}

	public void setSedaccm(byte[] sedaccm) {
		this.sedaccm = sedaccm;
	}

	public byte[] getFlowaccm() {
		return flowaccm;
	}

	public void setFlowaccm(byte[] flowaccm) {
		this.flowaccm = flowaccm;
	}

	public boolean checkCRC() {
		byte[] prePart=new byte[]{getAdr(),TYPE,12};
		byte[] data=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(sedcharge,sedaccm),flowaccm);
		byte[] total=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(prePart, data), getCrc());
		return CRC16M.checkBuf(total);
	}

	public byte[] appendCRC() {
		byte[] data=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(sedcharge,sedaccm),flowaccm);
		byte[] prePart=new byte[]{getAdr(),TYPE,(byte) data.length};
		byte[] total=AlgorithmUtil.byteArrsMerger(prePart, data);
		return CRC16M.getSendBuf(CRC16M.getBufHexStr(total));
	}

	public MSFData convert() {
		MSFData md=new MSFData();
		md.setName(NAME);
		md.setId(Byte.toUnsignedInt(getAdr()));
		IoBuffer ioBuffer=IoBuffer.wrap(sedcharge);
		md.setSedcharge(ioBuffer.getFloat());
		ioBuffer=IoBuffer.wrap(flowaccm);
		md.setFlowaccm(ioBuffer.getFloat());
		ioBuffer=IoBuffer.wrap(sedaccm);
		md.setSedaccm(ioBuffer.getFloat());
		md.setDate_time(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
		return md;
	}

	@Override
	public String toString() {
		IoBuffer ioBuffer=IoBuffer.wrap(sedcharge);
		String rslt="Message [sedcharge=";
		rslt+=ioBuffer.getFloat()+",sedaccm=";
		ioBuffer=IoBuffer.wrap(sedaccm);
		rslt+=ioBuffer.getFloat()+",flowaccm=";
		ioBuffer=IoBuffer.wrap(flowaccm);
		rslt+=ioBuffer.getFloat()+"]";
		return rslt;
	}
	
}
