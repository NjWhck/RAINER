package com.whck.msf.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import com.whck.msf.AddZoneDialog;
import com.whck.msf.ShowView;
import com.whck.msf.ZoneListView;
import com.whck.msf.model.Zone;

public class ZoneAddHandler extends AbstractHandler{

	public final static String ID = "com.whck.msf.command.zone.add";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		
		Object item = selection.getFirstElement();
		
		if(item instanceof Zone){
			AddZoneDialog d = new AddZoneDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			Zone temp = (Zone) item;
			d.setZone(temp);
			d.setFlag(0);
			int code=d.open();
			if(code==Window.OK){
				ZoneListView.getInitalInput();
				ShowView.refresh();
			}
		}
		
		return null;
	}
}
