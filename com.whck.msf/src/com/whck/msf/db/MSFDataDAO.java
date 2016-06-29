package com.whck.msf.db;

import java.sql.Date;
import java.util.List;
import com.whck.proto.domain.MSFData;

public class MSFDataDAO extends BaseDAO<MSFData>{
	public static MSFDataDAO instance = null;

	private MSFDataDAO() {
	}

	public void insert(MSFData msfData) throws Exception {
		save(msfData);
	}

	public List<MSFData> findData(String name) throws Exception {
		return findByName(name);
	}

	public List<MSFData> findData(String name, Date fromDate, Date toDate) throws Exception {
		return findByCnd(name, fromDate, toDate);
	}

	public static MSFDataDAO getInstance() {
		if(instance ==null){
			instance = new MSFDataDAO();
		}
		return instance;
	}
}
