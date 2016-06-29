package com.whck.msf;

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
import com.whck.msf.model.Device;
import com.whck.msf.model.Rainer;
import com.whck.msf.model.Zone;
import com.whck.msf.treeviewer.ZoneContentProvider;
import com.whck.msf.treeviewer.ZoneLabelProvider;
import com.whck.msf.util.XmlUtil;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;
import com.whck.proto.message.FlowMessage;
import com.whck.proto.message.MSFMessage;

public class ZoneListView extends ViewPart {

	private Text txtInput;
	private TreeViewer treeViewer;
	private Menu fMenu;
	private MenuManager fMenuMgr;
	protected ZoneLabelProvider labelProvider;
	private static List<Zone> zones;

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
		treeViewer.getTree().setFont(new Font(Display.getCurrent(), "微软雅黑", 12, SWT.NONE));
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

	public static Rainer getInitalInput() {
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Rainer rainer = xmlUtil.getRainer("lab");
		zones = rainer.getZones();
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

	public void update(String ip, boolean state) {
		for (Iterator<Zone> it = zones.iterator(); it.hasNext();) {
			Zone zone = it.next();
			List<Device> devices=zone.getDevices();
			for (Iterator<Device> itt = devices.iterator(); itt.hasNext();) {
				Device device = itt.next();
				if (device.getIp().equals(ip)) {
					device.setOnline(state);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (treeViewer != null && !treeViewer.getControl().isDisposed()) {
								treeViewer.refresh(device);
							}
						}
					});
					break;
				}
			}
		}
	}

	public void initDevIp(String ip, Object msg) {
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (msg instanceof MSFMessage) {
			MSFMessage mm = (MSFMessage) msg;
			MSFData m=mm.convert();
			for(Iterator<Zone> it=zones.iterator();it.hasNext();){
				Zone zone=it.next();
				String zoneName=zone.getName();
				String devName=m.getName();
				int devId=m.getId();
				if(xmlUtil.updateIpByDevNameAndId(zoneName,devName, String.valueOf(devId), ip)){
					List<Device> devices=zone.getDevices();
					for(Iterator<Device> itr=devices.iterator();itr.hasNext();){
						Device dev=itr.next();
						if((dev.getName().equals(devName))&&(dev.getId().equals(String.valueOf(devId)))){
							dev.setIp(ip);
							break;
						}
					}
					break;
				}
			}
		} else if (msg instanceof FlowMessage) {
			FlowMessage fm = (FlowMessage) msg;
			FlowData m=fm.convert();
			for(Iterator<Zone> it=zones.iterator();it.hasNext();){
				Zone zone=it.next();
				String zoneName=zone.getName();
				String devName=m.getName();
				int devId=m.getId();
				if(xmlUtil.updateIpByDevNameAndId(zoneName,devName, String.valueOf(devId), ip)){
					List<Device> devices=zone.getDevices();
					for(Iterator<Device> itr=devices.iterator();itr.hasNext();){
						Device dev=itr.next();
						if((dev.getName().equals(devName))&&(dev.getId().equals(String.valueOf(devId)))){
							dev.setIp(ip);
							break;
						}
					}
					break;
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
