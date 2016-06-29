package com.whck.rainer.panel;

import java.awt.GridLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.whck.rainer.model.Device;
//1:解决设备联动:点击启动按钮时过滤此zone内所有的triggered的“可控”设备
//并在协议中加入这些设备的信息，发送！
public class CtrlPanel extends Composite {
	
	private Shell shell;
	private Device device;
	private Image backgroud;
	private Image dashboard;
	public CtrlPanel(Composite parent, int style) {
		super(parent, SWT.NO_TRIM);
	}

	public CtrlPanel(Composite parent, int style,Device device) {
		super(parent, SWT.NO_TRIM);
		this.device = device;
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				CtrlPanel.this.widgetDisposed(e);
			}
		});
		int devType=device.getType();
		switch(devType){
		case 10:
			backgroud = PictureFactory.getImage("device_panel_bg");
			dashboard=PictureFactory.getImage("wind_motor_bg");
			break;
		case 11:
			backgroud = PictureFactory.getImage("device_panel_bg");
			dashboard=PictureFactory.getImage("wind_motor_bg");
			break;
		case 20:
			backgroud = PictureFactory.getImage("device_panel_bg");
			dashboard=PictureFactory.getImage("wind_motor_bg");
			break;
		default:
			backgroud = PictureFactory.getImage("device_panel_bg");
			dashboard=PictureFactory.getImage("wind_motor_bg");
				break;
		}
		createContents(devType);
	}
	protected void widgetDisposed(DisposeEvent e) {
		if(backgroud!=null){
			backgroud.dispose();
			backgroud=null;
		}
		if(dashboard!=null){
			dashboard.dispose();
			dashboard=null;
		}
	}
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Image getBackgroud() {
		return backgroud;
	}

	public void setBackgroud(Image backgroud) {
		this.backgroud = backgroud;
	}

	public Image getDashboard() {
		return dashboard;
	}

	public void setDashboard(Image dashboard) {
		this.dashboard = dashboard;
	}

	public void update(String ip,Object message){
		if(ip.equals(device.getIp())){
			
		}
	}
	
	protected void createContents(int type) {
		shell=new Shell(this.getDisplay(),SWT.NO_TRIM);
		createIrregularlyShell();
		Listener l = new Listener() {
			int startX, startY;
			public void handleEvent(Event e) {
				if (e.type == SWT.MouseDown && e.button == 1) {
					startX = e.x;
					startY = e.y;
				}
				if (e.type == SWT.MouseMove && (e.stateMask & SWT.BUTTON1) != 0) {
					Point p = shell.toDisplay(e.x, e.y);
					p.x -= startX;
					p.y -= startY;
					shell.setLocation(p);
				}
				if (e.type == SWT.Paint) {
					ImageData imageData=backgroud.getImageData();
					e.gc.drawImage(backgroud, imageData.x, imageData.y);
				}
			}
		};
		shell.addListener(SWT.KeyDown, l);
		shell.addListener(SWT.MouseDown, l);
		shell.addListener(SWT.MouseMove, l);
		shell.addListener(SWT.Paint, l);

		Composite header=new Composite(shell,SWT.NONE);
		
		Composite body=new Composite(shell,SWT.NONE);
		
		Composite footer=new Composite(shell,SWT.NONE);
		
	}
	protected void createIrregularlyShell() {

		backgroud = PictureFactory.getImage("device_panel_bg");

		Region region = new Region();
		ImageData imageData = backgroud.getImageData();
		if (imageData.alphaData != null) {
			for (int y = 0; y < imageData.height; y++) {
				for (int x = 0; x < imageData.width; x++) {
					if (imageData.getAlpha(x, y) == 255) {
						region.add(imageData.x + x, imageData.y + y, 1, 1);
					}
				}
			}
		} else {
			ImageData mask = imageData.getTransparencyMask();
			for (int y = 0; y < mask.height; y++) {
				for (int x = 0; x < mask.width; x++) {
					if (mask.getPixel(x, y) != 0) {
						region.add(imageData.x + x, imageData.y + y, 1, 1);
					}
				}
			}
		}
		shell.setRegion(region);
		shell.setSize(imageData.x + imageData.width, imageData.y + imageData.height);
	}
}
