package com.whck.msf;

import java.util.List;
import org.apache.mina.core.session.IoSession;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.whck.msf.util.XmlUtil;
import com.whck.proto.handler.MessageHandler;

public class SetDialog extends Dialog{
	String[] types={"小雨","中雨","大雨","暴雨"};
	Font font;
	Label devIdLbl;
	Combo devIdComb;
	Label lblRainType;
	Label lblRainIntn;
	Button[] btns=new Button[6];
	Spinner[] spns=new Spinner[6];
	Label rainfallarglbl;
	Combo rainfallargcomb;
	private Button okBtn;
	private Button cancelBtn;
	public SetDialog(Shell parentShell) {
		super(parentShell);
		font=new Font(Display.getCurrent(), "微软雅黑", 12,  SWT.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
//		XmlUtil xmlUtil=null;
//		try {
//			xmlUtil=XmlUtil.getInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		GridData gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
		Composite c=new Composite(parent,SWT.NONE);
//		c.setLayoutData(gridData);
//		c.setLayout(new GridLayout(2,true));
//		String zoneName=xmlUtil.getZoneNames().get(0);
//		List<String> devIds=xmlUtil.getDevIds(zoneName);
//		gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL);
//		devIdLbl=new Label(c,SWT.NONE);
//		devIdLbl.setText("选择区域");
//		devIdLbl.setFont(font);
//		devIdLbl.setAlignment(SWT.CENTER);
//		devIdLbl.setLayoutData(gridData);
//		devIdComb=new Combo(c,SWT.NONE);
//		devIdComb.setItems(devIds.toArray(new String[0]));
//		devIdComb.select(0);
//		devIdComb.setLayoutData(gridData);
//		gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL);
//		gridData.verticalIndent=20;
//		lblRainType=new Label(c,SWT.NONE);
//		lblRainType.setText("雨型");
//		lblRainType.setFont(font);
//		lblRainType.setAlignment(SWT.CENTER);
//		lblRainType.setLayoutData(gridData);
//		lblRainIntn=new Label(c,SWT.NONE);
//		lblRainIntn.setText("压强(10KPa)");
//		lblRainIntn.setFont(font);
//		lblRainIntn.setAlignment(SWT.CENTER);
//		lblRainIntn.setLayoutData(gridData);
//		gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL);
//		gridData.verticalIndent=6;
//		for(int i=0;i<types.length;i++){
//			btns[i]=new Button(c,SWT.CHECK);
//			btns[i].setText(types[i]);
//			btns[i].setFont(font);
//			btns[i].setAlignment(SWT.CENTER);
//			btns[i].setLayoutData(gridData);
//			gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL);
//			gridData.verticalIndent=6;
//			spns[i]=new Spinner(c,SWT.NONE);
//			spns[i].setMinimum(0);
//			spns[i].setMaximum(300);
//			spns[i].setLayoutData(gridData);
//			spns[i].setSelection((int)ArgsCache.rainer_args[i]);
//		}
//		gridData=new GridData(GridData.FILL_BOTH|GridData.FILL_HORIZONTAL);
//		gridData.verticalIndent=10;
//		rainfallarglbl=new Label(c,SWT.NONE);
//		rainfallarglbl.setText("雨量筒参数");
//		rainfallarglbl.setFont(font);
//		rainfallarglbl.setAlignment(SWT.CENTER);
//		rainfallarglbl.setLayoutData(gridData);
//		rainfallargcomb=new Combo(c,SWT.NONE);
//		rainfallargcomb.setItems(new String[]{"0.2mm","0.5mm"});
//		if(1==(int)ArgsCache.rainer_args[4]){
//			rainfallargcomb.select(0);
//		}else{
//			rainfallargcomb.select(1);
//		}
//		rainfallargcomb.setLayoutData(gridData);
		return c;
	}

//	@Override
//	protected void okPressed() {
//		byte adr=Byte.valueOf(devIdComb.getText().trim());
//		int[] args=ArgsCache.rainer_args;
//		byte[] data=new byte[20];
//		for(int i=0;i<types.length;i++){
////			if(btns[i].getSelection()){
//				args[i]=spns[i].getSelection();
////			}
//		}
//		args[4]=rainfallargcomb.getSelectionIndex()+1;
//		for(int i=0;i<5;i++){
//			byte[] temp=AlgorithmUtil.intToByteArray((int)args[i]);
//			for(int j=0;j<4;j++){
//				data[i*4+j]=temp[j];
//			}
//		}
//		//TODO:发送：
//		IoSession session=MessageHandler.sessions.get("192.168.1.45");
//		if(session==null||(!session.isConnected())){
//			showError();
//		}else{
//			new Thread(){
//				public void run() {
//					SendMulti sm=new SendMulti();
//			    	sm.setAdr(adr);
//			    	sm.setFuncCode(Constants.MULTI_REGISTER);
//			    	sm.setFromPos(new byte[]{0,7});
//					sm.setDataLen((byte)data.length);
//					sm.setDataNum(new byte[]{0,5});
//					sm.setData(data);
//					session.write(sm);
//				}
//			}.start();
//			showOK();
//		}
//		super.okPressed();
//	}
	protected void showOK(){
		MessageBox box= new MessageBox(getShell(),SWT.OK|SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("操作成功");
		box.open();
	}
	protected void showError(){
		MessageBox box= new MessageBox(getShell(),SWT.OK|SWT.ICON_INFORMATION);
		box.setText("提示");
		box.setMessage("连接已断开");
		box.open();
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("参数配置");
	}
	@Override
	protected Point getInitialSize() {
		return new Point(220,360);
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
