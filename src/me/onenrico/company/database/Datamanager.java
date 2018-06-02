package me.onenrico.company.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.onenrico.company.database.sql.Database;
import me.onenrico.company.database.sql.SQLite;
import me.onenrico.company.main.Core;
import me.onenrico.company.object.ECompany;
import me.onenrico.company.object.Worker;
import me.onenrico.company.utils.MessageUT;
import me.onenrico.company.utils.SqlUT;

public class Datamanager {
	private static Set<ECompany> LoadedData = new HashSet<>();
	private static List<BukkitTask> databaseload = new ArrayList<>();

	static Core instance;
	private static Database db;

	public Datamanager() {
		instance = Core.getThis();
	}

	public void reloadData() {
		setup();
	}

	public static Database getDB() {
		return db;
	}


	public static void setup() {
		for (BukkitTask task : databaseload) {
			task.cancel();
		}
		databaseload = new ArrayList<>();
		if (db != null) {
			if (db.connection != null) {
				try {
					if (!db.connection.isClosed()) {
						db.connection.close();
					}
					db = null;
				} catch (SQLException e) {
				}
			}
		}
		if (LoadedData != null) {
			LoadedData.clear();
		}
		db = new SQLite();
		db.load();
	}
	public static String getColumn(ECompany e,String data) {
		HashMap<String,Object> condition = new HashMap<>();
		condition.put("Name", e.getName());
		String sql = SqlUT.select(db.table, data, condition);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			ps = db.connection.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = rs.getString(data);
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		}finally {
			db.close(ps,rs);
		}
		return result;
	}

	public static ECompany getECompany(String name) {
		for (ECompany data : LoadedData) {
			if (data.getName().equalsIgnoreCase(name)) {
				return data;
			}
		}
		for (ECompany data : LoadedData) {
			if (data.getName().startsWith(name)) {
				return data;
			}
		}
		for (ECompany data : LoadedData) {
			if (data.getName().contains(name)) {
				return data;
			}
		}
		return null;
	}
	public static Worker getWorker(UUID player) {
		for(ECompany data : LoadedData) {
			for(Worker w : data.getWorkers()) {
				if(w.getPlayer().equals(player)) {
					return w;
				}
			}
		}
		return null;
	}
	public static ECompany getOwnedCompany(UUID player) {
		for(ECompany data : LoadedData) {
			if(data.getOwner().equals(player)) {
				return data;
			}
		}
		return null;
	}
	public static ECompany getCompany(UUID player) {
		for(ECompany data : LoadedData) {
			if(data.getOwner().equals(player) || (getWorker(player) != null && 
					getWorker(player).getCompany().equals(data))) {
				return data;
			}
		}
		return null;
	}
	public static List<ECompany> getLoaded() {
		return new ArrayList<>(LoadedData);
	}

	public static void addData(ECompany newdata) {
		LoadedData.add(newdata);
		save(newdata);
	}
	public static void save(ECompany newdata) {
		HashMap<String,Object> columns = new HashMap<>();
		for(String c : db.datacolumns) {
			String value = "";
			switch(c.toLowerCase()) {
			case "name":
				value = newdata.getName();
				break;
			case "owner":
				value = newdata.getOwner().toString();
				break;
			case "bank":
				value = ""+newdata.getBalance();
				break;
			case "hq":
				value = newdata.getHQSimple();
				break;
			case "workers":
				value = newdata.getWorkersSimple();
				break;
			case "description":
				value = newdata.getDescription();
				break;
			}
			columns.put(c, value);
		}
		SqlUT.executeUpdate(SqlUT.insert(db.table, columns));
		
	}

	public static void deleteData(ECompany data, BukkitRunnable callback) {
		if(LoadedData.contains(data)) {
			LoadedData.remove(data);
		}
		HashMap<String,Object> condition = new HashMap<>();
		condition.put("Name", data.getName());
		SqlUT.executeUpdate(SqlUT.delete(db.table,condition));
	}
	
	private static long last;
	public static void count() {
		long hasil = System.currentTimeMillis() - last;
		double r = hasil / 1000.0;
		for(ECompany ee : new ArrayList<>(LoadedData)) {
			ee.refresh();
		}
		int j = LoadedData.size();
		MessageUT.cmessage(name+ j + " &eData Loaded");
		MessageUT.cmessage(name+"Load Database Completed in &a" + r + "s");
		Datamanager.getDB().cache.clear();
	}
	private static String name = "|Company> ";
	public static void loadObject() {
		last = System.currentTimeMillis();
		MessageUT.cmessage(name+"Start Load Database");
		new BukkitRunnable() {
			@Override
			public void run() {
				List<String> databaseKey = new ArrayList<>(db.cache);
				int count = databaseKey.size();
				int times = (int) Math.ceil((double) count / (double) 100);
				if (count == 0) {
					MessageUT.cmessage(name+"No Data Found");
					return;
				}
				for (int x = 0; x < times; x++) {
					int num = x;
					databaseload.add(new BukkitRunnable() {
						int id = num + 1;
						int max = 100;

						@Override
						public void run() {
							if (id == times) {
								max = count % max;
							}
							for (int index = 0; index < max; index++) {
								String key = databaseKey.get(index + (max * num));
								ECompany ee = new ECompany(key);
								LoadedData.add(ee);
							}
							if (id + 1 == times && times > 1) {
								count();
							} else if (times < 2) {
								count();
							}
							cancel();
							return;
						}

					}.runTaskLater(Core.getThis(), 0));
				}
			}

		}.runTaskLater(Core.getThis(), 0);
	}
}