package me.onenrico.company.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.onenrico.company.main.Core;


public class EConfig extends ConfigMethod implements ConfigSet {
	public FileConfiguration config = null;
	public FileConfiguration defaultconfig = null;
	public File defaultfile = null;
	public File file = null;
	public InputStream is = null;

	public EConfig(Core plugin, String filename) {
		file = new File(plugin.getDataFolder(), filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		if (!file.exists()) {
			try {
				plugin.saveResource(filename, false);
			} catch (Exception ex) {
			}
		}
		is = plugin.getResource(filename);
		defaultfile = new File(Core.getThis().getDataFolder(), filename + ".temp");
		try {
			if (defaultfile.exists()) {
				defaultfile.delete();
				defaultfile.createNewFile();
			}
			FileUtils.copyInputStreamToFile(is, defaultfile);
			defaultconfig = YamlConfiguration.loadConfiguration(defaultfile);
			defaultfile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reload();
	}

	public void reload() {
		try {
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public List<String> getStrList(String path, List<String> def) {
		return getStrList(path, def, config, defaultconfig, file);
	}

	@Override
	public List<String> getStrList(String path) {
		return getStrList(path, config);
	}

	@Override
	public String getStr(String path, String def) {
		return getStr(path, def, config, defaultconfig, file);
	}

	@Override
	public String getStr(String path) {
		return getStr(path, config);
	}

	@Override
	public double getDouble(String path, double def) {
		if (getDouble(path, def, config,file) == def) {
			return defaultconfig.getDouble(path, def);
		}
		return getDouble(path, def, config,file);
	}

	@Override
	public int getInt(String path, int def) {
		if (getInt(path, def, config, file) == def) {
			return defaultconfig.getInt(path, def);
		}
		return getInt(path, def, config, file);
	}

	@Override
	public Boolean getBool(String path, boolean def) {
		if (getBool(path, def, config, file) == def) {
			return defaultconfig.getBoolean(path, def);
		}
		return getBool(path, def, config, file);
	}

	@Override
	public Boolean getBool(String path) {
		return getBool(path, config);
	}

}
