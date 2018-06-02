package me.onenrico.company.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigSet {
	public FileConfiguration config = null;

	public FileConfiguration getConfig();

	public List<String> getStrList(String path, List<String> def);

	public List<String> getStrList(String path);

	public String getStr(String path, String def);

	public String getStr(String path);

	public double getDouble(String path, double v);

	public int getInt(String path, int def);

	public Boolean getBool(String path, boolean def);

	public Boolean getBool(String path);
}
