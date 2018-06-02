package me.onenrico.company.locale;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.onenrico.company.main.Core;
import me.onenrico.company.utils.PlaceholderUT;


public class Locales {
	public static Boolean premium = true;

	private static HashMap<String, List<String>> map = new HashMap<>();
	private static HashMap<String, String> map2 = new HashMap<>();
	private static HashMap<String, List<String>> map3 = new HashMap<>();
	public static PlaceholderUT pub = null;
	private static FileConfiguration config = null;

	public static void setup() {
		Set<String> nkeys = config.getConfigurationSection("messages").getKeys(false);
		if (nkeys != null) {
			for (String key : nkeys) {
				map.put(key, config.getStringList("messages." + key));
			}
		}
		Set<String> nkeys2 = config.getConfigurationSection("custom-placeholder").getKeys(false);
		if (nkeys2 != null) {
			for (String key : nkeys2) {
				map2.put(key, config.getString("custom-placeholder." + key));
			}
		}
		try {
			Set<String> nkeys3 = config.getConfigurationSection("random-placeholder").getKeys(false);
			if (nkeys3 != null) {
				for (String key : nkeys3) {
					map3.put(key, config.getStringList("random-placeholder." + key));
					map2.put(key, "R<>" + key);
				}
			}
		}catch(Exception ex) {
			
		}
		pub = new PlaceholderUT(getPlaceholder());
		pluginPrefix = Core.configplugin.getStr("pluginPrefix", "&cNot Configured");
	}

	public static List<String> getValue(String msg) {
		if (map.get(msg) == null) {
			InputStream is = Core.getThis().getResource("lang_EN.yml");
			File tfile = new File(Core.getThis().getDataFolder(), "lang.temp");
			try {
				FileUtils.copyInputStreamToFile(is, tfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileConfiguration defaultc = YamlConfiguration.loadConfiguration(tfile);
			List<String> mmsg = defaultc.getStringList("messages." + msg);
			config.set("messages." + msg, mmsg);
			map.put(msg, mmsg);
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			tfile.delete();
		}
		return pub.t(map.get(msg));
	}

	public static HashMap<String, String> getPlaceholder() {
		return map2;
	}

	private static Random rand = new Random();

	public static String getRandom(String value) {
		List<String> values = map3.getOrDefault(value, new ArrayList<>());
		String hasil = values.get(rand.nextInt(values.size()));
		return pub.t(hasil);
	}

	public static String pluginPrefix;

	private static File file;

	public static void reload(String locale) {
		try {
			file = new File(Core.getThis().getDataFolder(), "lang_" + locale + ".yml");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			if (!file.exists()) {
				Core.getThis().saveResource("lang_EN.yml", false);
			}
			config = YamlConfiguration.loadConfiguration(file);
			setup();
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
}
