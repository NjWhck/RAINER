package com.whck.msf.treeviewer;

import java.util.Iterator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.whck.msf.model.DeltaEvent;
import com.whck.msf.model.Device;
import com.whck.msf.model.IDeltaListener;
import com.whck.msf.model.RModel;
import com.whck.msf.model.Rainer;
import com.whck.msf.model.Variable;
import com.whck.msf.model.Zone;

public class ZoneContentProvider implements ITreeContentProvider, IDeltaListener{
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom(oldInput);
		}
		if(newInput != null) {
			addListenerTo(newInput);
		}
	}
	protected void removeListenerFrom(Object object) {
		if(object instanceof Rainer){
			Rainer rainer=(Rainer)object;
			rainer.removeListener(this);
			for (Iterator<Zone> iterator = rainer.getZones().iterator(); iterator.hasNext();) {
				Zone zone =iterator.next();
				removeListenerFrom(zone);
			}
		}else if(object instanceof Zone){
			Zone zone=(Zone)object;
			zone.removeListener(this);
			for (Iterator<Device> iterator = zone.getDevices().iterator(); iterator.hasNext();) {
				Device device =iterator.next();
				removeListenerFrom(device);
			}
		}else if(object instanceof Device){
			Device device=(Device)object;
			device.removeListener(this);
			for (Iterator<Variable> iterator = device.getVariables().iterator(); iterator.hasNext();) {
				Variable variable = iterator.next();
				removeListenerFrom(variable);
			}
		}
		
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(Object object) {
		if(object instanceof Rainer){
			Rainer rainer=(Rainer)object;
			rainer.addListener(this);
			for (Iterator<Zone> iterator = rainer.getZones().iterator(); iterator.hasNext();) {
				Zone zone =iterator.next();
				addListenerTo(zone);
			}
		}else if(object instanceof Zone){
			Zone zone=(Zone)object;
			zone.addListener(this);
			for (Iterator<Device> iterator = zone.getDevices().iterator(); iterator.hasNext();) {
				Device device =iterator.next();
				addListenerTo(device);
			}
		}else if(object instanceof Device){
			Device device=(Device)object;
			device.addListener(this);
			for (Iterator<Variable> iterator = device.getVariables().iterator(); iterator.hasNext();) {
				Variable variable = iterator.next();
				addListenerTo(variable);
			}
		}
	}
	@Override
	public void add(DeltaEvent event) {
		Object rainer = ((RModel)event.receiver()).getParent();
		viewer.refresh(rainer, false);
	}

	@Override
	public void remove(DeltaEvent event) {
		add(event);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Rainer) {
			Rainer rainer = (Rainer)parentElement;
			return rainer.getZones().toArray();
		}else if(parentElement instanceof Zone) {
			Zone zone = (Zone)parentElement;
			return zone.getDevices().toArray();
		}else if(parentElement instanceof Device){
			Device device=(Device)parentElement;
			return device.getVariables().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof RModel) {
			return ((RModel)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length>0;
	}

}
