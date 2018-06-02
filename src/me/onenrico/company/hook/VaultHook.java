package me.onenrico.company.hook;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.onenrico.company.main.Core;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {

	private static Core instance;
	public static Economy v_economy = null;
	public static Permission v_permission = null;
	public static Chat v_chat = null;

	public VaultHook() {
		instance = Core.getThis();
		if (setupEconomy()) {
			setupChat();
			setupPermissions();
		}
	}

	public static boolean setupEconomy() {
		if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		v_economy = rsp.getProvider();
		return v_economy != null;
	}

	public static boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = instance.getServer().getServicesManager().getRegistration(Chat.class);
		v_chat = rsp.getProvider();
		return v_chat != null;
	}

	public static boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = instance.getServer().getServicesManager()
				.getRegistration(Permission.class);
		v_permission = rsp.getProvider();
		return v_permission != null;
	}
}
