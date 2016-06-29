package com.whck.rainer.model;

import java.util.ArrayList;
import java.util.List;

public class Device extends RModel {
	private boolean triggered; // 联合type字段确定此trigger的作用
	private String id;
	private String name;
	private String ip;
	private int type;
	private List<Variable> variables = new ArrayList<>(0);
	private List<Device> relaDevs = new ArrayList<>(0);
	private boolean online;
	private List<TimeManager> timeMng = new ArrayList<>(0);

	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();

	public Device() {
	}

	public Device(String id, String name, String ip, int type, List<Variable> variables) {
		super();
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.type = type;
		this.variables = variables;
	}

	public void setParent(RModel zone) {
		parent = zone;
	}

	public RModel getParent() {
		return parent;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public List<Device> getRelaDevs() {
		return relaDevs;
	}

	public void setRelaDevs(List<Device> relaDevs) {
		this.relaDevs = relaDevs;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public List<TimeManager> getTimeMng() {
		return timeMng;
	}

	public void setTimeMng(List<TimeManager> timeMng) {
		this.timeMng = timeMng;
	}

	private static class Adder implements IModelVisitor {

		public void visitVariable(Variable variable, Object argument) {
			((Device) argument).addVariable(variable);
		}

		public void visitZone(Zone zone, Object argument) {
		}

		public void visitDevice(Device box, Object argument) {
			// ((Device) argument).addBox(box);
		}

		@Override
		public void visitRainer(Rainer rainer, Object passAlongArgument) {
			// TODO Auto-generated method stub

		}

	}

	private static class Remover implements IModelVisitor {
		@Override
		public void visitZone(Zone zone, Object argument) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visitDevice(Device device, Object argument) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visitVariable(Variable variable, Object argument) {
			((Device) argument).removeVariable(variable);

		}

		@Override
		public void visitRainer(Rainer rainer, Object passAlongArgument) {
			// TODO Auto-generated method stub

		}
	}

	protected void addVariable(Variable variable) {
		variables.add(variable);
		variable.parent = this;
		fireAdd(variable);
	}

	public void remove(RModel toRemove) {
		toRemove.accept(remover, this);
	}

	protected void removeVariable(Variable variable) {
		variables.remove(variable);
		variable.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(variable);
	}

	public void add(RModel toAdd) {
		toAdd.accept(adder, this);
	}

	/**
	 * Answer the total number of items the receiver contains.
	 */
	public int size() {
		return getVariables().size();
	}

	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object argument) {
		visitor.visitDevice(this, argument);
	}

	public String toString() {
		return "(id=" + id + ")" + "(name:" + name + ")" + "(Vars:" + variables.toString() + ")";
	}

}
