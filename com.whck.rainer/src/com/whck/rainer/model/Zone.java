package com.whck.rainer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域类
 * 
 * @author Administrator
 *
 */
public class Zone extends RModel {
	private String name;
	private List<Device> devices = new ArrayList<>(); // 区域包含的设备
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();

	public Zone() {
	}

	public Zone(String name, List<Device> devices) {
		this.name = name;
		this.devices = devices;
	}

	public void setParent(RModel rainer) {
		this.parent = rainer;
	}

	public RModel getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	private static class Adder implements IModelVisitor {
		@Override
		public void visitZone(Zone zone, Object argument) {
		}

		@Override
		public void visitDevice(Device device, Object argument) {
			((Zone) argument).addDevice(device);
		}

		@Override
		public void visitVariable(Variable variable, Object argument) {
		}

		@Override
		public void visitRainer(Rainer rainer, Object passAlongArgument) {
			// TODO Auto-generated method stub

		}
	}

	private static class Remover implements IModelVisitor {

		@Override
		public void visitZone(Zone zone, Object argument) {
		}

		@Override
		public void visitDevice(Device device, Object argument) {
			((Zone) argument).removeDevice(device);
		}

		@Override
		public void visitVariable(Variable variable, Object argument) {
		}

		@Override
		public void visitRainer(Rainer rainer, Object passAlongArgument) {
			// TODO Auto-generated method stub

		}
	}

	protected void addDevice(Device device) {
		devices.add(device);
		device.parent = this;
		fireAdd(device);
	}

	protected void removeDevice(Device device) {
		devices.remove(device);
		device.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(device);
	}

	public void add(RModel toAdd) {
		toAdd.accept(adder, this);
	}

	public void remove(RModel toRemove) {
		toRemove.accept(remover, this);
	}

	public String toString() {
		return "{zoneName:" + name + "}" + "{devices:" + devices.toString() + "}";
	}

	public int size() {
		return devices.size();
	}

	@Override
	public void accept(IModelVisitor visitor, Object argument) {
		visitor.visitZone(this, argument);

	}

}
