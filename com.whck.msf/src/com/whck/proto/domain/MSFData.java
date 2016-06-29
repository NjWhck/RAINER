package com.whck.proto.domain;

public class MSFData extends AbstractEntity{
	private String name;
	private int id;
	private String ip;
	private float sedcharge;
	private float sedaccm;
	private float flowaccm;
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
	
	public float getSedcharge() {
		return sedcharge;
	}
	public void setSedcharge(float sedcharge) {
		this.sedcharge = sedcharge;
	}
	public float getSedaccm() {
		return sedaccm;
	}
	public void setSedaccm(float sedaccm) {
		this.sedaccm = sedaccm;
	}
	public float getFlowaccm() {
		return flowaccm;
	}
	public void setFlowaccm(float flowaccm) {
		this.flowaccm = flowaccm;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
	@Override
	public String toString() {
		return "MSFData [name=" + name + ", id=" + id + ", ip=" + ip + ", sedcharge=" + sedcharge + ", sedaccm="
				+ sedaccm + ", flowaccm=" + flowaccm + ", datetime=" + date_time + "]";
	}
}
