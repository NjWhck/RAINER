package com.whck.proto.model;

public class RainerData extends AbsData{
	private String name;
	private int id;
	private String ip;
	private float rainintn;
	private float pressure;
	private float rainfall;
	private int raintype;
	private String rdate;
	
	public RainerData() {
		super();
	}

	public RainerData(String name, int id, String ip, float rainintn, float pressure, float rainfall, int raintype,
			String rdate) {
		super();
		this.name = name;
		this.id = id;
		this.ip = ip;
		this.rainintn = rainintn;
		this.pressure = pressure;
		this.rainfall = rainfall;
		this.raintype = raintype;
		this.rdate = rdate;
	}

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

	public float getRainintn() {
		return rainintn;
	}

	public void setRainintn(float rainintn) {
		this.rainintn = rainintn;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	public float getRainfall() {
		return rainfall;
	}

	public void setRainfall(float rainfall) {
		this.rainfall = rainfall;
	}

	public int getRaintype() {
		return raintype;
	}

	public void setRaintype(int raintype) {
		this.raintype = raintype;
	}

	public String getRdate() {
		return rdate;
	}

	public void setRdate(String rdate) {
		this.rdate = rdate;
	}

	@Override
	public String toString() {
		return "RainerData [name=" + name + ", id=" + id + ", ip=" + ip + ", rainintn=" + rainintn + ", pressure="
				+ pressure + ", rainfall=" + rainfall + ", raintype=" + raintype + ", rdate=" + rdate + ", getMode()="
				+ getMode() + ", getStatus()=" + getStatus() + ", getTimedur()=" + getTimedur() + ", getYear()="
				+ getYear() + ", getMonth()=" + getMonth() + ", getDay()=" + getDay() + ", getHour()=" + getHour()
				+ ", getMinute()=" + getMinute() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	
}
