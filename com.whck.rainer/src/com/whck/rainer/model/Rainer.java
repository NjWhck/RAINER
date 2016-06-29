package com.whck.rainer.model;

import java.util.List;
public class Rainer extends RModel{
	private String name;
	private List<Zone> zones;	 //区域包含的设备	
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public Rainer(){}
	public Rainer(String name, List<Zone> zones) {
		this.name = name;
		this.zones = zones;
	}
	
	public List<Zone> getZones() {
		return zones;
	}
	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}
	
	private static class Adder implements IModelVisitor {
		@Override
		public void visitZone(Zone zone, Object argument) {
			((Rainer) argument).addZone(zone);
		}

		@Override
		public void visitDevice(Device device, Object argument) {
			
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
			((Rainer) argument).removeZone(zone);
		}

		@Override
		public void visitDevice(Device device, Object argument) {
			
		}

		@Override
		public void visitVariable(Variable variable, Object argument) {
		}

		@Override
		public void visitRainer(Rainer rainer, Object passAlongArgument) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void setParent(RModel parent){
		this.parent=parent;
	}
	protected void addZone(Zone zone) {
		zones.add(zone);
		zone.parent = this;
		fireAdd(zone);
	}
	protected void removeZone(Zone zone) {
		zones.remove(zone);
		zone.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(zone);
	}
	public void add(RModel toAdd) {
		toAdd.accept(adder, this);
	}
	public void remove(RModel toRemove) {
		toRemove.accept(remover, this);
	}
	
	public String toString(){
		return "{zoneName:"+name+"}"+"{devices:"+zones.toString()+"}";
	}

	public int size(){
		return zones.size();
	}
	@Override
	public void accept(IModelVisitor visitor, Object argument) {
		visitor.visitRainer(this, argument);
		
	}
}
