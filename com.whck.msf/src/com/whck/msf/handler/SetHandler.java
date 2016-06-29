package com.whck.msf.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import com.whck.msf.SetDialog;
public class SetHandler  extends AbstractHandler{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SetDialog d = new SetDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		d.open();
		return null;
	}
}
