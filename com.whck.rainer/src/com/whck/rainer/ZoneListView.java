package com.whck.rainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.whck.proto.model.Constants;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Rainer;
import com.whck.rainer.model.Zone;
import com.whck.rainer.treeviewer.ZoneContentProvider;
import com.whck.rainer.treeviewer.ZoneLabelProvider;
import com.whck.rainer.util.XmlUtil;

public class ZoneListView extends ViewPart {

	private Text txtInput;
	private TreeViewer treeViewer;
	private Menu fMenu;
	private MenuManager fMenuMgr;
	protected ZoneLabelProvider labelProvider;
	private List<Zone> zones;

	List<ISelectionChangedListener> myListeners = new ArrayList<>();

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		txtInput = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		txtInput.setLayoutData(layoutData);
		treeViewer = new TreeViewer(parent);
		treeViewer.getTree().setFont(new Font(Display.getCurrent(),"Î¢ÈíÑÅºÚ",12,SWT.NONE));
		treeViewer.setContentProvider(new ZoneContentProvider());
		labelProvider = new ZoneLabelProvider();
		treeViewer.setLabelProvider(labelProvider);

		hookContextMenu();
		hookListener();
		treeViewer.setUseHashlookup(true);

		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
		getSite().setSelectionProvider(treeViewer);
	}

	public Rainer getInitalInput() {
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Rainer rainer=xmlUtil.getRainer("lab");
		zones=rainer.getZones();
		//@Unportable(project)
		List<Device> devices=zones.get(0).getDevices();
		//initialize the Constants DEVICE_NAMES_IDS
		for(Iterator<Device> it=devices.iterator();it.hasNext();){
			Device device=it.next();
			Constants.DEVICE_NAMES_IDS.put(device.getId(),device.getName());
		}
		return rainer;
	}

	private void hookContextMenu() {
		fMenuMgr = new MenuManager("#PopupMenu");
		fMenuMgr.setRemoveAllWhenShown(true);
		fMenuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
			}
		});
		fMenu = fMenuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(fMenu);
		getSite().registerContextMenu(fMenuMgr, treeViewer);
	}

	protected void hookListener() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
			}
		});
	}

	public void update(String ip ,boolean state){
		for(Iterator<Zone> it=zones.iterator();it.hasNext();){
			Zone zone=it.next();
			List<Device> devices=zone.getDevices();
			for(Iterator<Device> itt=devices.iterator();itt.hasNext();){
				Device device=itt.next();
				if(device.getIp().equals(ip)){
					device.setOnline(true);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							 if (treeViewer != null && !treeViewer.getControl().isDisposed()){
								 treeViewer.refresh(device);
							 }
						}
					});
				}
			}
		}
	}
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
}
