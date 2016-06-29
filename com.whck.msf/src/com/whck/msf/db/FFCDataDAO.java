package com.whck.msf.db;

import java.sql.Date;
import java.util.List;
import com.whck.proto.domain.FFCData;

public class FFCDataDAO extends BaseDAO<FFCData>{
	public static FFCDataDAO instance = null;

	private FFCDataDAO() {
	}

	public void insert(FFCData ffcData) throws Exception {
		save(ffcData);
	}

	public List<FFCData> findData(String name) throws Exception {
		return findByName(name);
	}

	public List<FFCData> findData(String name, Date fromDate, Date toDate) throws Exception {
		return findByCnd(name, fromDate, toDate);
	}

	public static FFCDataDAO getInstance() {
		if(instance ==null){
			instance = new FFCDataDAO();
		}
		return instance;
	}
}
