package com.whck.msf.model;
public interface IDeltaListener {
	public void add(DeltaEvent event);
	public void remove(DeltaEvent event);
}
