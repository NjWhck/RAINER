package com.whck.proto.domain;

public class FFCData extends AbstractEntity{
	private String name;
	private int id;
	private String ip;
	private float flowrate;
	private float flowaccm;
	private float waterlvl;
	private String date_time;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public float getFlowrate() {
		return flowrate;
	}
	public void setFlowrate(float flowrate) {
		this.flowrate = flowrate;
	}
	public float getFlowaccm() {
		return flowaccm;
	}
	public void setFlowaccm(float flowaccm) {
		this.flowaccm = flowaccm;
	}
	public float getWaterlvl() {
		return waterlvl;
	}
	public void setWaterlvl(float waterlvl) {
		this.waterlvl = waterlvl;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
	@Override
	public String toString() {
		return "FFCData [name=" + name + ", id=" + id + ", ip=" + ip + ", flowrate=" + flowrate + ", flowaccm="
				+ flowaccm + ", waterlvl=" + waterlvl + ", datetime=" + date_time + "]";
	}
	
}
