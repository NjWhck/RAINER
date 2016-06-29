package com.whck.msf.panel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import com.whck.msf.db.FFCDataDAO;
import com.whck.msf.db.FlowDataDAO;
import com.whck.msf.db.MSFDataDAO;
import com.whck.msf.model.Device;
import com.whck.msf.model.Variable;
import com.whck.proto.domain.FFCData;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;

public class SoHard extends Composite {
	private Device device;
	private Font font;
//	private Label stateBar;
	private Map<String, Control> showCtrls = new HashMap<String, Control>(0);

	public SoHard(Composite parent, int style) {
		super(parent, SWT.BORDER);
	}

	public SoHard(Composite parent, int style, Device device) {
		super(parent, SWT.BORDER);
		this.setLayout(new GridLayout(1, true));
		this.device = device;
		font = new Font(Display.getCurrent(), "微软雅黑", 10, SWT.NONE);
		createControlArea();
	}

	private void createControlArea() {
		GridData outGridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Composite header = new Composite(this, SWT.NONE);
		header.setLayoutData(outGridData);
		header.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.heightHint = 22;
		gridData.horizontalIndent = 6;
		gridData.verticalIndent = 1;

		Label titleLbl = new Label(header, SWT.NONE);
		titleLbl.setLayoutData(gridData);
		titleLbl.setFont(new Font(this.getDisplay(), "微软雅黑", 14, SWT.BOLD));
		titleLbl.setAlignment(SWT.CENTER);
		titleLbl.setText(device.getName() + "(" + device.getId() + ")");

//		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
//				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
//		gridData.heightHint = 22;
//		gridData.horizontalSpan=1;
//		gridData.horizontalIndent = 6;
//		gridData.verticalIndent = 1;
//		stateBar = new Label(header, SWT.NONE);
//		stateBar.setText("已停止");
//		stateBar.setFont(new Font(this.getDisplay(), "微软雅黑", 14, SWT.NONE));
//		stateBar.setAlignment(SWT.CENTER);
//		stateBar.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.heightHint = 22;
		gridData.horizontalSpan=1;
		gridData.horizontalIndent = 6;
		gridData.verticalIndent = 1;
		
		Composite body = new Composite(this, SWT.NONE);
		outGridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		body.setLayoutData(outGridData);
		body.setLayout(new GridLayout(1, true));
		Composite showArea = new Composite(body, SWT.BORDER);
		showArea.setLayoutData(outGridData);
		RowLayout layout = new RowLayout();
		layout.marginTop = 13;
		layout.spacing=0;
		layout.justify = true;
		showArea.setLayout(layout);
		for (Iterator<Variable> it = device.getVariables().iterator(); it.hasNext();) {
			Control control = null;
			Variable var = it.next();
			String varName = var.getName();
			control = new DashBoard(showArea, SWT.NONE, var);
			showCtrls.put(varName, control);
		}
		outGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		GridData setGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		setGridData.verticalIndent = 8;

		Composite footer = new Composite(this, SWT.NONE);
		outGridData = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_END);
		footer.setLayoutData(outGridData);
		footer.setLayout(new GridLayout(3, true));

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
	}

	public void update(String ip, Object msg) {
		if (msg instanceof MSFData) {
			MSFData entity = (MSFData) msg;
			entity.setName(device.getName());
			MSFDataDAO dao = MSFDataDAO.getInstance();
			if (String.valueOf(entity.getId()).equals(device.getId())) {
				new Thread() {
					public void run() {
						try {
							dao.save(entity);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				for (Iterator<Control> canvas = showCtrls.values().iterator(); canvas.hasNext();) {
					DashBoard db = (DashBoard) canvas.next();
					db.update(msg);
				}
			}
		}else if(msg instanceof FlowData){
			FlowData entity = (FlowData) msg;
			entity.setName(device.getName());
			FlowDataDAO dao = FlowDataDAO.getInstance();
			if (String.valueOf(entity.getId()).equals(device.getId())) {
				new Thread() {
					public void run() {
						try {
							dao.save(entity);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				for (Iterator<Control> canvas = showCtrls.values().iterator(); canvas.hasNext();) {
					DashBoard db = (DashBoard) canvas.next();
					db.update(msg);
				}
			}
		}else if(msg instanceof FFCData){
			FFCData entity = (FFCData) msg;
			entity.setName(device.getName());
			FFCDataDAO dao = FFCDataDAO.getInstance();
			if (String.valueOf(entity.getId()).equals(device.getId())) {
				new Thread() {
					public void run() {
						try {
							dao.save(entity);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				for (Iterator<Control> canvas = showCtrls.values().iterator(); canvas.hasNext();) {
					DashBoard db = (DashBoard) canvas.next();
					db.update(msg);
				}
			}
		}
	}
	
	public void relaTrigger(boolean triggered){
		this.getDevice().setTriggered(triggered);
//		relaChkBtn.setSelection(triggered);
	}
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(360, 450);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (font != null) {
			font.dispose();
		}
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	protected void showOK() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("操作成功");
		box.open();
	}

	protected void showError() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("操作失败");
		box.open();
	}
}
