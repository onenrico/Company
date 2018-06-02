package me.onenrico.company.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.onenrico.company.utils.MessageUT;

public class ConfigMethod {
	public String getStr(String path, String def, FileConfiguration config, FileConfiguration defaultc, File file) {
		def = MessageUT.t(def);
		if (config.getString(path, def).equalsIgnoreCase(def)) {
			if (defaultc.get(path) == null) {
				config.set(path, MessageUT.u(def));
			} else {
				config.set(path, defaultc.get(path));
				def = (String) defaultc.get(path);
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getString(path, def);
	}

	public List<String> getStrList(String path, FileConfiguration config) {
		return config.getStringList(path);
	}

	public List<String> getStrList(String path, List<String> def, FileConfiguration config, FileConfiguration defaultc,
			File file) {
		if (config.getStringList(path).isEmpty()) {
			if (defaultc.getStringList(path).isEmpty()) {
				config.set(path, def);
			} else {
				config.set(path, defaultc.getStringList(path));
				def = defaultc.getStringList(path);
			}
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getStringList(path);
	}

	public int getInt(String path, int def, FileConfiguration config, File file) {
		if (config.get(path) == null) {
			config.set(path, def);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getInt(path, def);
	}

	public String getStr(String path, FileConfiguration config) {
		return MessageUT.t(config.getString(path));
	}

	public int getInt(String path, FileConfiguration config) {
		return config.getInt(path);
	}

	public double getDouble(String path, double def, FileConfiguration config, File file) {
		if (config.get(path) == null) {
			config.set(path, def);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getDouble(path, def);
	}

	public Boolean getBool(String path, FileConfiguration config) {
		return config.getBoolean(path);
	}

	public Boolean getBool(String path, Boolean def, FileConfiguration config, File file) {
		if (config.get(path) == null) {
			config.set(path, def);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
		return config.getBoolean(path, def);
	}

}
