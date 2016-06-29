package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import com.whck.rainer.AddZoneDialog;
import com.whck.rainer.model.Zone;

public class ZoneAddHandler extends AbstractHandler{

	public final static String ID = "com.whck.rainer.command.zone.add";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		
		Object item = selection.getFirstElement();
		
		System.out.println("handle obj:"+item);
		if(item instanceof Zone){
			AddZoneDialog d = new AddZoneDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			Zone temp = (Zone) item;
			d.setZone(temp);
			d.setFlag(0);
			d.open();
		}
		
		return null;
	}
}
