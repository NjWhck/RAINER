package com.whck.rainer.model;

public class DynamicLimit {
	private int dUpLimit;
	private int dDownLimit;
	
	@Override
	public String toString() {
		return "DynamicLimit [dUpLimit=" + dUpLimit + ", dDownLimit=" + dDownLimit + "]";
	}
	public DynamicLimit() {
		super();
	}
	public DynamicLimit(int dUpLimit, int dDownLimit) {
		super();
		this.dUpLimit = dUpLimit;
		this.dDownLimit = dDownLimit;
	}
	public int getdUpLimit() {
		return dUpLimit;
	}
	public void setdUpLimit(int dUpLimit) {
		this.dUpLimit = dUpLimit;
	}
	public int getdDownLimit() {
		return dDownLimit;
	}
	public void setdDownLimit(int dDownLimit) {
		this.dDownLimit = dDownLimit;
	}
	
	
}
