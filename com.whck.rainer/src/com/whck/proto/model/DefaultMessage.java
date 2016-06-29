package com.whck.proto.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.whck.rainer.model.ArgsCache;
import com.whck.rainer.util.AlgorithmUtil;
import com.whck.rainer.util.CRC16M;

public class DefaultMessage extends ReceiveAbstractMessage{
	private byte dataLen;
	//mode|rainintn|pressure|rainfall|time|raintype|termpress|dblpress|trplpress|altrlpress|rmppress|rainfallarg|reserve
	private byte[] data;
	
	public DefaultMessage() {
		super();
	}
	public byte getDataLen() {
		return dataLen;
	}
	public void setDataLen(byte dataLen) {
		this.dataLen = dataLen;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int generateCRC(){
		int crcRslt=0;
		return crcRslt;
	}
	@Override
	public boolean checkCRC() {
		byte[] prePart=new byte[]{getAdr(),getFuncCode(),getDataLen()};
		byte[] total=AlgorithmUtil.byteArrsMerger(AlgorithmUtil.byteArrsMerger(prePart, data), getCrc());
		
		return CRC16M.checkBuf(total);
	}
	
	public RainerData convert(){
		RainerData rData=new RainerData();
		int devId=(int)this.getAdr();
		System.out.println("data:"+CRC16M.getBufHexStr(data));
		rData.setId(devId);
		rData.setName(Constants.DEVICE_NAMES_IDS.get(String.valueOf(devId)));
		int[] args=new int[12];
		for(int i=0;i<12;i++){
			args[i]=AlgorithmUtil.byteArr2Int(Arrays.copyOfRange(data, i*4, (i+1)*4));
		}
		rData.setMode(args[0]);				//模式
		rData.setStatus(args[1]);			//状态
		rData.setRainintn(args[2]/10.0f); 	//雨强
		rData.setPressure(args[3]);			//压强
		rData.setRainfall(args[4]/10.0f);	//雨量
		rData.setTimedur(args[5]);			//时间
		rData.setRaintype(args[6]);			//雨型
		rData.setRdate(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
	
		ArgsCache.refreshArgs(Arrays.copyOfRange(args, 7,12));
		System.out.println("args:"+args[7]+","+args[8]+","+args[9]+","+args[10]+","+args[11]);
		return rData;
	}
}
