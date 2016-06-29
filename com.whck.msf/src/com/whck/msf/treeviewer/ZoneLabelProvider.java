package com.whck.msf.treeviewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.whck.msf.model.Device;
import com.whck.msf.model.Variable;
import com.whck.msf.model.Zone;
import com.whck.msf.panel.PictureFactory;

public class ZoneLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		if (element instanceof Zone) {
			return PictureFactory.getImage("zone");
		} else if (element instanceof Device) {
			Device device = (Device) element;
			if (device.isOnline()) {
				return PictureFactory.getImage("device_online");
			} else {
				return PictureFactory.getImage("device_offline");
			}
		} else if (element instanceof Variable) {
			Variable var = (Variable) element;
			if (var.isCtrlable()) {
				return PictureFactory.getImage("variable_ctrl");
			} else {
				return PictureFactory.getImage("variable_show");
			}
		} else {
			throw unknownElement(element);
		}
	}

	public String getText(Object element) {
		if (element instanceof Zone) {
			if (((Zone) element).getName() == null) {
				return "zone";
			} else {
				return ((Zone) element).getName();
			}
		} else if (element instanceof Variable) {
			return ((Variable) element).getName() + "(" + ((Variable) element).getUnit() + ")";
		} else if (element instanceof Device) {
			return ((Device) element).getName() + "(ID:" + ((Device) element).getId() + ")";
		} else {
			throw unknownElement(element);
		}
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
