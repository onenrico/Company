package me.onenrico.company.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.company.main.Core;
import me.onenrico.company.utils.MessageUT;
import me.onenrico.company.utils.SqlUT;


public abstract class Database {

	public Connection connection;
	
	public String table = "Company_Data";
	public List<String> datacolumns = new ArrayList<>();


	public abstract Connection getSQLConnection();

	public abstract void load();
	

	public List<String> cache = new ArrayList<>();

	public void reloadData(BukkitRunnable callback) {
		if (!cache.isEmpty()) {
			cache.clear();
		}
		new BukkitRunnable() {
			PreparedStatement ps = null;
			ResultSet rs = null;
			@Override
			public void run() {
				try {
					String sql = SqlUT.select(table, "*");
					ps = connection.prepareStatement(sql);
					rs = ps.executeQuery();
					while (rs.next()) {
						cache.add(rs.getString("Name"));
					}
					close(ps, rs);
					if (callback != null) {
						callback.run();
					}
				} catch (SQLException ex) {
					MessageUT.debug("A: " + ex);
				} finally {
					close(ps, rs);
				}
			}

		}.runTask(Core.getThis());
	}

	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ex) {
			MessageUT.debug("F: " + ex);
		}
	}
}
