package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import com.whck.rainer.ChartDialog;

public class ShowChartHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ChartDialog d = new ChartDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		
		d.open();
		return null;
	}
}
