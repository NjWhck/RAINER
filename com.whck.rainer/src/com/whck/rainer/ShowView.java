package com.whck.rainer;

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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Zone;
import com.whck.rainer.panel.PictureFactory;
import com.whck.rainer.panel.SoHard;
import com.whck.rainer.util.XmlUtil;

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

	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.CLOSE);
		tabFolder.setLayout(new RowLayout());
		getSite().getPage().addSelectionListener("com.whck.rainer.zonelistview", (ISelectionListener) this);
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
				RowLayout layout = new RowLayout();
				layout.marginTop = 20;
				layout.justify = true;
				tabCom.setLayout(layout);
				tabItem.setControl(tabCom);
				tabItem.setText(tabName);

				tabItems.put(tabName, tabItem);
				new FillTab(tabCom, zone).start();

				tabItem.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent e) {
						tabItems.remove(tabName); // 从容器中移除此tabitem
						sPanels.remove(tabName); // 从容器中移除此区域的panels
					}
				});
			}
		}
	}
	public void update(String ip, Object msg) {
		Map<String, SoHard> target = sPanels.get(zoneNames.get(0)); // @Customized
		if (target != null) {
			for (Iterator<SoHard> itr = target.values().iterator(); itr.hasNext();) {
				itr.next().update(ip, msg);
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
						panels.put(device.getIp(), sPanel);
					}
				});
			}
			sPanels.put(zone.getName(), panels);
		}
	}
}
