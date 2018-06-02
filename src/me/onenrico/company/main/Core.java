package me.onenrico.company.main;


import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.company.commands.Company;
import me.onenrico.company.config.EConfig;
import me.onenrico.company.database.Datamanager;
import me.onenrico.company.hook.VaultHook;
import me.onenrico.company.locale.Locales;
import me.onenrico.company.nms.actionbar.ActionBar;
import me.onenrico.company.object.Worker;
import me.onenrico.company.object.Worker.Role;
public class Core extends JavaPlugin{
	private static Core instance;
	public static EConfig configplugin;
	public static EConfig guiconfig;
	public static EConfig databaseconfig;
	public static HashMap<OfflinePlayer,Worker> cache = new HashMap<>();
	public static Core getThis() {
		return instance;
	}
	@Override
	public void onEnable() {
		instance = this;
		configplugin = new EConfig(this,"config.yml");
		databaseconfig = new EConfig(this,"database.yml");
		new VaultHook();
		reloadSetting();
		Datamanager.setup();
		Locales.reload(configplugin.getStr("locales","none"));
		ActionBar.setup();
		getCommand("company").setExecutor(new Company());
		new VaultHook();
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					Worker w = cache.getOrDefault(p, null);
					if(w == null) {
						w = Datamanager.getWorker(p.getUniqueId());
						if(w != null) {
							if(w.getWage() <= 0) {
								continue;
							}
							cache.put(p, w);
							w.addOnlineSecond(1);
						}
					}else {
						if(w.getWage() <= 0) {
							continue;
						}
						w.addOnlineSecond(1);
					}
				}
			}
		}.runTaskTimer(this, 20, 20);
	}
	public void reloadSetting() {
		Role.EMPLOYEE_PREFIX = configplugin.getStr("role.employee", "&f");
		Role.MANAGER_PREFIX = configplugin.getStr("role.manager", "&a");
		Role.CEO_PREFIX = configplugin.getStr("role.ceo", "&c");
		Worker.secondtopay = configplugin.getInt("second_to_pay", 120);
		Company.cost = configplugin.getInt("cost", 10000);
	}
}
