package com.whck.rainer.model;

import java.util.Date;

/**
 * 时间段管理
 * 
 * @author 江心
 *
 */
public class TimeManager {
	private boolean triggered;
	private Date fromDate;
	private Date toDate;
	private int runningDur;
	private int idleDur;

	@Override
	public String toString() {
		return "TimeManager [fromDate=" + fromDate + ", toDate=" + toDate + ", runningDur=" + runningDur + ", idleDur="
				+ idleDur + "]";
	}

	public TimeManager() {
		super();
	}

	public TimeManager(Date fromDate, Date toDate, int runningDur, int idleDur) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.runningDur = runningDur;
		this.idleDur = idleDur;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getRunningDur() {
		return runningDur;
	}

	public void setRunningDur(int runningDur) {
		this.runningDur = runningDur;
	}

	public int getIdleDur() {
		return idleDur;
	}

	public void setIdleDur(int idleDur) {
		this.idleDur = idleDur;
	}

}
