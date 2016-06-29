package com.whck.rainer.panel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.whck.rainer.model.Device;
import com.whck.rainer.model.DynamicLimit;
import com.whck.rainer.model.TimeManager;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;

public class SettingDialog extends Dialog {

	private Device device;
	private Device oldDevice;
	
	protected SettingDialog(Shell parentShell) {
		super(parentShell);
	}
	public SettingDialog(Shell parentShell,Device device){
		super(parentShell);
		this.device=device;
		oldDevice=new Device();
		try {
			BeanUtils.copyProperties(oldDevice, device);
			List<TimeManager> timeMngs=new ArrayList<>();
			timeMngs.addAll(device.getTimeMng());
			List<Variable> variables=new ArrayList<>();
			variables.addAll(device.getVariables());
			oldDevice.setTimeMng(timeMngs);
			oldDevice.setVariables(variables);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		System.out.println("device hashcode:"+device.hashCode()+" ||oldhashcode:"+oldDevice.hashCode());
	}
	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("设置");
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container =new Composite(parent,SWT.NONE);
		container.setLayout(new GridLayout(1,true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.VERTICAL_ALIGN_BEGINNING));
		//时段设定组
		Group timeSettingGrp=new Group(container,SWT.BORDER);
		timeSettingGrp.setText("运行时段设置");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.GRAB_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;
		timeSettingGrp.setLayoutData(gridData);
		timeSettingGrp.setLayout(new GridLayout(2,true));
		//四个时间段设置
		for(int i=0;i<4;i++){
			gridData = new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.GRAB_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalSpan = 1;
			TimeManager tMng=new TimeManager();
			Group timeGrp=new Group(timeSettingGrp,SWT.BORDER);
			timeGrp.setLayout(new GridLayout(4,true));
			timeGrp.setLayoutData(gridData);
			gridData =new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL|GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalSpan=4;
			Button chkBtn=new Button(timeGrp,SWT.CHECK);
			chkBtn.setText("第"+(i+1)+"时间段");
			chkBtn.setLayoutData(gridData);
			chkBtn.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("start Time:"+tMng.hashCode());
					if(chkBtn.getSelection()){
						tMng.setTriggered(true);
					}else{
						tMng.setTriggered(false);
					}
					if(!device.getTimeMng().contains(tMng)){
						device.getTimeMng().add(tMng);
					}
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			gridData =new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
			gridData.horizontalSpan=1;
			Label startTimeLbl=new Label(timeGrp,SWT.NONE);
			startTimeLbl.setText("时间起点");
			startTimeLbl.setLayoutData(gridData);
			 CDateTime startCdt=new CDateTime(timeGrp, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
			 startCdt.setPattern("yyyy-MM-dd HH:mm:ss");
			 startCdt.setSelection(new Date());
			 startCdt.setLayoutData(gridData);
			 startCdt.addSelectionListener(new SelectionListener() {  	//不确定是否是这个监听器
				@Override
				public void widgetSelected(SelectionEvent e) {
					Date date=startCdt.getSelection();
					tMng.setFromDate(date);
					
				}
				public void widgetDefaultSelected(SelectionEvent e) {	}
			});
			
			 Label stopTimeLbl=new Label(timeGrp,SWT.NONE);
			stopTimeLbl.setText("时间终点");
			stopTimeLbl.setLayoutData(gridData);
			CDateTime stopCdt=new CDateTime(timeGrp, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
			stopCdt.setPattern("yyyy-MM-dd HH:mm:ss");
			stopCdt.setSelection(new Date());
			stopCdt.setLayoutData(gridData);
			stopCdt.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					Date date=stopCdt.getSelection();
					tMng.setToDate(date);
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			Label runningDurLbl=new Label(timeGrp,SWT.NONE);
			runningDurLbl.setText("运行时长(min)");
			runningDurLbl.setLayoutData(gridData);
			Spinner runningDurSpn=new Spinner(timeGrp,SWT.NONE);
			runningDurSpn.setMaximum(360);
			runningDurSpn.setLayoutData(gridData);
			runningDurSpn.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					int selection=runningDurSpn.getSelection();
					System.out.println("selection Listener:"+selection);
					tMng.setRunningDur(selection);
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			
			Label idleDurLbl=new Label(timeGrp,SWT.NONE);
			idleDurLbl.setText("间歇时长(min)");
			idleDurLbl.setLayoutData(gridData);
			 Spinner idleDurSpn=new Spinner(timeGrp,SWT.NONE);
			idleDurSpn.setMaximum(360);
			idleDurSpn.setLayoutData(gridData);
			idleDurSpn.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					int selection = Integer.valueOf(idleDurSpn.getText());
					System.out.println("modify Listener:"+selection);
					tMng.setIdleDur(selection);
				}
			});
		}
		
		//关联传感器组  //动态
		Zone zone=(Zone) device.getParent();
		List<Device > devices=zone.getDevices();
		gridData=new GridData(GridData.FILL_HORIZONTAL|GridData.GRAB_HORIZONTAL|GridData.GRAB_VERTICAL);
		Group relaSencerGrp=new Group(container,SWT.NONE);
		relaSencerGrp.setLayoutData(gridData);
		RowLayout layout=new RowLayout();
		layout.marginTop=10;
		layout.justify=true;
		relaSencerGrp.setLayout(layout);
		
		for(Iterator<Device> it=devices.iterator();it.hasNext();){
			Device dev=it.next();
			if(dev.getType()==20){//监测设备(传感器)  ,检测设备下的变量按理说都是只显示的量
				Group group=new Group(relaSencerGrp,SWT.BORDER);
				group.setLayout(new GridLayout(2,true));
				GridData gData=new GridData();
				gData.horizontalSpan=2;
				Button chkBtn=new Button(group,SWT.CHECK);
				chkBtn.setText(dev.getName());
				chkBtn.setLayoutData(gData);
				chkBtn.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						if(chkBtn.getSelection()){
							dev.setTriggered(true);
							device.getRelaDevs().add(dev);
						}else {
							dev.setTriggered(false);
							if(device.getRelaDevs().contains(dev)){
								device.getRelaDevs().remove(dev);
							}
						}
					}
					public void widgetDefaultSelected(SelectionEvent e) {	}
				});
				List<Variable> variables=dev.getVariables();
				for(Iterator<Variable> itr=variables.iterator();itr.hasNext();){
					Variable var=itr.next();
					DynamicLimit dLimit=new DynamicLimit();
					var.setdLimit(dLimit);
					 gData=new GridData();
					gData.horizontalSpan=2;
					gData.horizontalAlignment=GridData.HORIZONTAL_ALIGN_FILL;
					Group varGrp=new Group(group,SWT.BORDER);
					varGrp.setLayout(new GridLayout(2,true));
					varGrp.setLayoutData(gData);
					
					gData=new GridData();
					gData.horizontalSpan=2;
					gData.horizontalAlignment=GridData.HORIZONTAL_ALIGN_BEGINNING;
					
					Button varBtn=new Button(varGrp,SWT.CHECK);
					varBtn.setText(var.getName());
					varBtn.setLayoutData(gData);
					varBtn.addSelectionListener(new SelectionListener() {
						public void widgetSelected(SelectionEvent e) {
							if(varBtn.getSelection()){
								var.setTriggered(true);
							}else{
								var.setTriggered(false);
							}
						}
						public void widgetDefaultSelected(SelectionEvent e) {}
					});
					
					gData=new GridData();
					gData.horizontalSpan=2;
					gData.horizontalAlignment=GridData.HORIZONTAL_ALIGN_BEGINNING;
					Label upValLbl=new Label(varGrp,SWT.NONE);
					upValLbl.setText("上限值"+"("+var.getUnit()+")");
					Spinner upSpn=new Spinner(varGrp,SWT.NONE);
					upSpn.setMaximum(Integer.valueOf(var.getUpLimitVal()));
					upSpn.setMinimum(Integer.valueOf(var.getDownLimitVal()));
					upSpn.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							int upVal=Integer.valueOf(upSpn.getText());
							dLimit.setdUpLimit(upVal);
						}
					});
					
					Label downValLbl=new Label(varGrp,SWT.NONE);
					downValLbl.setText("下限值"+"("+var.getUnit()+")");
					Spinner downSpn=new Spinner(varGrp,SWT.NONE);
					downSpn.setMaximum(Integer.valueOf(var.getUpLimitVal()));
					downSpn.setMinimum(Integer.valueOf(var.getDownLimitVal()));
					downSpn.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							dLimit.setdDownLimit(Integer.valueOf(downSpn.getText()));
						}
					});
				}
			}
		}
		 
		return container;
	}
	
	@Override
	protected void cancelPressed() {
		device=oldDevice;										//还原
		super.cancelPressed();
	}
	@Override
	protected Point getInitialSize() {
		return new Point(900,800);
	}
	
	@Override
	protected int getShellStyle() {
		return SWT.RESIZE|SWT.CLOSE;
	}
}
