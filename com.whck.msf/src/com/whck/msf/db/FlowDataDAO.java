package com.whck.msf.db;

import java.sql.Date;
import java.util.List;

import com.whck.proto.domain.FlowData;

public class FlowDataDAO extends BaseDAO<FlowData>{
	public static FlowDataDAO instance = null;

	private FlowDataDAO() {
	}

	public void insert(FlowData flowData) throws Exception {
		save(flowData);
	}

	public List<FlowData> findData(String name) throws Exception {
		return findByName(name);
	}

	public List<FlowData> findData(String name, Date fromDate, Date toDate) throws Exception {
		return findByCnd(name, fromDate, toDate);
	}

	public static FlowDataDAO getInstance() {
		if(instance ==null){
			instance = new FlowDataDAO();
		}
		return instance;
	}
}
