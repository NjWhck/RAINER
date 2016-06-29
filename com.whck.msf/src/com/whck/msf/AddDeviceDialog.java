package com.whck.msf;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.whck.msf.model.Device;
import com.whck.msf.model.Variable;
import com.whck.msf.model.Zone;
import com.whck.msf.util.MethodUtil;
import com.whck.msf.util.XmlUtil;
import com.whck.proto.handler.MessageHandler;

public class AddDeviceDialog extends Dialog {

	private static final int SUCCESS = 0;
	private static final int DEVICE_NAME_EMPTY = 1;
	private static final int DEVICE_ID_EMPTY = 2;
	private static final int DEVICE_ID_EXSIT = 3;
	private static final int DEVICE_ID_INVALID = 4;
	private static final int DEVICE_IP_EMPTY = 5;
	private static final int DEVICE_IP_INVALID = 6;
	private XmlUtil xmlUtil;
	
	private Font font;
	private int flag;
	private Zone zone;
	private Device device;
	private Label nameLbl;
	private Text nameTxt;
	private Label idLbl;
	private Text idTxt;
	private Label ipLbl;
	private Text ipTxt;
	private Button ctrlChk;
	private Button dectChk;
	private Group typeGrp;
	private Button motorRadio;
	private Button triggerRadio;
	private Button okBtn;
	private Button cancelBtn;

	public AddDeviceDialog(Shell parentShell) {
		super(parentShell);
		font=new Font(Display.getCurrent(), "微软雅黑", 12,  SWT.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_VERTICAL|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(gridData);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 1;
		gridData.verticalIndent=0;
		nameLbl = new Label(composite, SWT.NONE);
		nameLbl.setText("设备名称");
		nameLbl.setFont(font);
		nameLbl.setAlignment(SWT.CENTER);
		nameLbl.setLayoutData(gridData);
		nameTxt = new Text(composite, SWT.BORDER);
		nameTxt.setLayoutData(gridData);
		idLbl = new Label(composite, SWT.NONE);
		idLbl.setText("设备ID");
		idLbl.setFont(font);
		idLbl.setAlignment(SWT.CENTER);
		idLbl.setLayoutData(gridData);
		idTxt = new Text(composite, SWT.BORDER);
		idTxt.setLayoutData(gridData);
		ipLbl = new Label(composite, SWT.NONE);
		ipLbl.setText("设备IP");
		ipLbl.setFont(font);
		ipLbl.setAlignment(SWT.CENTER);
		ipLbl.setLayoutData(gridData);
		ipTxt = new Text(composite, SWT.BORDER);
		ipTxt.setLayoutData(gridData);

		ctrlChk = new Button(composite, SWT.CHECK);
		ctrlChk.setText("控制设备");
		ctrlChk.setFont(font);
		ctrlChk.setAlignment(SWT.CENTER);
		ctrlChk.setLayoutData(gridData);
		ctrlChk.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (ctrlChk.getSelection()) {
					typeGrp.setVisible(true);
				} else {
					typeGrp.setVisible(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		dectChk = new Button(composite, SWT.CHECK);
		dectChk.setText("检测设备");
		dectChk.setFont(font);
		dectChk.setAlignment(SWT.CENTER);
		dectChk.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 2;
		typeGrp = new Group(composite, SWT.NONE);
		typeGrp.setVisible(false);
		typeGrp.setLayoutData(gridData);
		typeGrp.setLayout(new GridLayout(2, true));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan =1;
		gridData.verticalIndent=0;
		motorRadio = new Button(typeGrp, SWT.RADIO);
		motorRadio.setText("电机设备");
		motorRadio.setFont(font);
		motorRadio.setAlignment(SWT.CENTER);
		motorRadio.setLayoutData(gridData);
		triggerRadio = new Button(typeGrp, SWT.RADIO);
		triggerRadio.setText("单点设备");
		triggerRadio.setFont(font);
		triggerRadio.setAlignment(SWT.CENTER);
		triggerRadio.setLayoutData(gridData);
		if (flag == 1) {
			nameTxt.setText(device.getName());
			idTxt.setText(device.getId());
			ipTxt.setText(device.getIp());
			int type = device.getType();

			if (10 == type) { // 电机控制
				typeGrp.setVisible(true);
				ctrlChk.setSelection(true);
				motorRadio.setSelection(true);
			} else if (11 == type) { // 单点控制
				typeGrp.setVisible(true);
				ctrlChk.setSelection(true);
				triggerRadio.setSelection(true);
			} else if (20 == type) {
				dectChk.setSelection(true);
			}
		}
		// loadDeviceInfo();
		return composite;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("添加设备");
	}

	@Override
	protected void okPressed() {
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String devName = nameTxt.getText().trim();
		String devId = idTxt.getText().trim();
		String devIp = ipTxt.getText().trim();
		int type = 0;
		if (ctrlChk.getSelection()) {
			if (motorRadio.getSelection()) {
				type = 10;
			} else {
				type = 11;
			}
		} else {
			type = 20;
		}

		if (!showMessage(checkForm(devName, devId, devIp), flag)) {
			return;
		}
		if (flag == 1) {
			List<Variable> vars = device.getVariables();
			xmlUtil.deleteDevice(zone.getName(),device.getName(),device.getId());
			zone.remove(device);
			device = new Device(devId, devName, devIp, type, new ArrayList<Variable>(0));
			device.setVariables(vars);
			zone.add(device);
			xmlUtil.addDevice(zone.getName(), device);

		} else {
			device = new Device(devId, devName, devIp, type, new ArrayList<Variable>(0));
			zone.add(device);
			xmlUtil.addDevice(zone.getName(), device);
		}
		showOK();
		super.okPressed();
	}

	protected void showOK() {
		MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("操作成功");
		box.open();
		getShell().close();
	}

	protected boolean showMessage(int msgType, int flag) {
		if (DEVICE_NAME_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("设备不能为空");
			box.open();
			return false;
		} else if (DEVICE_ID_EXSIT == msgType && flag == 0) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("该ID已存在");
			box.open();
			return false;
		} else if (DEVICE_ID_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("ID不能为空");
			box.open();
			return false;
		} else if (DEVICE_ID_INVALID == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("ID不可用,请填写数字");
			box.open();
			return false;
		} else if (DEVICE_IP_EMPTY == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("IP地址不能为空");
			box.open();
			return false;
		} else if (DEVICE_IP_INVALID == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("IP地址不合法");
			box.open();
			return false;
		}
		return true;
	}

	protected int checkForm(String name, String id, String ip) {
		int result = SUCCESS;
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (name.equals("")) {
			return DEVICE_NAME_EMPTY;
		}
		if (id.equals("")) {
			return DEVICE_ID_EMPTY;
		}
		if (ip.equals("")) {
			return DEVICE_IP_EMPTY;
		}
		if (!MethodUtil.isValidIp(ip)) {
			return DEVICE_IP_INVALID;
		}
		if (xmlUtil.getAllDevIdsByDevName(name).contains(id)) {
			return DEVICE_ID_EXSIT;
		}
		if (!MethodUtil.isNumeric(id)) {
			return DEVICE_ID_INVALID;
		}

		return result;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Zone getZone() {
		return zone;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
		zone = (Zone) device.getParent();
	}
	@Override
	protected Point getInitialSize() {
		return new Point(280,360);
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
	}
	@Override
	protected void initializeBounds() {
		Composite compo = (Composite) getButtonBar();
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		compo.setLayout(new GridLayout(2,true));
		compo.setLayoutData(gridData);
		okBtn=super.createButton(compo, IDialogConstants.OK_ID, "确定", false);
		cancelBtn=super.createButton(compo, IDialogConstants.CANCEL_ID, "取消", false);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent=6;
		okBtn.setText("确定");
		okBtn.setFont(font);
		okBtn.setLayoutData(gridData);
		cancelBtn.setText("取消");
		cancelBtn.setFont(font);
		cancelBtn.setLayoutData(gridData);
		super.initializeBounds();
	}
}
