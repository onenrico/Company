package me.onenrico.company.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


public class SqlUT {
	public static String quote(String d) {
		return "`" + d + "`";
	}
	public static Connection con = null;
	public static Boolean tableExist(String table) {
		DatabaseMetaData dbm;
		ResultSet tables = null;
		try {
			dbm = con.getMetaData();
			tables = dbm.getTables(null, null, table, null);
			return tables.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(null, tables);
		}
		return false;
	}
	public static HashMap<PreparedStatement, ResultSet> executeQuery(String sql) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			HashMap<PreparedStatement, ResultSet> result = new HashMap<>();
			result.put(ps, ps.executeQuery());
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			close(ps,null);
		}
		return null;
	}
	public static int executeUpdate(String sql) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(ps,null);
		}
		return 0;
	}
	public static boolean execute(String sql) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			return ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(ps,null);
		}
		return false;
	}
	public static int[] executeBatch(List<String> sqls) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sqls.get(0));
			for(String sql : sqls) {
				ps.addBatch(sql);
			}
			return ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(ps,null);
		}
		return null;
	}
	public static String sqlcondition(HashMap<String,Object> condition,String operator) {
		String sql = " WHERE (";
		int index = 0;
		String[] op = {"AND"};
		if(operator != null) {
			op = operator.split(",");
		}
		for (String key : condition.keySet()) {
			String qkey = quote(key);
			if(index == 0) {
				sql += "%s=";
			}else {
				if(op.length > 1) {
					sql += " "+op[index-1].toUpperCase()+" (%s=";
				}else {
					sql += " "+op[0].toUpperCase()+" (%s=";
				}
			}
			if(condition.get(key) instanceof String)
				sql += "'%s')";
			else
				sql += "%s)";
			sql = String.format(sql,qkey,""+condition.get(key));
			index++;
		}
		sql += ";";
		return sql;
	}
	public static String createTable(String tablename,HashMap<String,String> tablevalue,String pkey) {
		String result = "CREATE TABLE IF NOT EXISTS " + tablename + " (";
		int index = 0;
		for (String key : tablevalue.keySet()) {
			index += 1;
			String nn = " NOT NULL";
			String ty = tablevalue.get(key);
			if (ty.contains("null")){
				ty = ty.replace(" null","");
				nn = "";
			}
			if(index >= (tablevalue.keySet().size())) {
				result += "`" + key + "` " + ty + nn;
			}
			else {
				result += "`" + key + "` " + ty + nn+",";
			}
		}
		if (pkey != "null"){
			result += ",PRIMARY KEY (`" + pkey + "`));";
		}
		else {
			result += ");";
		}
		return result;
	}

	public static String droptable(String tablename) {
		return "DROP TABLE IF EXISTS "+tablename;
	}
	public static void main(String[] args) {
		System.out.println("Tes");
		HashMap<String,Object> map = new HashMap<>();
		map.put("enrico", "dewa");
		map.put("kancil", "aasdas");
		map.put("golok", "mantap");
		System.out.println(insert("dewa",map));
		System.out.println(sqlcondition(map,"OR,AND"));
	}
	public static String insert(String table, HashMap<String,Object> columns) {
		String column = "(";
		String values = "(";
		int	index = 0;
		String query = "";
		for (String c : columns.keySet()) {
			column += quote(c);
			if(columns.get(c) instanceof String) {
				values += "'%s'";
			}else {
				values += "%s";
			}
			values = String.format(values, ""+columns.get(c));
			if (index + 1 != (columns.keySet().size())) {
				column += ", ";
				values += ", ";
				index += 1;
			}else {
				column += ")";
				values += ")";
			}
		}
		query = "REPLACE INTO " + table + " " + column + " " + "VALUES " + values + ";";
		return query;
	}
	public static String select(String table,String column) {
		return select(table,column,null,null);
	}
	public static String select(String table,String column,HashMap<String,Object> condition) {
		return select(table,column,condition,null);
	}
	public static String select(String table,String column,HashMap<String,Object> condition,String conditionop) {
		String sql = String.format("SELECT %s from %s",column,table);
		if(condition != null && !condition.isEmpty()) {
			sql += sqlcondition(condition,conditionop);
		}
		return sql;
	}
	public static String delete(String table,HashMap<String,Object> condition) {
		return delete(table,condition,null);
	}
	public static String delete(String table,HashMap<String,Object> condition,String conditionop) {
		String sql = String.format("DELETE from %s",table);
		if(condition != null && !condition.isEmpty()) {
			sql += sqlcondition(condition,conditionop);
		}
		return sql;
	}
	public static String update(String table,String column, Object value,HashMap<String,Object> condition) {
		return update(table,column,value,condition,null);
	}
	public static String update(String table,String column, Object value,HashMap<String,Object> condition,String conditionop) {
		String sql = String.format("UPDATE %s SET",table);
		String qkey = quote(column);
		sql += " %s=";
		if(value instanceof String) {
			sql += "'%s'";
		}
		else {
			sql += "%s";
		}
		sql = String.format(sql,qkey,""+value);
		if(condition != null && !condition.isEmpty()) {
			sql += sqlcondition(condition,conditionop);
		}
		return sql;
	}
	public static void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				if(!ps.isClosed()) {
					ps.close();
				}
			}
			if (rs != null) {
				if(!rs.isClosed()) {
					rs.close();
				}
			}
		} catch (SQLException ex) {
			MessageUT.debug("F: " + ex);
		}
	}
}