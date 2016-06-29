package com.whck.rainer.model;

import java.util.List;
/**
 * 
 * @author JSexy
 *
 */

public class Variable extends RModel{
	private boolean triggered;
	private String name;  		 	//变量名
	private List<String> types;  	//针对"雨型"类变量
	private String unit;  		 	//变量单位
	private String upLimitVal;	   	//上限值
	private String downLimitVal;	//下限值
	private boolean ctrlable;    	//可控制变量(即可下发控制的值)
	private boolean showable;		//可显示变量
	private String dbType="0";
	private  DynamicLimit dLimit;
	private String value;
	public Variable(){}
	
	public Variable(String name, List<String> types, String unit,String upLimitVal,
						String downLimitVal, boolean ctrlable,boolean showable,String dbType) {
		this.name = name;
		this.types = types;
		this.unit = unit;
		this.upLimitVal=upLimitVal;
		this.downLimitVal=downLimitVal;
		this.ctrlable = ctrlable;
		this.showable=showable;
		this.dbType=dbType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public void setParent(RModel device){
		parent=device;
	}
	public RModel getParent(){
		return parent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getTypes(){
		return this.types;
	}
	public void setTypes(List<String> types){
		this.types=types;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean isCtrlable() {
		return ctrlable;
	}
	public void setCtrlable(boolean ctrlable) {
		this.ctrlable = ctrlable;
	}
	
	public String getUpLimitVal() {
		return upLimitVal;
	}

	public void setUpLimitVal(String upLimitVal) {
		this.upLimitVal = upLimitVal;
	}

	public String getDownLimitVal() {
		return downLimitVal;
	}

	public void setDownLimitVal(String downLimitVal) {
		this.downLimitVal = downLimitVal;
	}

	public boolean isShowable(){
		return showable;
	}
	public void setShowable(boolean showable){
		this.showable=showable;
	}
	
	public DynamicLimit getdLimit() {
		return dLimit;
	}

	public void setdLimit(DynamicLimit dLimit) {
		this.dLimit = dLimit;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String toString(){
		
		return "[name:"+name+"]"+"[unit:"+unit+"]"+"[ctrlable:"+ctrlable+"]"+"[type:"+types.toString()+"]";
	}

	@Override
	public void accept(IModelVisitor visitor, Object argument) {
		visitor.visitVariable(this, argument);
	}
}
