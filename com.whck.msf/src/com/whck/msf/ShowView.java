package com.whck.msf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import com.whck.msf.model.Device;
import com.whck.msf.model.Zone;
import com.whck.msf.panel.PictureFactory;
import com.whck.msf.panel.SoHard;
import com.whck.msf.util.XmlUtil;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;
import com.whck.proto.message.FlowMessage;
import com.whck.proto.message.MSFMessage;

public class ShowView extends ViewPart implements ISelectionListener {

	private CTabFolder tabFolder;
	private Map<String, CTabItem> tabItems = new HashMap<>(0);
	public static Map<String, Map<String, SoHard>> sPanels = new HashMap<>(0);
	public static List<String> zoneNames = new ArrayList<>();

	static {
		try {
			XmlUtil xmlUtil = XmlUtil.getInstance();
			zoneNames = xmlUtil.getZoneNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void refresh(){
		try {
			XmlUtil xmlUtil = XmlUtil.getInstance();
			zoneNames = xmlUtil.getZoneNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.CLOSE);
		tabFolder.setLayout(new FillLayout());
		getSite().getPage().addSelectionListener("com.whck.msf.zonelistview", (ISelectionListener) this);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		PictureFactory.dispose();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		Object obj = structuredSelection.getFirstElement();
		if (obj instanceof Zone) {
			Zone zone = (Zone) obj;
			String tabName = zone.getName();
			if (!tabItems.containsKey(tabName)) {
				CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
				Composite tabCom = new Composite(tabFolder, SWT.NONE);
				tabCom.setLayout(new FillLayout());
				tabItem.setText(tabName);
				tabItem.setControl(tabCom);
				tabItems.put(tabName, tabItem);
				final ScrolledComposite sc = new ScrolledComposite(tabCom, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
				final Composite contentCom = new Composite(sc, SWT.NONE);
				RowLayout layout = new RowLayout();
				layout.marginTop = 70;
				layout.marginLeft=80;
				layout.spacing = 20;
				layout.justify = true;
				contentCom.setLayout(layout);
				sc.setContent(contentCom);
				sc.setAlwaysShowScrollBars(true);
				List<Device> devices = new ArrayList<>(0);
				Map<String, SoHard> panels = new IdentityHashMap<>();
				for (Iterator<Device> it = zone.getDevices().iterator(); it.hasNext();) {
					Device device = it.next();
					devices.add(device);
					SoHard sPanel = new SoHard(contentCom, SWT.NONE, device);
					panels.put(device.getName(), sPanel);
					contentCom.setSize(contentCom.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				}
				sPanels.put(tabName, panels);

				tabItem.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent e) {
						tabItems.remove(tabName);
						sPanels.remove(tabName);
					}
				});
			}
		}
	}

	public void update(String ip, Object msg) {
		String typeName = "";
		if (msg instanceof MSFData) {
			typeName = MSFMessage.NAME;
		} else if (msg instanceof FlowData) {
			typeName = FlowMessage.NAME;
		}
		for (Iterator<String> it = zoneNames.iterator(); it.hasNext();) {
			String zoneName = it.next();
			System.out.println("zoneName:"+zoneName);
			Map<String, SoHard> target = sPanels.get(zoneName);
			if (target != null)
				for (Map.Entry<String, SoHard> entry : target.entrySet()) {
					String name = entry.getKey();
					System.out.println("devName:"+name);
					SoHard panel = entry.getValue();
					if (name.equals(typeName)) {
						panel.update(ip, msg);
					}
				}
		}
	}

	class FillTab extends Thread {
		Composite tabCom;
		Zone zone;

		FillTab(Composite tabCom, Zone zone) {
			this.tabCom = tabCom;
			this.zone = zone;
		}

		@Override
		public void run() {
			List<Device> devices = new ArrayList<>(0);
			Map<String, SoHard> panels = new IdentityHashMap<>();
			for (Iterator<Device> it = zone.getDevices().iterator(); it.hasNext();) {
				Device device = it.next();
				devices.add(device);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						SoHard sPanel = new SoHard(tabCom, SWT.NONE, device);
						panels.put(device.getName(), sPanel);
					}
				});
			}
			sPanels.put(zone.getName(), panels);
		}
	}
}
