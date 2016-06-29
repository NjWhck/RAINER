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
import com.whck.rainer.model.Rainer;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.XmlUtil;

public class ZoneDeleteHandler extends AbstractHandler{
	public final static String ID = "com.whck.rainer.command.zone.delete";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object item = selection.getFirstElement();
		if(item instanceof Zone){
			Zone temp = (Zone) item;
			MessageBox box = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OK |SWT.CANCEL| SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("确定删除"+temp.getName()+"分区？");
			int code=box.open();
			if(code==SWT.OK){
				XmlUtil xmlUtil = null;
				try {
					xmlUtil = XmlUtil.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				//删除xml文件中的zone节点
				xmlUtil.deleteZone(temp.getName());
				//删除treeViewer中的zone节点
				Rainer rainer = (Rainer) (temp.getParent());
				rainer.remove(temp);
			}
		}
		return null;
	}
}
