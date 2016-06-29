package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.whck.rainer.Activator;
import com.whck.rainer.AddZoneDialog;
import com.whck.rainer.ZoneListView;
import com.whck.rainer.model.Zone;

public class ZoneModifyHandler extends AbstractHandler {
	public final static String ID = "com.whck.rainer.command.zone.modify";
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();
		if(item instanceof Zone){
			AddZoneDialog d = new AddZoneDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			Zone temp = (Zone) item;
			d.setZone(temp);
			d.setFlag(1);
			int code = d.open();
			if (code == Window.OK) {
				ZoneListView zoneListView = (ZoneListView) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView("com.whck.rainer.zonelistview");
				zoneListView.getTreeViewer().refresh();
			}
		}
		return null;
	}
}
