package com.whck.msf.db;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BaseDAO<T> {

	JDBCUtil conn = new JDBCUtil();
	protected Connection connection = null;

	protected Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public BaseDAO() {
		initConnection();
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		persistentClass = (Class<T>) type.getActualTypeArguments()[0];
	}

	public void initConnection() {
		connection = conn.getConnection();
	}

	public void close() {
		conn.closeConn();
	}

	public void save(T entity) throws Exception {
		String sql = "insert into " + entity.getClass().getSimpleName().toLowerCase() + "(";

		List<Method> list = this.matchPojoMethods(entity, "get");

		Iterator<Method> iter = list.iterator();

		while (iter.hasNext()) {
			Method method = iter.next();
			sql += method.getName().substring(3).toLowerCase() + ",";
		}

		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values(";

		for (int j = 0; j < list.size(); j++) {
			sql += "?,";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";

		System.out.println(sql);

		PreparedStatement statement = connection.prepareStatement(sql);

		int i = 0;
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			if (method.getReturnType().getSimpleName().indexOf("String") != -1) {
				statement.setString(++i, this.getString(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("float") != -1) {
				statement.setFloat(++i, this.getFloat(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("double") != -1) {
				statement.setDouble(++i, this.getDouble(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("int") != -1) {
				statement.setInt(++i, this.getInt(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("Date") != -1) {
				statement.setDate(++i, this.getDate(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("InputStream") != -1) {
				statement.setAsciiStream(++i, this.getBlob(method, entity), 1440);
			} else {
				statement.setInt(++i, this.getInt(method, entity));
			}
		}
		conn.execUpdate(statement);
		statement.close();
	}

	public void update(T entity) throws Exception {
		String sql = "update " + entity.getClass().getSimpleName().toLowerCase() + " set ";

		List<Method> list = this.matchPojoMethods(entity, "get");

		Method tempMethod = null;

		Method idMethod = null;
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				idMethod = tempMethod;
				iter.remove();
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			sql += tempMethod.getName().substring(3).toLowerCase() + "= ?,";
		}

		sql = sql.substring(0, sql.lastIndexOf(","));

		sql += " where " + idMethod.getName().substring(3).toLowerCase() + " = ?";

		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);

		int i = 0;
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			if (method.getReturnType().getSimpleName().indexOf("String") != -1) {
				statement.setString(++i, this.getString(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("Date") != -1) {
				statement.setDate(++i, this.getDate(method, entity));
			} else if (method.getReturnType().getSimpleName().indexOf("InputStream") != -1) {
				statement.setAsciiStream(++i, this.getBlob(method, entity), 1440);
			} else {
				statement.setInt(++i, this.getInt(method, entity));
			}
		}

		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		statement.executeUpdate();

		statement.close();
	}

	public void delete(T entity) throws Exception {
		String sql = "delete from " + entity.getClass().getSimpleName().toLowerCase() + " where ";

		Method idMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "get");
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				idMethod = tempMethod;
				iter.remove();
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		sql += idMethod.getName().substring(3).toLowerCase() + " = ?";

		PreparedStatement statement = this.connection.prepareStatement(sql);

		int i = 0;
		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		conn.execUpdate(statement);
		statement.close();
	}

	public List<T> findByCnd(Object object, Object fromDate, Object toDate) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		T entity = persistentClass.newInstance();

		Method nameMethod = null;
		Method timeMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Id") != -1) {
				nameMethod = tempMethod;
			} else if (tempMethod.getName().indexOf("Date_time") != -1) {
				timeMethod = tempMethod;
			}
		}
		sql += (nameMethod.getName().substring(3, 4).toLowerCase() + nameMethod.getName().substring(4)).toLowerCase()
				+ " = ?";
		String fieldStr = (timeMethod.getName().substring(3, 4).toLowerCase() + timeMethod.getName().substring(4))
				.toLowerCase();
		sql += " and " + fieldStr + " between ?  and  ? ";
		System.out.println(sql);
		PreparedStatement statement = this.connection.prepareStatement(sql);

		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}
		if (fromDate instanceof Date) {
			statement.setDate(2, (Date) fromDate);
			statement.setDate(3, (Date) toDate);
		} else if (fromDate instanceof String) {
			statement.setString(2, (String) fromDate);
			statement.setString(3, (String) toDate);
		}

		ResultSet rs = conn.execQuery(statement);

		iter = list.iterator();

		while (rs.next()) {
			entity = persistentClass.newInstance();
			iter = list.iterator();
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
					this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}

		rs.close();

		statement.close();
		return entities;
	}

	public List<T> findByName(Object object) throws Exception {

		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		T entity = persistentClass.newInstance();

		Method idMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Zonename") != -1 && tempMethod.getName().substring(3).length() == 8) {
				idMethod = tempMethod;
			} else if ((entity.getClass().getSimpleName() + "Name")
					.equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
			}
		}
		sql += (idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4)).toLowerCase()
				+ " = ?";

		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);

		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		ResultSet rs = conn.execQuery(statement);

		iter = list.iterator();

		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}

		rs.close();

		statement.close();

		return entities;
	}

	public T findById(Object object) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";

		T entity = persistentClass.newInstance();

		Method idMethod = null;
		
		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				idMethod = tempMethod;
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
			}
		}
		sql += idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4) + " = ?";

		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);

		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		ResultSet rs = conn.execQuery(statement);

		iter = list.iterator();

		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					this.setString(method, entity, rs.getString(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("Date") != -1) {
					this.setDate(method, entity, rs.getDate(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("InputStream") != -1) {
					this.setBlob(method, entity,
							rs.getBlob(method.getName().substring(3).toLowerCase()).getBinaryStream());
				} else {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
		}

		rs.close();

		statement.close();

		return entity;
	}

	public List<T> findAvg(String timeunit, String field, String fromDate, String toDate) throws Exception {

		System.out.println("TimeUnit:"+timeunit);
		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(date_time as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day, DATEPART(hour, cast(date_time as datetime)) hour,";
		} else {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day, DATEPART(hour, cast(date_time as datetime)) hour,DATEPART(minute, cast(date_time as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					targetMethods.add(method.getName().substring(3).toLowerCase());
				}
			}

			for (int i = 0; i < targetMethods.size(); i++) {
				String tField = targetMethods.get(i);
				sql += " AVG(" + tField + ") " + tField + " ";
				if (i != targetMethods.size() - 1) {
					sql += ",";
				}  
			}
		} else {
			sql += " AVG(" + field + ") " + field + " ";
		}

		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where date_time between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(date_time as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)) , DAY(cast(date_time as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)), DAY(cast(date_time as datetime)) , DATEPART(hour, cast(date_time as datetime)) ";
		} else {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)), DAY(cast(date_time as datetime)) , DATEPART(hour, cast(date_time as datetime)) ,DATEPART(minute, cast(date_time as datetime)) ";
		}
		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		ResultSet rs = conn.execQuery(statement);
		list = this.matchPojoInheridMethods(entity, "set");
		iter = list.iterator();

		while (rs.next()) {
			entity = persistentClass.newInstance();
			while (iter.hasNext()) {
				Method method = iter.next();
				try{
					rs.findColumn(method.getName().substring(3).toLowerCase());
					if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
						this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
					} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
						this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
					}else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
						this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
					}
				}catch(Exception e){
					
				}
			}
			entities.add(entity);
			iter = list.iterator();
		}
		rs.close();
		statement.close();
		return entities;
	}
	public List<T> findAvg(String devId,String timeunit, String field, String fromDate, String toDate) throws Exception {
		int id=Integer.valueOf(devId);
		System.out.println("TimeUnit:"+timeunit);
		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(date_time as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day, DATEPART(hour, cast(date_time as datetime)) hour,";
		} else {
			sql += " year(cast(date_time as datetime)) year, MONTH(cast(date_time as datetime)) month, DAY(cast(date_time as datetime)) day, DATEPART(hour, cast(date_time as datetime)) hour,DATEPART(minute, cast(date_time as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					targetMethods.add(method.getName().substring(3).toLowerCase());
				}
			}

			for (int i = 0; i < targetMethods.size(); i++) {
				String tField = targetMethods.get(i);
				sql += " AVG(" + tField + ") " + tField + " ";
				if (i != targetMethods.size() - 1) {
					sql += ",";
				}  
			}
		} else {
			sql += " AVG(" + field + ") " + field + " ";
		}
		
		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where id = "+devId+" and date_time between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(date_time as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)) , DAY(cast(date_time as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)), DAY(cast(date_time as datetime)) , DATEPART(hour, cast(date_time as datetime)) ";
		} else {
			sql += " group by year(cast(date_time as datetime)), MONTH(cast(date_time as datetime)), DAY(cast(date_time as datetime)) , DATEPART(hour, cast(date_time as datetime)) ,DATEPART(minute, cast(date_time as datetime)) ";
		}
		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		ResultSet rs = conn.execQuery(statement);
		list = this.matchPojoInheridMethods(entity, "set");
		iter = list.iterator();

		while (rs.next()) {
			entity = persistentClass.newInstance();
			while (iter.hasNext()) {
				Method method = iter.next();
				try{
					rs.findColumn(method.getName().substring(3).toLowerCase());
					if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
						this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
					} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
						this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
					}else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
						this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
					}
				}catch(Exception e){
					
				}
			}
			entities.add(entity);
			iter = list.iterator();
		}
		rs.close();
		statement.close();
		return entities;
	}
	protected List<Method> matchPojoMethods(T entity, String methodName) {
		Method[] methods = entity.getClass().getDeclaredMethods();

		List<Method> list = new ArrayList<Method>();

		for (int index = 0; index < methods.length; index++) {
			if (methods[index].getName().indexOf(methodName) != -1) {
				list.add(methods[index]);
			}
		}
		return list;
	}
	
	protected List<Method> matchPojoInheridMethods(T entity, String methodName) {
		List<Method> methods =getDeclaredMethods(entity);

		List<Method> list = new ArrayList<Method>();

		for(Iterator<Method> it=methods.iterator();it.hasNext();){
			Method m=it.next();
			if (m.getName().indexOf(methodName) != -1) {
				list.add(m);
			}
		}
		return list;
	}

	public Integer getInt(Method method, T entity) throws Exception {
		return (Integer) method.invoke(entity, new Object[] {});
	}

	public float getFloat(Method method, T entity) throws Exception {
		return (float) method.invoke(entity, new Object[] {});
	}

	public double getDouble(Method method, T entity) throws Exception {
		return (double) method.invoke(entity, new Object[] {});
	}

	public String getString(Method method, T entity) throws Exception {
		return (String) method.invoke(entity, new Object[] {});
	}

	public InputStream getBlob(Method method, T entity) throws Exception {
		return (InputStream) method.invoke(entity, new Object[] {});
	}

	public Date getDate(Method method, T entity) throws Exception {
		return (Date) method.invoke(entity, new Object[] {});
	}

	public void setInt(Method method, T entity, Integer arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	public void setFloat(Method method, T entity, float arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	public void setDouble(Method method, T entity, double arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	public void setString(Method method, T entity, String arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	public void setBlob(Method method, T entity, InputStream arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	public void setDate(Method method, T entity, Date arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}
	
      
    protected  List<Method> getDeclaredMethods(T entity){  
        List<Method> methods = new ArrayList<>() ;  
          
        for(Class<?> clazz = entity.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
            try {  
            	Method[] tempMethods=clazz.getDeclaredMethods() ;;
                methods.addAll(Arrays.asList(tempMethods));  
            } catch (Exception e) {  
              
            }  
        }  
          
        return methods;  
    }  
}
