package me.onenrico.company.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.onenrico.company.locale.Locales;


public class PlaceholderUT {
	private HashMap<String, String> acuan;

	@SuppressWarnings("unchecked")
	public PlaceholderUT(HashMap<String, String> acuan) {
		this.acuan = (HashMap<String, String>) acuan.clone();
	}

	public PlaceholderUT() {
		acuan = new HashMap<>();
	}

	public HashMap<String, String> getAcuan() {
		return acuan;
	}

	public void setAcuan(HashMap<String, String> acuan) {
		this.acuan = acuan;
	}

	public void remove(String data) {
		if (acuan.containsKey(data)) {
			acuan.remove(data);
		}
	}

	public void add(String placeholder, String data) {
		acuan.put(placeholder, data);
	}

	public List<String> t(Player player,List<String> data) {
		List<String> result = new ArrayList<>();
		for (String b : data) {
			String temp = t(player,b);
			result.add(MessageUT.t(temp));
		}
		return result;
	}
	public List<String> t(List<String> data) {
		List<String> result = new ArrayList<>();
		for (String b : data) {
			String temp = t(null,b);
			for(String t : temp.split("\n")) {
				result.add("<np>"+MessageUT.t(t));
			}
		}
		return result;
	}

	public String t(String data) {
		return t(null, data);
	}

	public Boolean papi = null;

	public String t(Player player, String data) {
		for (String a : acuan.keySet()) {
			if (a.startsWith("<") && a.endsWith(">")) {
				if (data.contains(a)) {
					String replacer = acuan.get(a);
					if (replacer.startsWith("R<>")) {
						replacer = Locales.getRandom(replacer.split("R<>")[1]);
					}
					data = data.replace(a, replacer);
				}
			} else if (data.contains("{" + a + "}")) {
				String replacer = acuan.get(a);
				if (replacer.startsWith("R<>")) {
					replacer = Locales.getRandom(replacer.split("R<>")[1]);
				}
				data = data.replace("{" + a + "}", replacer);
			}
		}
		if (papi == null) {
			papi = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
		}
		if (papi) {
			data = PlaceholderAPI.setBracketPlaceholders(player, data);
			data = PlaceholderAPI.setPlaceholders(player, data);
		}
		return MessageUT.t(data);
	}

}
