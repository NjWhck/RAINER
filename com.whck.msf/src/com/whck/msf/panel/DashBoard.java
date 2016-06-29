package com.whck.msf.panel;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import com.whck.msf.model.Variable;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;

public class DashBoard extends Canvas {
	public static final int DB_SIZE=120;
	public static final int DB_PADDING=40;
	public static final int SPACING_ADJUSTED=3;
	private Variable var;
	private String value="0";
	private Image dashboard;
	private Font titleFont;
	private Font scaleFont;
	private Font valueFont;
	public DashBoard(Composite parent, int style) {
		super(parent, style);
	}
	
	public DashBoard(Composite parent, int style,Variable var) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.var=var;
		titleFont=new Font(this.getDisplay(),"微软雅黑",12,SWT.NONE);
		scaleFont=new Font(this.getDisplay(),"微软雅黑",10,SWT.NONE);
		valueFont=new Font(this.getDisplay(),"微软雅黑",10,SWT.NONE);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				DashBoard.this.paintControl(e);
			}
		});
	}

	public Variable getVar() {
		return var;
	}

	public void setVar(Variable var) {
		this.var = var;
	}

	protected void paintControl(PaintEvent e) {
		String valueString=value;
		String title= var.getName()+"("+var.getUnit()+")";
		if(value.length()>7){
			valueString=value.substring(0, 6);
		}
		if(var.getDbType().equals("0")){
			dashboard=PictureFactory.getImage("db_temp_120");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			gc.drawImage(dashboard,DB_PADDING/2, DB_PADDING/2);
			
			gc.setFont(scaleFont);
			String upSign=var.getUpLimitVal();
			Point upSignPt=gc.stringExtent(upSign);
			gc.drawString(upSign,(rectangle.width-DB_SIZE/3)/2-upSignPt.x-SPACING_ADJUSTED, DB_PADDING/2);
		
			gc.setFont(scaleFont);
			String downSign=var.getDownLimitVal();
			Point downSignPt=gc.stringExtent(downSign);
			gc.drawString(downSign,(rectangle.width-DB_SIZE/3)/2-downSignPt.x-SPACING_ADJUSTED,DB_PADDING/2+DB_SIZE-downSignPt.y);
		
			int size=((Double.valueOf(value)).intValue()-Integer.valueOf(downSign))*(DB_SIZE-2*SPACING_ADJUSTED)/(Integer.valueOf(upSign)-Integer.valueOf(downSign));
			int startY= DB_PADDING/2+DB_SIZE-2*SPACING_ADJUSTED-size;
			int startX=rectangle.width/2-2;
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.setForeground(PictureFactory.getColor("Red"));
			gc.setLineWidth(3);	
			gc.drawRectangle(startX, startY, 3, size);
			
			gc.setFont(valueFont);
			Point valueExtend=gc.stringExtent(valueString);
			
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, DB_PADDING/2+DB_SIZE);
			gc.dispose();
			
		}
			
		else if(var.getDbType().equals("1")){
			int rainType=Integer.valueOf(value);
			switch(rainType){
				case 1:
					dashboard=PictureFactory.getImage("termRain");
					valueString="小雨";
					break;
				case 2:
					dashboard=PictureFactory.getImage("dblRain");
					valueString="中雨";
					break;
				case 3:
					dashboard=PictureFactory.getImage("trpRain");
					valueString="大雨";
					break;
				case 4:
					dashboard=PictureFactory.getImage("altrRain");
					valueString="暴雨";
					break;
			}
			GC gc=e.gc;
		
			Rectangle rectangle=getBounds();
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(dashboard,DB_PADDING/2, DB_PADDING/2);
			
			gc.setFont(titleFont);
			Point valueExtend=gc.stringExtent(valueString);
			gc.setForeground(PictureFactory.getColor("Red"));
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, DB_PADDING/2+DB_SIZE);
			gc.dispose();
		}else if(var.getDbType().equals("2")){
			dashboard=PictureFactory.getImage("db_semi_120");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			Rectangle imageRect=dashboard.getBounds();
			gc.drawImage(dashboard,DB_PADDING/2, DB_PADDING/2);
			
			gc.setFont(scaleFont);
			String upSign=var.getUpLimitVal();
			Point upSignPt=gc.stringExtent(upSign);

			gc.drawString(upSign,(rectangle.width+imageRect.width-upSignPt.x)/2, rectangle.height/2+SPACING_ADJUSTED);
		
			gc.setFont(scaleFont);
			String downSign=var.getDownLimitVal();
			Point downSignPt=gc.stringExtent(downSign);
			gc.drawString(downSign,(rectangle.width-imageRect.width-downSignPt.x)/2, rectangle.height/2+SPACING_ADJUSTED);
			
			gc.setFont(valueFont);
			Point valueExtend=gc.stringExtent(valueString);
			gc.setForeground(PictureFactory.getColor("Red"));
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, imageRect.height);
			
			Transform tr = new Transform(this.getDisplay());
			float rate=(Float.valueOf(value)-Float.valueOf(var.getDownLimitVal()))/(((Float.valueOf(var.getUpLimitVal())-Float.valueOf(var.getDownLimitVal())))*1.0f);
			float angle=(float) (rate*180);
			Image pointer=PictureFactory.getImage("semi_pointer_120");
			
			tr.translate((DB_SIZE+DB_PADDING)/2.0f, (DB_SIZE+DB_PADDING)/2.0f);
	        tr.rotate(angle);
			tr.translate(-(DB_SIZE+DB_PADDING)/2.0f,-(DB_SIZE+DB_PADDING)/2.0f);
			gc.setTransform(tr);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(pointer,(rectangle.width-imageRect.width)/2,  (rectangle.height-imageRect.height)/2); 
			tr.dispose();
			gc.dispose();
		}else if(var.getDbType().equals("3")){
			dashboard=PictureFactory.getImage("db_round_120");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			gc.drawImage(dashboard,DB_PADDING/2, DB_PADDING/2);
			
			gc.setFont(valueFont);
			gc.setForeground(PictureFactory.getColor("Red"));
			Point valueExtend=gc.stringExtent(valueString);
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, DB_PADDING/2+DB_SIZE);
			Transform tr = new Transform(getDisplay());
			int order;
			if(var.getName().equals("风向")){
				List<String> windOrient=Arrays.asList(Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}else if(var.getName().equals("转角")){
				List<String> windOrient=Arrays.asList(Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}else{
				List<String> windOrient=Arrays.asList(Constants.WIND_ORIENTIONS_8);
				 order=windOrient.indexOf(value);
			}
			Image pointer=PictureFactory.getImage("round_pointer_120");
			
			order=3;
			float angle=(float) (order*45);
	        tr.translate((DB_SIZE+DB_PADDING)/2.0f, (DB_SIZE+DB_PADDING)/2.0f);
	        tr.rotate(angle);
			tr.translate(-(DB_SIZE+DB_PADDING)/2.0f,-(DB_SIZE+DB_PADDING)/2.0f);
			gc.setTransform(tr);
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.drawImage(pointer,DB_PADDING/2,  DB_PADDING/2); 
			tr.dispose();
			gc.dispose();
		}else{ //无仪表
			dashboard=PictureFactory.getImage("db_static");
			Rectangle rectangle=getBounds();
			GC gc=e.gc;
			
			gc.setFont(titleFont);
			Point titleExtend=gc.stringExtent(title);
			gc.drawString(title, (rectangle.width-titleExtend.x)/2, 0);
			
			gc.drawImage(dashboard,DB_PADDING/2, DB_PADDING/2);
			
			gc.setAdvanced(true);
			gc.setAntialias(SWT.ON);
			gc.setForeground(PictureFactory.getColor("Red"));
			
			gc.setFont(valueFont);
			Point valueExtend=gc.stringExtent(valueString);
			
			gc.drawString(valueString, (rectangle.width-valueExtend.x)/2, DB_PADDING/2+DB_SIZE);
			gc.dispose();
		}
	}
	protected void widgetDisposed(DisposeEvent e) {
		if(dashboard!=null){
			dashboard.dispose();
			dashboard=null;
		}
	}
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(DB_SIZE+DB_PADDING,DB_SIZE+DB_PADDING);
	}
	public void update(Object message) {
		String varname_cn=var.getName();
		String varname_en=Constants.cn_en_map.get(varname_cn);
		Method method=null;
		if(message instanceof FlowData){
			method=matchPojoMethods(FlowData.class,varname_en);
		}else if(message instanceof MSFData){
			method=matchPojoMethods(MSFData.class,varname_en);
		}
		Object valobj=null;
		try {
			valobj = method.invoke(message);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		this.value=String.valueOf(valobj);
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				redraw();		
			}
		});
	}
	protected Method matchPojoMethods(Class clazz, String varName) {
		Method target=null;
		Method[] methods = clazz.getMethods();
		String methodName="get"+varName.substring(0, 1).toUpperCase()+varName.substring(1);
		for (int index = 0; index < methods.length; index++) {
			Method temp=methods[index];
			if (temp.getName().equals(methodName)) {
				target=temp;
				break;
			}
		}
		return target;
	}
	
}
