package com.whck.rainer.db;

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
		// 获得参数化类型
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		persistentClass = (Class<T>) type.getActualTypeArguments()[0];
	}

	/**
	 * 获得数据库连接
	 */
	public void initConnection() {
		connection = conn.getConnection();
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		conn.closeConn();
	}

	/**
	 * 保存
	 */
	public void save(T entity) throws Exception {
		// SQL语句,insert into table name (
		String sql = "insert into " + entity.getClass().getSimpleName().toLowerCase() + "(";

		// 获得带有字符串get的所有方法的对象
		List<Method> list = this.matchPojoMethods(entity, "get");

		Iterator<Method> iter = list.iterator();

		// 拼接字段顺序 insert into table name(id,name,email,
		while (iter.hasNext()) {
			Method method = iter.next();
			sql += method.getName().substring(3).toLowerCase() + ",";
		}

		// 去掉最后一个,符号insert insert into table name(id,name,email) values(
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values(";

		// 拼装预编译SQL语句insert insert into table name(id,name,email) values(?,?,?,
		for (int j = 0; j < list.size(); j++) {
			sql += "?,";
		}

		// 去掉SQL语句最后一个,符号insert insert into table name(id,name,email)
		// values(?,?,?);
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";

		// 到此SQL语句拼接完成,打印SQL语句
		System.out.println(sql);

		// 获得预编译对象的引用
		PreparedStatement statement = connection.prepareStatement(sql);

		int i = 0;
		// 把指向迭代器最后一行的指针移到第一行.
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			// 此初判断返回值的类型,因为存入数据库时有的字段值格式需要改变,比如String,SQL语句是'"+abc+"'
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
		// 执行
		conn.execUpdate(statement);
		statement.close();
	}

	/**
	 * 修改
	 */
	public void update(T entity) throws Exception {
		String sql = "update " + entity.getClass().getSimpleName().toLowerCase() + " set ";

		// 获得该类所有get方法对象集合
		List<Method> list = this.matchPojoMethods(entity, "get");

		// 临时Method对象,负责迭代时装method对象.
		Method tempMethod = null;

		// 由于修改时不需要修改ID,所以按顺序加参数则应该把Id移到最后.
		Method idMethod = null;
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			// 如果方法名中带有ID字符串并且长度为2,则视为ID.
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				// 把ID字段的对象存放到一个变量中,然后在集合中删掉.
				idMethod = tempMethod;
				iter.remove();
				// 如果方法名去掉set/get字符串以后与pojo + "id"想符合(大小写不敏感),则视为ID
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		// 把迭代指针移到第一位
		iter = list.iterator();
		while (iter.hasNext()) {
			tempMethod = iter.next();
			sql += tempMethod.getName().substring(3).toLowerCase() + "= ?,";
		}

		// 去掉最后一个,符号
		sql = sql.substring(0, sql.lastIndexOf(","));

		// 添加条件
		sql += " where " + idMethod.getName().substring(3).toLowerCase() + " = ?";

		// SQL拼接完成,打印SQL语句
		System.out.println(sql);

		PreparedStatement statement = this.connection.prepareStatement(sql);

		int i = 0;
		iter = list.iterator();
		while (iter.hasNext()) {
			Method method = iter.next();
			// 此初判断返回值的类型,因为存入数据库时有的字段值格式需要改变,比如String,SQL语句是'"+abc+"'
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

		// 为Id字段添加值
		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		// 执行SQL语句
		statement.executeUpdate();

		// 关闭预编译对象
		statement.close();
	}

	/**
	 * 删除
	 */
	public void delete(T entity) throws Exception {
		String sql = "delete from " + entity.getClass().getSimpleName().toLowerCase() + " where ";

		// 存放字符串为"id"的字段对象
		Method idMethod = null;

		// 取得字符串为"id"的字段对象
		List<Method> list = this.matchPojoMethods(entity, "get");
		Iterator<Method> iter = list.iterator();
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			// 如果方法名中带有ID字符串并且长度为2,则视为ID.
			if (tempMethod.getName().lastIndexOf("Id") != -1 && tempMethod.getName().substring(3).length() == 2) {
				// 把ID字段的对象存放到一个变量中,然后在集合中删掉.
				idMethod = tempMethod;
				iter.remove();
				// 如果方法名去掉set/get字符串以后与pojo + "id"想符合(大小写不敏感),则视为ID
			} else if ((entity.getClass().getSimpleName() + "Id").equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
				iter.remove();
			}
		}

		sql += idMethod.getName().substring(3).toLowerCase() + " = ?";

		PreparedStatement statement = this.connection.prepareStatement(sql);

		// 为Id字段添加值
		int i = 0;
		if (idMethod.getReturnType().getSimpleName().indexOf("String") != -1) {
			statement.setString(++i, this.getString(idMethod, entity));
		} else {
			statement.setInt(++i, this.getInt(idMethod, entity));
		}

		// 执行
		conn.execUpdate(statement);
		statement.close();
	}

	/* 通过名称、日期查询 */
	public List<T> findByCnd(Object object, Object fromDate, Object toDate) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		// 通过子类的构造函数,获得参数化类型的具体类型.比如BaseDAO<T>也就是获得T的具体类型
		T entity = persistentClass.newInstance();

		Method nameMethod = null;
		Method timeMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		// 过滤取得Method对象
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Id") != -1) {
				nameMethod = tempMethod;
			} else if (tempMethod.getName().indexOf("Rdate") != -1) {
				timeMethod = tempMethod;
			}
		}
		// 第一个字母转为小写
		sql += (nameMethod.getName().substring(3, 4).toLowerCase() + nameMethod.getName().substring(4)).toLowerCase()
				+ " = ?";
		String fieldStr = (timeMethod.getName().substring(3, 4).toLowerCase() + timeMethod.getName().substring(4))
				.toLowerCase();
		sql += " and " + fieldStr + " between ?  and  ? ";
		// 封装语句完毕,打印sql语句
		System.out.println(sql);
		// 获得连接
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// 判断name的类型
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

		// 执行sql,取得查询结果集.
		ResultSet rs = conn.execQuery(statement);

		// 把指针指向迭代器第一行
		iter = list.iterator();

		// 封装
		while (rs.next()) {
			entity = persistentClass.newInstance();
			iter = list.iterator();
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// 由于list集合中,method对象取出的方法顺序与数据库字段顺序不一致(比如:list的第一个方法是setDate,而数据库按顺序取的是"123"值)
					// 所以数据库字段采用名字对应的方式取.
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

		// 关闭结果集
		rs.close();

		// 关闭预编译对象
		statement.close();
		return entities;
	}

	/* 通过名称查询 */
	public List<T> findByName(Object object) throws Exception {

		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";
		List<T> entities = new ArrayList<>();
		// 通过子类的构造函数,获得参数化类型的具体类型.比如BaseDAO<T>也就是获得T的具体类型
		T entity = persistentClass.newInstance();

		// 存放Pojo(或被操作表)主键的方法对象
		Method idMethod = null;

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();

		// 过滤取得Method对象
		while (iter.hasNext()) {
			Method tempMethod = iter.next();
			if (tempMethod.getName().indexOf("Zonename") != -1 && tempMethod.getName().substring(3).length() == 8) {
				idMethod = tempMethod;
			} else if ((entity.getClass().getSimpleName() + "Name")
					.equalsIgnoreCase(tempMethod.getName().substring(3))) {
				idMethod = tempMethod;
			}
		}
		// 第一个字母转为小写
		sql += (idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4)).toLowerCase()
				+ " = ?";

		// 封装语句完毕,打印sql语句
		System.out.println(sql);

		// 获得连接
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// 判断name的类型
		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		// 执行sql,取得查询结果集.
		ResultSet rs = conn.execQuery(statement);

		// 把指针指向迭代器第一行
		iter = list.iterator();

		// 封装
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// 由于list集合中,method对象取出的方法顺序与数据库字段顺序不一致(比如:list的第一个方法是setDate,而数据库按顺序取的是"123"值)
					// 所以数据库字段采用名字对应的方式取.
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

		// 关闭结果集
		rs.close();

		// 关闭预编译对象
		statement.close();

		return entities;
	}

	/**
	 * 通过ID查询
	 */
	public T findById(Object object) throws Exception {
		String sql = "select * from " + persistentClass.getSimpleName().toLowerCase() + " where ";

		// 通过子类的构造函数,获得参数化类型的具体类型.比如BaseDAO<T>也就是获得T的具体类型
		T entity = persistentClass.newInstance();

		// 存放Pojo(或被操作表)主键的方法对象
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
		// 第一个字母转为小写
		sql += idMethod.getName().substring(3, 4).toLowerCase() + idMethod.getName().substring(4) + " = ?";

		// 封装语句完毕,打印sql语句
		System.out.println(sql);

		// 获得连接
		PreparedStatement statement = this.connection.prepareStatement(sql);

		// 判断id的类型
		if (object instanceof Integer) {
			statement.setInt(1, (Integer) object);
		} else if (object instanceof String) {
			statement.setString(1, (String) object);
		}

		// 执行sql,取得查询结果集.
		ResultSet rs = conn.execQuery(statement);

		// 把指针指向迭代器第一行
		iter = list.iterator();

		// 封装
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("String") != -1) {
					// 由于list集合中,method对象取出的方法顺序与数据库字段顺序不一致(比如:list的第一个方法是setDate,而数据库按顺序取的是"123"值)
					// 所以数据库字段采用名字对应的方式取.
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

		// 关闭结果集
		rs.close();

		// 关闭预编译对象
		statement.close();

		return entity;
	}

	/**
	 * 
	 * @param timeunit
	 *            查询均值的时间单位
	 * @param field 小写
	 *            查询的字段
	 * @param fromDate
	 *            启示日期
	 * @param toDate
	 *            截止日期
	 * @return
	 * @throws Exception
	 */
	public List<T> findAvg(String timeunit, String field, String fromDate, String toDate) throws Exception {

		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(rdate as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,";
		} else {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,DATEPART(minute, cast(rdate as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		// 通过子类的构造函数,获得参数化类型的具体类型.比如BaseDAO<T>也就是获得T的具体类型
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			// 过滤取得Method对象
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

		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where rdate between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(rdate as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) , DAY(cast(rdate as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ";
		} else {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ,DATEPART(minute, cast(rdate as datetime)) ";
		}
		// 封装语句完毕,打印sql语句
		System.out.println(sql);

		// 获得连接
		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		// 执行sql,取得查询结果集.
		ResultSet rs = conn.execQuery(statement);

		list = this.matchPojoInheridMethods(entity, "set");
		// 把指针指向迭代器第一行
		iter = list.iterator();

		// 封装
		while (rs.next()) {
			while (iter.hasNext()) {
				Method method = iter.next();
				if (method.getParameterTypes()[0].getSimpleName().indexOf("double") != -1) {
					this.setDouble(method, entity, rs.getDouble(method.getName().substring(3).toLowerCase()));
				} else if (method.getParameterTypes()[0].getSimpleName().indexOf("float") != -1) {
					this.setFloat(method, entity, rs.getFloat(method.getName().substring(3).toLowerCase()));
				}else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
					this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
				}
			}
			entities.add(entity);
		}
		// 关闭结果集
		rs.close();
		// 关闭预编译对象
		statement.close();
		return entities;
	}
	/**
	 * @param id
	 * 			      区id
	 * @param timeunit
	 *            查询均值的时间单位
	 * @param field 小写
	 *            查询的字段
	 * @param fromDate
	 *            启示日期
	 * @param toDate
	 *            截止日期
	 * @return
	 * @throws Exception
	 */
	public List<T> findAvg(int id,String timeunit, String field, String fromDate, String toDate) throws Exception {

		System.out.println("TimeUnit:"+timeunit);
		String sql = "select ";
		if (timeunit.equals("YEAR")) {
			sql += " year(cast(rdate as datetime)) year,";
		} else if (timeunit.equals("MONTH")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month,";
		} else if (timeunit.equals("DAY")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day,";
		} else if (timeunit.equals("HOUR")) {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,";
		} else {
			sql += " year(cast(rdate as datetime)) year, MONTH(cast(rdate as datetime)) month, DAY(cast(rdate as datetime)) day, DATEPART(hour, cast(rdate as datetime)) hour,DATEPART(minute, cast(rdate as datetime)) minute,";
		}
		List<T> entities = new ArrayList<>();
		// 通过子类的构造函数,获得参数化类型的具体类型.比如BaseDAO<T>也就是获得T的具体类型
		T entity = persistentClass.newInstance();

		List<Method> list = this.matchPojoMethods(entity, "set");
		Iterator<Method> iter = list.iterator();
		if (field.equals("all")) {
			List<String> targetMethods = new ArrayList<>();
			// 过滤取得Method对象
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

		sql += " from " + persistentClass.getSimpleName().toLowerCase() + " where id = "+id+" and rdate between '" + fromDate
				+ "' and '" + toDate+"' ";
		
		if (timeunit.equals("YEAR")) {
			sql += " group by year(cast(rdate as datetime))";
		} else if (timeunit.equals("MONTH")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) ";
		} else if (timeunit.equals("DAY")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)) , DAY(cast(rdate as datetime)) ";
		} else if (timeunit.equals("HOUR")) {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ";
		} else {
			sql += " group by year(cast(rdate as datetime)), MONTH(cast(rdate as datetime)), DAY(cast(rdate as datetime)) , DATEPART(hour, cast(rdate as datetime)) ,DATEPART(minute, cast(rdate as datetime)) ";
		}
		// 封装语句完毕,打印sql语句
		System.out.println(sql);

		// 获得连接
		PreparedStatement statement = this.connection.prepareStatement(sql);
		
		// 执行sql,取得查询结果集.
		ResultSet rs = conn.execQuery(statement);
		list = this.matchPojoInheridMethods(entity, "set");
		// 把指针指向迭代器第一行
		iter = list.iterator();

		// 封装
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
					}
					else if (method.getParameterTypes()[0].getSimpleName().indexOf("int") != -1) {
						this.setInt(method, entity, rs.getInt(method.getName().substring(3).toLowerCase()));
					}
				}catch(Exception e){
					
				}
			}
			entities.add(entity);
			iter = list.iterator();
		}
		// 关闭结果集
		rs.close();
		// 关闭预编译对象
		statement.close();
		return entities;
	}

	/**
	 * 过滤当前Pojo类所有带传入字符串的Method对象,返回List集合.
	 */
	protected List<Method> matchPojoMethods(T entity, String methodName) {
		// 获得当前Pojo所有方法对象
		Method[] methods = entity.getClass().getDeclaredMethods();

		// List容器存放所有带get字符串的Method对象
		List<Method> list = new ArrayList<Method>();

		// 过滤当前Pojo类所有带get字符串的Method对象,存入List容器
		for (int index = 0; index < methods.length; index++) {
			if (methods[index].getName().indexOf(methodName) != -1) {
				list.add(methods[index]);
			}
		}
		return list;
	}
	
	/**
	 * 过滤当前Pojo类及父类所有带传入字符串的Method对象,返回List集合.
	 */
	protected List<Method> matchPojoInheridMethods(T entity, String methodName) {
		// 获得当前Pojo所有方法对象
		List<Method> methods =getDeclaredMethods(entity);

		// List容器存放所有带get字符串的Method对象
		List<Method> list = new ArrayList<Method>();

		// 过滤当前Pojo类所有带get字符串的Method对象,存入List容器
		for(Iterator<Method> it=methods.iterator();it.hasNext();){
			Method m=it.next();
			if (m.getName().indexOf(methodName) != -1) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 方法返回类型为int或Integer类型时,返回的SQL语句值.对应get
	 */
	public Integer getInt(Method method, T entity) throws Exception {
		return (Integer) method.invoke(entity, new Object[] {});
	}

	/**
	 * 方法返回类型为float类型时,返回的SQL语句值.对应get
	 */
	public float getFloat(Method method, T entity) throws Exception {
		return (float) method.invoke(entity, new Object[] {});
	}

	/**
	 * 方法返回类型为double类型时,返回的SQL语句值.对应get
	 */
	public double getDouble(Method method, T entity) throws Exception {
		return (double) method.invoke(entity, new Object[] {});
	}

	/**
	 * 方法返回类型为String时,返回的SQL语句拼装值.比如'abc',对应get
	 */
	public String getString(Method method, T entity) throws Exception {
		return (String) method.invoke(entity, new Object[] {});
	}

	/**
	 * 方法返回类型为Blob时,返回的SQL语句拼装值.对应get
	 */
	public InputStream getBlob(Method method, T entity) throws Exception {
		return (InputStream) method.invoke(entity, new Object[] {});
	}

	/**
	 * 方法返回类型为Date时,返回的SQL语句拼装值,对应get
	 */
	public Date getDate(Method method, T entity) throws Exception {
		return (Date) method.invoke(entity, new Object[] {});
	}

	/**
	 * 参数类型为Integer或int时,为entity字段设置参数,对应set
	 */
	public void setInt(Method method, T entity, Integer arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * 参数类型为float时,为entity字段设置参数,对应set
	 */
	public void setFloat(Method method, T entity, float arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * 参数类型为double时,为entity字段设置参数,对应set
	 */
	public void setDouble(Method method, T entity, double arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * 参数类型为String时,为entity字段设置参数,对应set
	 */
	public void setString(Method method, T entity, String arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * 参数类型为InputStream时,为entity字段设置参数,对应set
	 */
	public void setBlob(Method method, T entity, InputStream arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}

	/**
	 * 参数类型为Date时,为entity字段设置参数,对应set
	 */
	public void setDate(Method method, T entity, Date arg) throws Exception {
		method.invoke(entity, new Object[] { arg });
	}
	
	/** 
     * 循环向上转型, 获取对象的 DeclaredMethod 
     * @param object : 子类对象 
     * @param methodName : 父类中的方法名 
     * @param parameterTypes : 父类中的方法参数类型 
     * @return 父类中的方法对象 
     */  
      
    protected  List<Method> getDeclaredMethods(T entity){  
        List<Method> methods = new ArrayList<>() ;  
          
        for(Class<?> clazz = entity.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
            try {  
            	Method[] tempMethods=clazz.getDeclaredMethods() ;;
                methods.addAll(Arrays.asList(tempMethods));  
            } catch (Exception e) {  
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。  
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了  
              
            }  
        }  
          
        return methods;  
    }  
}
