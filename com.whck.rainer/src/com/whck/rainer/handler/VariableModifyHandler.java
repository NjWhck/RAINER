package com.whck.rainer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.whck.rainer.Activator;
import com.whck.rainer.AddVariableDialog;
import com.whck.rainer.ZoneListView;
import com.whck.rainer.model.Variable;

public class VariableModifyHandler extends AbstractHandler{
	public final static String ID = "com.whck.rainer.command.variable.modify";
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();
		if(item instanceof Variable){
			AddVariableDialog d = new AddVariableDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			Variable temp = (Variable) item;
			d.setVariable(temp);
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
