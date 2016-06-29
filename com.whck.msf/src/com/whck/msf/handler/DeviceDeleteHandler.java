package com.whck.msf.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import com.whck.msf.Activator;
import com.whck.msf.ShowView;
import com.whck.msf.ZoneListView;
import com.whck.msf.model.Device;
import com.whck.msf.model.Zone;
import com.whck.msf.util.XmlUtil;
import com.whck.proto.handler.MessageHandler;

public class DeviceDeleteHandler extends AbstractHandler {

	public final static String ID = "com.whck.msf.command.device.delete";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		XmlUtil xmlUtil = null;
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();
		if (item instanceof Device) {
			Device temp = (Device) item;
			MessageBox box = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("确认删除" + temp.getName() + "设备?");
			int code = box.open();
			if (code == SWT.OK) {
				try {
					xmlUtil = XmlUtil.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Zone zone = (Zone) temp.getParent();
				System.out.println("zone:"+zone);
				xmlUtil.deleteDevice(zone.getName(), temp.getName(),temp.getId());
				MessageHandler.sessions.remove(temp.getIp());
				zone.remove(temp);

				ZoneListView zoneListView = (ZoneListView) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView("com.whck.msf.zonelistview");
				zoneListView.getTreeViewer().refresh();
				
				ZoneListView.getInitalInput();
				ShowView.refresh();
			}

		}
		return null;
	}
}
