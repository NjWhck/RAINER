package com.whck.rainer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.FileLocator;
import com.whck.rainer.Activator;
import com.whck.rainer.model.Device;
import com.whck.rainer.model.Rainer;
import com.whck.rainer.model.Variable;
import com.whck.rainer.model.Zone;

/**
 * xml文件操作
 * 
 * @author 江心
 *
 */

public class XmlUtil {
	public static XmlUtil xmlUtil=null;
	public static XmlUtil getInstance() throws Exception{
		if(xmlUtil==null){
			xmlUtil=new XmlUtil();
		}
		return xmlUtil;
	}
	private Document doc;
	public String path ;

	private XmlUtil() throws Exception {
		URL url = Activator.getDefault().getBundle().getResource("bin/rainer.xml");
		System.out.println("url:"+url);
		path = FileLocator.toFileURL(url).toString().split("/", 2)[1];
		System.out.println("xml Path:"+path);
		SAXReader saxReader = new SAXReader();
		doc = saxReader.read(new File(path));
	}

	public void write(String fileName) {

		XMLWriter output = null;

		OutputFormat format = OutputFormat.createPrettyPrint();

		format.setEncoding("UTF-8");

		try {
			output = new XMLWriter(new FileOutputStream(new File(fileName)), format);
			output.write(doc);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("写入xml出错！");
		}
	}

	// 添加节点
	public void addZone(Zone zone) {
		String zoneName = zone.getName();
		Element root = doc.getRootElement();
		Element zoneElm = root.addElement("zone");
		zoneElm.addAttribute("name", zoneName);

		write(path);

		List<Device> devices = zone.getDevices();
		System.out.println("devices:" + devices);
		for (Iterator<Device> it = devices.iterator(); it.hasNext();) {
			addDevice(zoneName, it.next());
		}

	}

	public void addDevice(String zoneName, Device dev) {
		String devId = dev.getId();
		String devName = dev.getName();
		String type=String.valueOf(dev.getType());
		String ip=dev.getIp();
		Element root = doc.getRootElement();
		Element zoneElm = (Element) root.selectSingleNode("zone[@name='" + zoneName + "']");
		System.out.println("zoneName:" + zoneName);
		Element devElm = zoneElm.addElement("device");
		devElm.addAttribute("id", devId);
		devElm.addAttribute("name", devName);
		devElm.addAttribute("type", type);
		devElm.addAttribute("ip", ip);
		write(path);

		List<Variable> Vars = dev.getVariables();

		for (Iterator<Variable> it = Vars.iterator(); it.hasNext();) {
			addVraiable(zoneName, devId, it.next());
		}
	}

	public void addVraiable(String zoneName, String devId, Variable var) {
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']";
		String varName = var.getName();
		String varUnit = var.getUnit();
		String upLimitVal = var.getUpLimitVal();
		String downLimitVal = var.getDownLimitVal();
		List<String> types = var.getTypes(); // 除了雨型，其他变量此值为空
		boolean ctrlable = var.isCtrlable();
		boolean showable = var.isShowable();
		String dbType=var.getDbType();
		Element root = doc.getRootElement();
		Element devElm = (Element) root.selectSingleNode(xPath);
		Element varElm = devElm.addElement("variable");
		varElm.addAttribute("name", varName);
		if (types != null) {
			for (Iterator<String> it = types.iterator(); it.hasNext();) {
				varElm.addElement("type").addText(it.next());
			}
		}

		varElm.addElement("unit").addText(varUnit);
		varElm.addElement("upvalue").addText(upLimitVal);
		varElm.addElement("downvalue").addText(downLimitVal);
		varElm.addElement("ctrlable").addText(ctrlable ? "true" : "false");
		varElm.addElement("showable").addText(showable ? "true" : "false");
		varElm.addElement("dbType").addText(dbType);
		write(path);
	}

	// 获取对象
	public Rainer getRainer(String rainerName) {
		Rainer rainer = null;
		String xPath = "/rainer[@name='" + rainerName + "']";
		Element root = doc.getRootElement();
		Element elem = (Element) root.selectSingleNode(xPath);
		List<Zone> zones = new ArrayList<>();
		List<Element> zoneElms = elem.elements("zone");
		for (Iterator<Element> it = zoneElms.iterator(); it.hasNext();) {
			zones.add(getZone(rainerName, it.next().attributeValue("name")));
		}
		rainer = new Rainer(rainerName, zones);
		rainer.setParent(new Rainer());
		for(Iterator<Zone> it=zones.iterator();it.hasNext();){
			it.next().setParent(rainer);
		}
		return rainer;
	}

	// 获取对象
	public Zone getZone(String rainerName, String zoneName) {
		Zone zone = null;
		String xPath = "/rainer[@name='" + rainerName + "']/zone[@name='" + zoneName + "']";
		Element root = doc.getRootElement();
		Element elem = (Element) root.selectSingleNode(xPath);
		String zoneIp = elem.attributeValue("ip");
		List<Device> devices = new ArrayList<>();
		List<Element> devElms = elem.elements("device");
		for (Iterator<Element> it = devElms.iterator(); it.hasNext();) {
			devices.add(getDevice(zoneName, it.next().attributeValue("id")));
		}
		zone = new Zone(zoneName,devices);
		for(Iterator<Device> it=devices.iterator();it.hasNext();){
			it.next().setParent(zone);
		}
//		zone.setParent(getRainer(rainerName));
		return zone;
	}

	public Device getDevice(String zoneName, String devId) {
		Device device = null;
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']";

		Element root = doc.getRootElement();
		Element elem = (Element) root.selectSingleNode(xPath);
		String devName = elem.attributeValue("name");
		String ip = elem.attributeValue("ip");
		int type = Integer.valueOf(elem.attributeValue("type"));
		List<Element> varElms = elem.elements("variable");
		List<Variable> vars = new ArrayList<>();
		Variable tempVar = null;
		for (Iterator<Element> it = varElms.iterator(); it.hasNext();) {
			tempVar = getVariable(zoneName, devId, it.next().attributeValue("name"));	
			vars.add(tempVar);
		}
		device = new Device(devId, devName,ip,type,vars);
		for(Iterator<Variable> it=vars.iterator();it.hasNext();){
			it.next().setParent(device);
		}
//		device.setParent(getZone("lab",zoneName));
		return device;
	}

	public List<Device> getDeviceByName(String zoneName, String devName) {
		List<Device> devices = new ArrayList<>();
		String xPath = "zone[@name='" + zoneName + "']/device[@name='" + devName + "']";
		Element root = doc.getRootElement();
		List<Node> nodes = root.selectNodes(xPath);
		for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
			Element elem = (Element) it.next();
			String devId = elem.attributeValue("id");
			Device device = getDevice(zoneName, devId);
//			device.setParent(getZone("lab",zoneName));
			devices.add(device);
		}
		return devices;
	}

	public Variable getVariable(String zoneName, String devId, String varName) {
		Variable var = null;		                         
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']" + "/variable[@name='" + varName+ "']";
		Element root = doc.getRootElement();
		Element elem = (Element) root.selectSingleNode(xPath);
		if (elem == null) {
			return var;
		}
		String unit = elem.elementText("unit");
		String upLimitVal = elem.elementText("upvalue");
		String downLimitVal = elem.elementText("downvalue");
		String ctrlableStr = elem.elementText("ctrlable");
		String showableStr = elem.elementText("showable");
		boolean ctrlable = ctrlableStr.equals("true") ? true : false;
		boolean showable = showableStr.equals("true") ? true : false;
		String dbType=elem.elementText("dbType");
		List<String> types = new ArrayList<String>();
		List<Element> typeElms = elem.elements("type");
		for (Iterator<Element> it = typeElms.iterator(); it.hasNext();) {
			types.add(it.next().getText());
		}
		var = new Variable(varName, types, unit, upLimitVal, downLimitVal, ctrlable, showable,dbType);
//		var.setParent(getDevice(zoneName,devId));
		return var;
	}

	// 获取名称集合...未测试
	public List<String> getZoneNames() {
		List<String> zoneNames = new ArrayList<>();
		Element root = doc.getRootElement();
		List<Element> zoneElms = root.elements("zone");
		for (Iterator<Element> it = zoneElms.iterator(); it.hasNext();) {
			zoneNames.add(it.next().attributeValue("name"));
		}
		return zoneNames;
	}

	public List<String> getDevNames(String zoneName) {
		List<String> devNames = new ArrayList<>();
		String xPath = "zone[@name ='" + zoneName + "']/device";
		Element root = doc.getRootElement();
		List<Node> devElms = root.selectNodes(xPath);
		for (Iterator<Node> it = devElms.iterator(); it.hasNext();) {
			devNames.add(((Element) it.next()).attributeValue("name"));
		}
		return devNames;
	}

	public List<String> getDevIds(String zoneName) {
		List<String> devIps = new ArrayList<>();
		String xPath = "zone[@name ='" + zoneName + "']/device";
		Element root = doc.getRootElement();
		List<Node> devElms = root.selectNodes(xPath);
		for (Iterator<Node> it = devElms.iterator(); it.hasNext();) {
			devIps.add(((Element) it.next()).attributeValue("id"));
		}
		return devIps;
	}

	public List<String> getZoneIps() {

		List<String> zoneIps = new ArrayList<>();
		Element root = doc.getRootElement();
		List<Element> zoneElms = root.elements("zone");
		for (Iterator<Element> it = zoneElms.iterator(); it.hasNext();) {
			zoneIps.add(it.next().attributeValue("ip"));
		}
		return zoneIps;
	}

	public List<String> getRainTypes(String zoneName) {
		List<String> rainTypes = new ArrayList<>();
		String xPath = "zone[@name='" + zoneName + "']//type";
		Element root = doc.getRootElement();
		List<Node> typeElms = root.selectNodes(xPath);
		for (Iterator<Node> it = typeElms.iterator(); it.hasNext();) {
			rainTypes.add(((Element) it.next()).getText());
		}
		return rainTypes;
	}

	public List<String> getVarNames(String zoneName, String devId) {
		List<String> varNames = new ArrayList<>();
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']" + "/variable";
		Element root = doc.getRootElement();
		List<Node> varElms = root.selectNodes(xPath);
		for (Iterator<Node> it = varElms.iterator(); it.hasNext();) {
			varNames.add(((Element) it.next()).attributeValue("name"));
		}
		return varNames;
	}

	public List<String> getVarUnits(String zoneName, String devId) {
		List<String> varUnits = new ArrayList<>();
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']" + "/variable";
		Element root = doc.getRootElement();
		List<Node> varElms = root.selectNodes(xPath);
		for (Iterator<Node> it = varElms.iterator(); it.hasNext();) {
			varUnits.add(((Element) it.next()).attributeValue("unit"));
		}
		return varUnits;
	}

	public void deleteZone(String zoneName) {
		String xPath = "zone[@name='" + zoneName + "']";
		Element root = doc.getRootElement();
		Element elem = (Element) root.selectSingleNode(xPath);
		root.remove(elem);
		write(path);
	}

	public void deleteVariable(String zoneName, String devId, String varName) {
		Element devElem = (Element) doc
				.selectSingleNode("rainer/zone[@name='" + zoneName + "']/device[@id='" + devId + "']");
		Element elem = (Element) devElem.selectSingleNode("variable[@name='" + varName + "']");
		devElem.remove(elem);
		write(path);
	}

	public void deleteDevice(String zoneName, String devId) {
		Element zoneElem = (Element) doc.selectSingleNode("rainer/zone[@name='" + zoneName + "']");
		Element elem = (Element) zoneElem.selectSingleNode("device[@id='" + devId + "']");
		zoneElem.remove(elem);
		write(path);
	}

	public List<Device> getDevices(String zoneName) {
		List<Device> devices = new ArrayList<>();

		String xPath = "zone[@name ='" + zoneName + "']/device";
		Element root = doc.getRootElement();
		List<Node> devElms = root.selectNodes(xPath);
		for (Iterator<Node> it = devElms.iterator(); it.hasNext();) {
			devices.add(getDevice(zoneName, ((Element) it.next()).attributeValue("id")));
		}

		return devices;
	}

	public List<Variable> getVariables(String zoneName, String devId) {
		List<Variable> vars = new ArrayList<>();
		List<String> varNames = new ArrayList<>();
		String xPath = "zone[@name='" + zoneName + "']/device[@id='" + devId + "']" + "/variable";
		Element root = doc.getRootElement();
		List<Node> varElms = root.selectNodes(xPath);

		for (Iterator<Node> it = varElms.iterator(); it.hasNext();) {
			varNames.add(((Element) it.next()).attributeValue("name"));
		}
		for (Iterator<String> it = varNames.iterator(); it.hasNext();) {
			vars.add(getVariable(zoneName, devId, it.next()));
		}
		return vars;

	}
}
