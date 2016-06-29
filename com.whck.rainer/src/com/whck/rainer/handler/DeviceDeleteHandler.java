package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.whck.rainer.Activator;
import com.whck.rainer.ZoneListView;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.XmlUtil;

public class DeviceDeleteHandler extends AbstractHandler {

	public final static String ID = "com.whck.rainer.command.device.delete";

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
			box.setMessage("确定删除" + temp.getName() + "设备？");
			int code = box.open();
			if (code == SWT.OK) {
				try {
					xmlUtil = XmlUtil.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Zone zone = (Zone) temp.getParent();
				// 删除xml文件中的device节点
				xmlUtil.deleteDevice(zone.getName(), temp.getId());
				// 删除treeViewer中的device节点
				zone.remove(temp);

				ZoneListView zoneListView = (ZoneListView) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView("com.whck.rainer.zonelistview");
				zoneListView.getTreeViewer().refresh();

			}

		}
		return null;
	}
}
