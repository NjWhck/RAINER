package com.whck.msf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import com.whck.msf.db.BaseDAO;
import com.whck.msf.db.FFCDataDAO;
import com.whck.msf.db.FlowDataDAO;
import com.whck.msf.db.MSFDataDAO;
import com.whck.msf.excel.CVS;
import com.whck.msf.util.XmlUtil;
import com.whck.proto.domain.AbstractEntity;
import com.whck.proto.domain.FFCData;
import com.whck.proto.domain.FlowData;
import com.whck.proto.domain.MSFData;
import com.whck.proto.message.FFCMessage;
import com.whck.proto.message.FlowMessage;
import com.whck.proto.message.MSFMessage;

public class ChartDialog extends Dialog {
	private static Map<String, String> param_time_map = new HashMap<>();
	static {
		param_time_map.put("年", "YEAR");
		param_time_map.put("月", "MONTH");
		param_time_map.put("日", "DAY");
		param_time_map.put("时", "HOUR");
		param_time_map.put("分", "MINUTE");
	}
	private List<String> columnNames = new ArrayList<>();
	private List<String> properties = new ArrayList<>();
	private static double maxVal = 0;
	private static double minVal = 0;
	private org.eclipse.swt.graphics.Font font;
	private Label devNameLbl;
	private Combo devNameComb;
	private Label paramLbl;
	private Combo paramComb;
	private Label fromDateLbl;
	private Label toDateLbl;
	private CDateTime fromDate;
	private CDateTime toDate;
	private Label cllctDurLbl;
	private Combo cllctDurComb;
	private Button srchBtn;
	private ChartComposite chrtFrame;
	private JFreeChart jFreeChart;
	private TableViewer tableViewer;
	private Button expPlotBtn;
	private Button expXlsBtn;
	private DateTickUnit dateTickUnit;
	private BaseDAO dao;
	private Map<String,String> param_map;
	private static List avgData;

//	protected List getData(String deviceName, String fromDate, String toDate) {
//		List data = new ArrayList<>();
//		MSFEntityDAO dao = MSFEntityDAO.getInstance();
//		try {
//			data = dao.findByCnd(deviceName, fromDate, toDate);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return data;
//	}

	protected TimeSeriesCollection getAvgData(String durName, String paramName, CDateTime fromDate,
			CDateTime toDate) throws Exception {
		TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
		TimeSeries ts = null;
		String devFlag=devNameComb.getText().trim();
		String devName=devFlag.split("\\(")[0];
		String devId=devFlag.split("\\(ID:")[1].split("\\)")[0];
		if(devName.equals(FFCMessage.NAME)){
			dao=FFCDataDAO.getInstance();
			
			avgData = new ArrayList<FFCData>();
			avgData = dao.findAvg(devId,param_time_map.get(durName), param_map.get(paramName),
					formatDt(fromDate), formatDt(toDate));

			if (paramName.equals("所有") || paramName.equals("流速")) {
				ts = new TimeSeries("流速");
				for (int i = 0; i < avgData.size(); i++) {
					FFCData avg = (FFCData) avgData.get(i);
					float rainintn = avg.getFlowrate();
					if (rainintn > maxVal)
						maxVal = rainintn;
					if (rainintn < minVal)
						minVal = rainintn;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainintn);
				}
				seriesCollection.addSeries(ts);
			}
			if (paramName.equals("所有") || paramName.equals("流量")) {
				ts = new TimeSeries("流量");
				for (int i = 0; i < avgData.size(); i++) {
					FFCData avg = (FFCData) avgData.get(i);
					float rainfall = avg.getFlowaccm();
					if (rainfall > maxVal)
						maxVal = rainfall;
					if (rainfall < minVal)
						minVal = rainfall;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainfall);
				}
				seriesCollection.addSeries(ts);
			}
			if (paramName.equals("所有") || paramName.equals("水位")) {
				ts = new TimeSeries("水位");
				for (int i = 0; i < avgData.size(); i++) {
					FFCData avg = (FFCData) avgData.get(i);
					float pressure = avg.getWaterlvl();
					if (pressure > maxVal)
						maxVal = pressure;
					if (pressure < minVal)
						minVal = pressure;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							pressure);
				}
				seriesCollection.addSeries(ts);
			}
		}else if(devName.equals(MSFMessage.NAME)){
			dao=MSFDataDAO.getInstance();
			avgData = new ArrayList<MSFData>();
			avgData = dao.findAvg(devId,param_time_map.get(durName), param_map.get(paramName),
					formatDt(fromDate), formatDt(toDate));

			if (paramName.equals("所有") || paramName.equals("泥沙含量")) {
				ts = new TimeSeries("泥沙含量");
				for (int i = 0; i < avgData.size(); i++) {
					MSFData avg = (MSFData) avgData.get(i);
					float rainintn = avg.getSedcharge();
					if (rainintn > maxVal)
						maxVal = rainintn;
					if (rainintn < minVal)
						minVal = rainintn;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainintn);
				}
				seriesCollection.addSeries(ts);
			}
			if (paramName.equals("所有") || paramName.equals("累积流量")) {
				ts = new TimeSeries("累积流量");
				for (int i = 0; i < avgData.size(); i++) {
					MSFData avg = (MSFData) avgData.get(i);
					float rainfall = avg.getFlowaccm();
					if (rainfall > maxVal)
						maxVal = rainfall;
					if (rainfall < minVal)
						minVal = rainfall;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainfall);
				}
				seriesCollection.addSeries(ts);
			}
			if (paramName.equals("所有") || paramName.equals("累积含量")) {
				ts = new TimeSeries("累积含量");
				for (int i = 0; i < avgData.size(); i++) {
					MSFData avg = (MSFData) avgData.get(i);
					float pressure = avg.getSedaccm();
					if (pressure > maxVal)
						maxVal = pressure;
					if (pressure < minVal)
						minVal = pressure;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							pressure);
				}
				seriesCollection.addSeries(ts);
			}
		}else if(devName.equals(FlowMessage.NAME)){
			dao=FlowDataDAO.getInstance();
			avgData = new ArrayList<FlowData>();
			avgData = dao.findAvg(devId,param_time_map.get(durName), param_map.get(paramName),
					formatDt(fromDate), formatDt(toDate));
			if (paramName.equals("所有") || paramName.equals("流速")) {
				ts = new TimeSeries("流速");
				for (int i = 0; i < avgData.size(); i++) {
					FlowData avg = (FlowData) avgData.get(i);
					float rainintn = avg.getFlowrate();
					if (rainintn > maxVal)
						maxVal = rainintn;
					if (rainintn < minVal)
						minVal = rainintn;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainintn);
				}
				seriesCollection.addSeries(ts);
			}
			if (paramName.equals("所有") || paramName.equals("累积流量")) {
				ts = new TimeSeries("累积流量");
				for (int i = 0; i < avgData.size(); i++) {
					FlowData avg = (FlowData) avgData.get(i);
					float rainfall = avg.getFlowaccm();
					if (rainfall > maxVal)
						maxVal = rainfall;
					if (rainfall < minVal)
						minVal = rainfall;
					ts.add(new Minute(avg.getMinute(), avg.getHour(), avg.getDay(), avg.getMonth(), avg.getYear()),
							rainfall);
				}
				seriesCollection.addSeries(ts);
			}
		}else if(devName.equals("卡口站")){
			
		}
		

		return seriesCollection;
	}

	@Override
	public Control createDialogArea(Composite parent) {
		XmlUtil xmlUtil = null;
		try {
			xmlUtil = XmlUtil.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> devNameAndId=xmlUtil.mergeAllDevNameAndId();
		GridLayout layout = new GridLayout(4, true);

		parent.setLayout(layout);
		layout.verticalSpacing = 10;
		layout.marginBottom = 0;

		GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		devNameLbl = new Label(parent, SWT.NONE);
		devNameLbl.setLayoutData(gridData);
		devNameLbl.setText("设备名");
		devNameLbl.setFont(font);
		devNameLbl.setAlignment(SWT.CENTER);

		devNameComb = new Combo(parent, SWT.BORDER);
		devNameComb.setLayoutData(gridData);
		devNameComb.setItems(devNameAndId.toArray(new String[0]));
		
		paramLbl = new Label(parent, SWT.NONE);
		paramLbl.setText("测量值");
		paramLbl.setLayoutData(gridData);
		paramLbl.setFont(font);
		paramLbl.setAlignment(SWT.CENTER);
		paramComb = new Combo(parent, SWT.NONE);
		paramComb.select(1);
		paramComb.setLayoutData(gridData);
		fromDateLbl = new Label(parent, SWT.NONE);
		fromDateLbl.setLayoutData(gridData);
		fromDateLbl.setText("起始时刻");
		fromDateLbl.setFont(font);
		fromDateLbl.setAlignment(SWT.CENTER);
		fromDate = new CDateTime(parent, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
		fromDate.setPattern("yyyy-MM-dd HH:mm:ss");
		fromDate.setSelection(new Date());
		fromDate.setLayoutData(gridData);
		toDateLbl = new Label(parent, SWT.NONE);
		toDateLbl.setLayoutData(gridData);
		toDateLbl.setText("截止时刻");
		toDateLbl.setFont(font);
		toDateLbl.setAlignment(SWT.CENTER);
		toDate = new CDateTime(parent, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN);
		toDate.setPattern("yyyy-MM-dd HH:mm:ss");
		toDate.setSelection(new Date());
		toDate.setLayoutData(gridData);

		cllctDurLbl = new Label(parent, SWT.NONE);
		cllctDurLbl.setText("时间单位");
		cllctDurLbl.setFont(font);
		cllctDurLbl.setAlignment(SWT.CENTER);
		cllctDurLbl.setLayoutData(gridData);
		cllctDurComb = new Combo(parent, SWT.BORDER);
		cllctDurComb.setItems(new String[] { "年", "月", "日", "时", "分" });
		cllctDurComb.setLayoutData(gridData);
		cllctDurComb.select(0);
		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		srchBtn = new Button(parent, SWT.NONE);
		srchBtn.setText("确定");
		srchBtn.setFont(font);
		srchBtn.setAlignment(SWT.CENTER);
		srchBtn.setLayoutData(gridData);
		srchBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (checkDate()) {
					new LoadHandler().start();
				} else {
					MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
					box.setText("提示");
					box.setMessage("时间选择有误");
					box.open();
					return;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		try {
			jFreeChart = createChart(null, cllctDurComb.getText(), paramComb.getText());
			jFreeChart.setBackgroundPaint(Color.getHSBColor(184, 112, 112));
			chrtFrame = new ChartComposite(parent, SWT.NONE, jFreeChart);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 4;
		gridData.minimumHeight = 280;
		chrtFrame.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 4;
		gridData.minimumHeight = 220;
		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ContentProvider());
		devNameComb.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String deviceName=devNameComb.getText().split("\\(")[0];
				System.out.println(deviceName);
				if(deviceName.equals(FFCMessage.NAME)){
					param_map=FFCMessage.param_map;
					paramComb.setItems(param_map.keySet().toArray(new String[0]));
					String[] columnNms = { "   设备名      ","    设备ID   ", "    流速(L/min)    ", "   累积流量(L)   ",
							"   水位(cm)   ","                  时间               " };
					String[] pros = { "name","id", "flowrate", "flowaccm", "waterlvl","date_time" };
					columnNames=Arrays.asList(columnNms);
					properties=Arrays.asList(pros);
				}else if(deviceName.equals(MSFMessage.NAME)){
					param_map=MSFMessage.param_map;
					paramComb.setItems(param_map.keySet().toArray(new String[0]));
					String[] columnNms = { "   设备名     ", "    设备ID   ", "   泥沙含量(g)   ", "   累积含量(%)   ",
							"   累积流量(L)   ","                  时间               " };
					String[] pros = { "name","id", "sedcharge", "sedaccm", "flowaccm","date_time" };
					columnNames=Arrays.asList(columnNms);
					properties=Arrays.asList(pros);
				}else if(deviceName.equals(FlowMessage.NAME)){
					param_map=FlowMessage.param_map;
					paramComb.setItems(param_map.keySet().toArray(new String[0]));
					String[] columnNms = { "   设备名    ", "    设备ID   ","   流速(L/min)   ", "   累积流量(L)   ","                  时间               " };
					String[] pros = { "name", "id","flowrate", "flowaccm","date_time" };
					columnNames=Arrays.asList(columnNms);
					properties=Arrays.asList(pros);
				}
				tableViewer.setColumnProperties(properties.toArray(new String[0]));
				Table table = tableViewer.getTable();
				for (int i = 0; i < columnNames.size(); i++) {
					TableColumn tableColumn = table.getColumn(i);
					tableColumn.setText(columnNames.get(i));	
					table.getColumn(i).pack();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Table table = tableViewer.getTable();
		for (int i = 0; i < 6; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setText("");
			table.getColumn(i).pack();
		}

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gridData.horizontalSpan = 2;
		gridData.minimumHeight = 30;
		gridData.widthHint = 130;
		expPlotBtn = new Button(parent, SWT.PUSH);
		expPlotBtn.setText("导出图片");
		expPlotBtn.setFont(font);
		expPlotBtn.setAlignment(SWT.CENTER);
		expPlotBtn.setLayoutData(gridData);
		expPlotBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog export = new FileDialog(parent.getShell(), SWT.SAVE);
				String filePath = export.open();
				exp2Picture(export, filePath, jFreeChart);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		expXlsBtn = new Button(parent, SWT.PUSH);
		expXlsBtn.setText("导出表格");
		expXlsBtn.setFont(font);
		expXlsBtn.setAlignment(SWT.CENTER);
		expXlsBtn.setLayoutData(gridData);
		expXlsBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog export = new FileDialog(parent.getShell(), SWT.SAVE);
				String filePath = export.open();
				try {
					//CVS.saveFile(avgData, filePath);
					 exp2Excel(filePath, avgData);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return parent;
	}

	protected JFreeChart createChart(XYDataset xydataset, String duration, String valueName) {
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("曲线图", null, "数值", xydataset, true, true, true);
		TextTitle textTitle = jfreechart.getTitle();
		textTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));

		LegendTitle legend = jfreechart.getLegend();
		if (legend != null) {
			legend.setPadding(0, 0, 6, 2);
			legend.setMargin(-2, 0, 5, 0);
			legend.setItemFont(new Font("微软雅黑", Font.PLAIN, 12));
		}

		XYPlot xyplot = jfreechart.getXYPlot();
		xyplot.setBackgroundPaint(new Color(255, 253, 246));
		xyplot.setOutlineStroke(new BasicStroke(1.5f));
		xyplot.setRangeGridlinesVisible(true);
		xyplot.setDomainGridlinesVisible(true);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setNoDataMessage("没有数据");
		xyplot.setNoDataMessageFont(new Font("微软雅黑", Font.BOLD, 14));
		xyplot.setNoDataMessagePaint(new Color(87, 149, 117));
		xyplot.setWeight(100);
		xyplot.zoom(20);

		DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		dateaxis.setTickMarkPosition(DateTickMarkPosition.START);
		dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy")));

		ValueAxis valueAxis = xyplot.getRangeAxis();
		valueAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
		valueAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 10));

		valueAxis.setUpperBound(10);
		valueAxis.setAutoRangeMinimumSize(0.1);
		valueAxis.setLowerBound(-10);

		valueAxis.setAutoRange(true);
		valueAxis.setAxisLineStroke(new BasicStroke(1.5f));
		valueAxis.setAxisLinePaint(new Color(215, 215, 215));
		valueAxis.setLabelPaint(new Color(10, 10, 10));
		valueAxis.setTickLabelPaint(new Color(102, 102, 102));

		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
		xylineandshaperenderer.setBaseShapesVisible(true);
		xylineandshaperenderer.setBaseLinesVisible(true);
		StandardXYToolTipGenerator xytool = new StandardXYToolTipGenerator();
		xylineandshaperenderer.setBaseToolTipGenerator((XYToolTipGenerator) xytool);
		xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(1.5f));

		return jfreechart;
	}

	protected void refresh(JFreeChart chart, XYDataset dataset) {
		XYPlot plot = (XYPlot) chart.getPlot();
		if (cllctDurComb.getText().equals("年")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy"));
		} else if (cllctDurComb.getText().equals("月")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.MONTH, 1, new SimpleDateFormat("yyyy-MM"));
		} else if (cllctDurComb.getText().equals("日")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat("MM-dd"));
		} else if (cllctDurComb.getText().equals("时")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.HOUR, 1, new SimpleDateFormat("dd-HH"));
		} else if (cllctDurComb.getText().equals("分")) {
			dateTickUnit = new DateTickUnit(DateTickUnitType.MINUTE, 1, new SimpleDateFormat("HH:mm"));
		} else {
			dateTickUnit = new DateTickUnit(DateTickUnitType.SECOND, 1, new SimpleDateFormat("mm:ss"));
		}
		ValueAxis valueAxis = plot.getRangeAxis();

		valueAxis.setUpperBound(maxVal);
		valueAxis.setAutoRangeMinimumSize(0.1);
		valueAxis.setLowerBound(minVal);

		DateAxis dateaxis = (DateAxis) plot.getDomainAxis();
		dateaxis.setTickUnit(dateTickUnit);
		plot.setDataset(dataset);
	}

	protected String formatDt(DateTime dt) {
		return dt.getYear() + "-" + dt.getMonth() + "-" + dt.getDay() + " " + dt.getHours() + ":" + dt.getMinutes()
				+ ":" + dt.getSeconds();
	}

	protected String formatDt(CDateTime dt) {
		return dt.getText();
	}

	protected boolean checkDate() {
		Date fromdt = fromDate.getSelection();
		Date todt = toDate.getSelection();

		if (fromdt.getTime() > todt.getTime()) {
			return false;
		}
		return true;
	}

	class LoadHandler extends Thread {
		public void run() {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					TimeSeriesCollection collection = null;
					try {
						collection = getAvgData(cllctDurComb.getText(), paramComb.getText(),
								fromDate, toDate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					refresh(jFreeChart, collection);
					tableViewer.getTable().clearAll();
					tableViewer.setInput(avgData);
				}
			});
		}
	}

	class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String devFlag=devNameComb.getText().trim();
			String devName=devFlag.split("\\(")[0];
			String result = "";
			if(devName.equals(MSFMessage.NAME)){
				MSFData o = (MSFData) element;
				String name=devFlag.split("\\(")[0];
				String id=devFlag.split("\\(ID:")[1].split("\\)")[0];
				switch (columnIndex) {
				case 0:
					result = name;
					o.setName(name);
					break;
				case 1:
					result = id;
					o.setId(Integer.valueOf(id));
					break;
				case 2:
					result = String.valueOf(o.getSedcharge());
					break;
				case 3:
					result = String.valueOf(o.getSedaccm());
					break;
				case 4:
					result = String.valueOf(o.getFlowaccm());
					break;
				case 5:
					if (cllctDurComb.getText().equals("年")) {
						result = o.getYear()+"年";
					} else if (cllctDurComb.getText().equals("月")) {
						result = o.getYear()+"年"+o.getMonth()+"月";
					} else if (cllctDurComb.getText().equals("日")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日";
					} else if (cllctDurComb.getText().equals("时")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时";
					} else if (cllctDurComb.getText().equals("分")) {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					} else {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					}
					o.setDate_time(result);
					break;
				default:
					result = "";
				}
			}else if(devName.equals(FFCMessage.NAME)){
				FFCData o = (FFCData) element;
				String name=devFlag.split("\\(")[0];
				String id=devFlag.split("\\(ID:")[1].split("\\)")[0];
				switch (columnIndex) {
				case 0:
					result = name;
					o.setName(name);
					break;
				case 1:
					result = id;
					o.setId(Integer.valueOf(id));
					break;
				case 2:
					result = String.valueOf(o.getFlowrate());
					break;
				case 3:
					result = String.valueOf(o.getFlowaccm());
					break;
				case 4:
					result = String.valueOf(o.getWaterlvl());
					break;
				case 5:
					if (cllctDurComb.getText().equals("年")) {
						result = o.getYear()+"年";
					} else if (cllctDurComb.getText().equals("月")) {
						result = o.getYear()+"年"+o.getMonth()+"月";
					} else if (cllctDurComb.getText().equals("日")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日";
					} else if (cllctDurComb.getText().equals("时")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时";
					} else if (cllctDurComb.getText().equals("分")) {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					} else {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					}
					o.setDate_time(result);
					break;
				default:
					result = "";
				}
			}else if(devName.equals(FlowMessage.NAME)){
				FlowData o = (FlowData) element;
				String name=devFlag.split("\\(")[0];
				String id=devFlag.split("\\(ID:")[1].split("\\)")[0];
				switch (columnIndex) {
				case 0:
					result = name;
					o.setName(name);
					break;
				case 1:
					result = id;
					o.setId(Integer.valueOf(id));
					break;
				case 2:
					result = String.valueOf(o.getFlowrate());
					break;
				case 3:
					result = String.valueOf(o.getFlowaccm());
					break;
				case 4:
					if (cllctDurComb.getText().equals("年")) {
						result = o.getYear()+"年";
					} else if (cllctDurComb.getText().equals("月")) {
						result = o.getYear()+"年"+o.getMonth()+"月";
					} else if (cllctDurComb.getText().equals("日")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日";
					} else if (cllctDurComb.getText().equals("时")) {
						result =  o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时";
					} else if (cllctDurComb.getText().equals("分")) {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					} else {
						result = o.getYear()+"年"+o.getMonth()+"月"+o.getDay()+"日"+o.getHour()+"时"+o.getMinute()+"分";
					}
					o.setDate_time(result);
					break;
				default:
					result = "";
				}
			}
			
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected void exp2Picture(FileDialog dialog, String filePath, JFreeChart chart) {
		if (!(filePath.endsWith(".png") || filePath.endsWith(".jpg"))) {
			filePath += ".jpg";
		}
		try {
			ChartUtilities.saveChartAsPNG(new File(filePath), chart, 1200, 350);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected void exp2Excel(String filePath, List rainData) throws Exception {
		@SuppressWarnings("resource")
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet = hwb.createSheet("数据表格");
		HSSFCellStyle style = hwb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = hwb.createFont();
		
		font.setFontHeightInPoints((short) 26);
		font.setFontName("Courier New"); 
		style.setFont(font);
		HSSFRow firstrow = sheet.createRow(0); 
		HSSFCell firstCell = firstrow.createCell(6);
		firstCell.setCellStyle(style);
		String devFlag=devNameComb.getText();
		String devName=devFlag.split("\\(")[0];
		firstCell.setCellValue(devNameComb.getText());
		
		String[] valNames = new String[9];
		if(devName.equals(MSFMessage.NAME)){
			valNames[0] = "设备名";
			valNames[1] = "ID";
			valNames[2] = "泥沙含量(g)";
			valNames[3] = "累积含量(%)";
			valNames[4] = "累积流量(L)";
			valNames[5] = "日期";
			HSSFRow secondrow = sheet.createRow(1);
			HSSFCell secondcell[] = new HSSFCell[6];
			for (int j = 0; j < 6; j++) {
				secondcell[j] = secondrow.createCell(j);
				secondcell[j].setCellValue(new HSSFRichTextString(valNames[j]));
			}
			for (int i = 2; i < rainData.size() + 2; i++) {
				HSSFRow row = sheet.createRow(i);
				MSFData rainRecd = (MSFData) rainData.get(i - 2);
				HSSFCell type = row.createCell(0);
				type.setCellValue(rainRecd.getName());
				HSSFCell intn = row.createCell(1);
				intn.setCellValue(rainRecd.getId());
				HSSFCell fall = row.createCell(2);
				fall.setCellValue(rainRecd.getSedcharge());
				HSSFCell pressure = row.createCell(3);
				pressure.setCellValue(rainRecd.getSedaccm());
				HSSFCell flowaccm = row.createCell(4);
				flowaccm.setCellValue(rainRecd.getFlowaccm());
				HSSFCell date = row.createCell(5);
				date.setCellValue(rainRecd.getDate_time());
			}
		}else if(devName.equals(FlowMessage.NAME)){
			valNames[0] = "设备名";
			valNames[1] = "ID";
			valNames[2] = "流速(L/min)";
			valNames[3] = "累积流量(L)";
			valNames[4] = "日期";

			HSSFRow secondrow = sheet.createRow(1);
			HSSFCell secondcell[] = new HSSFCell[5];
			for (int j = 0; j < 5; j++) {
				secondcell[j] = secondrow.createCell(j);
				secondcell[j].setCellValue(new HSSFRichTextString(valNames[j]));
			}
			for (int i = 2; i < rainData.size() + 2; i++) {
				HSSFRow row = sheet.createRow(i);
				FlowData rainRecd = (FlowData) rainData.get(i - 2);
				HSSFCell type = row.createCell(0);
				type.setCellValue(rainRecd.getName());
				HSSFCell intn = row.createCell(1);
				intn.setCellValue(rainRecd.getId());
				HSSFCell fall = row.createCell(2);
				fall.setCellValue(rainRecd.getFlowrate());
				HSSFCell pressure = row.createCell(3);
				pressure.setCellValue(rainRecd.getFlowaccm());
				HSSFCell date = row.createCell(4);
				date.setCellValue(rainRecd.getDate_time());
			}
		}
		
		
		
		if (!(filePath.endsWith(".xls"))) {
			filePath += ".xls";
		}
		OutputStream out = new FileOutputStream(filePath);
		hwb.write(out);
		out.close();
	}

	public ChartDialog(Shell parentShell) {
		super(parentShell);
		font = new org.eclipse.swt.graphics.Font(Display.getCurrent(), "微软雅黑", 14, SWT.NONE);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.MIN;
	}
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("数据分析");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(1100, 860);
	}
}
