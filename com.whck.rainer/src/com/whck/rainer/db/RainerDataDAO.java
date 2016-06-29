package com.whck.rainer.db;

import java.sql.Date;
import java.util.List;
import com.whck.proto.model.RainerData;

public class RainerDataDAO extends BaseDAO<RainerData>{
	public static RainerDataDAO instance = null;

	public RainerDataDAO() {
		super();
	}

	public void insert(RainerData rainerData) throws Exception {
		save(rainerData);
	}

	public List<RainerData> findData(String name) throws Exception {
		return findByName(name);
	}

	public List<RainerData> findData(String name, Date fromDate, Date toDate) throws Exception {
		return findByCnd(name, fromDate, toDate);
	}

	public static RainerDataDAO getInstance() {
		if(instance ==null){
			instance = new RainerDataDAO();
		}
		return instance;
	}
}
