package me.onenrico.company.database.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.company.database.Datamanager;
import me.onenrico.company.main.Core;
import me.onenrico.company.utils.MessageUT;
import me.onenrico.company.utils.SqlUT;


public class SQLite extends Database {
	public static final String dbname = "database";
	public SQLite() {
		super();

	}
	@Override
	public void load() {
		connection = getSQLConnection();
		SqlUT.con = connection;
		HashMap<String, String> map = new HashMap<>();
		map.put("Name", "varchar(255)");
		map.put("Owner", "varchar(255)");
		map.put("Bank", "float(2)");
		map.put("HQ", "varchar(255)");
		map.put("Workers", "text");
		map.put("Description", "text");
		for(String c : map.keySet()) {
			datacolumns.add(c);
		}
		SqlUT.executeUpdate(SqlUT.createTable(table, map, "Name"));
		reloadData(new BukkitRunnable() {
			@Override
			public void run() {
				Datamanager.loadObject();
			}
		});
	}

	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(Core.getThis().getDataFolder() + "/data/");
		File dataFile = new File(Core.getThis().getDataFolder() + "/data/", dbname + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.mkdir();
				dataFile.createNewFile();
			} catch (IOException e) {
				MessageUT.debug("File write error: " + dbname + ".db");
			}
		}
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
			return connection;
		} catch (SQLException ex) {
			MessageUT.debug("G: SQLite exception on initialize");
		} catch (ClassNotFoundException ex) {
			MessageUT.debug("H: SQLite exception on initialize");
		}
		return null;
	}
}
