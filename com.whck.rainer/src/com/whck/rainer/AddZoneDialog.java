package com.whck.rainer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Rainer;
import com.whck.rainer.model.Zone;
import com.whck.rainer.util.XmlUtil;

public class AddZoneDialog extends Dialog{
	public AddZoneDialog(Shell parentShell) {
		super(parentShell);
	}

	// 标识 0：添加 1：修改
	private int flag;
	/* 验证用户输入后的返回值 */
	private static final int ACCEPT = 0;
	private static final int EMPTY_ZONE_NAME = 1;
	private static final int EXIST_ZONE_NAME = 2;

	// 所关联的分区
	private Zone zone;
	private XmlUtil xmlUtil;
	private Font font;
	private Label nameLbl;
	private Text zoneNameTxt;
	private Button okBtn;
	private Button cancelBtn;
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("分区管理");
		font=new Font(Display.getCurrent(), "微软雅黑", 12,  SWT.NONE);
	}

	protected Control createDialogArea(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_VERTICAL|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, true));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER|GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 1;
		nameLbl = new Label(composite, SWT.CENTER);
		nameLbl.setText("区域名称");
		nameLbl.setFont(font);
		nameLbl.setAlignment(SWT.CENTER);
		nameLbl.setLayoutData(gridData);

		zoneNameTxt = new Text(composite, SWT.CENTER);
		zoneNameTxt.setLayoutData(gridData);
		loadZoneInfo();
		return composite;
	}

	// zoneNameComb 监听事件调用
	protected void loadZoneInfo() {
//		rainTypeBtns = new HashMap<>();
//		devBtns = new HashMap<>();
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e1) {
			System.out.println("AddZoneDialog:Document 加载失败!");
		}
		if(flag==1){
			zoneNameTxt.setText(zone.getName());
		}
		
	}

	protected void okPressed() {
		// 更新Zone
		String zoneName = zoneNameTxt.getText().trim();
		Rainer rainer=(Rainer)zone.getParent();
		// 验证输入合法性
		if(!showMessage(checkForm(zoneName),flag)){
			return;
		}
		
		if (1 == flag) { // 修改 删掉
			xmlUtil.deleteZone(zone.getName());
			rainer.remove(zone);
			List<Device> devs=zone.getDevices();
			Zone newZone = new Zone(zoneName,new ArrayList<Device>(0));
			zone = newZone;
			zone.setDevices(devs);
			rainer.add(zone);
			xmlUtil.addZone(zone);
			showOK();
		}
		else{
			Zone newZone = new Zone(zoneName,new ArrayList<Device>(0));
			zone = newZone;
			rainer.add(zone);
			xmlUtil.addZone(zone);
			showOK();
		}

		
		super.okPressed();
	}

	protected void showOK() {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
			box.setText("提示");
			box.setMessage("操作成功");
			box.open();
			getShell().close();
	}
	protected boolean showMessage(int msgType,int flag) {
		if (EMPTY_ZONE_NAME == msgType) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("区域名不能为空");
			box.open();
			return false;
		} else if (EXIST_ZONE_NAME == msgType&&flag==0) {
			MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText("提示");
			box.setMessage("区域名已存在");
			box.open();
			return false;
		}
		return true;
	}

	protected int checkForm(String zoneName) {
		if (zoneName == "") {
			return EMPTY_ZONE_NAME;
		}
		if (xmlUtil.getZoneNames().indexOf(zoneName) != -1) {
			return EXIST_ZONE_NAME;
		}
		return ACCEPT;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	public void setZone(Zone zone){
		this.zone=zone;
	}
	public Zone getZone() {
		return zone;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(240,120);
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
