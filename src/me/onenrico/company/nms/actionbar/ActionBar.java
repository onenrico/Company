package me.onenrico.company.nms.actionbar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.onenrico.company.main.Core;
import me.onenrico.company.utils.ReflectionUT;

public class ActionBar {

	public static boolean works = true;

	private static boolean useOldMethods = false;
	static Core plugin;
	private static Class<?> c1;
	private static Class<?> c2;
	private static Class<?> c3;
	private static Class<?> c4;
	private static Class<?> c5;
	private static Class<?> c6;
	private static Method m1;

	private static String ver = ReflectionUT.VERSION;
	public ActionBar() {
		setup();
	}

	public static void setup() {
		plugin = Core.getThis();
		if (ver.equalsIgnoreCase("v1_8_R1") || ver.equalsIgnoreCase("v1_7_")) {
			useOldMethods = true;
		}
		try {
			c1 = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer");
			c5 = Class.forName("net.minecraft.server." + ver + ".Packet");
			if (useOldMethods) {
				c2 = Class.forName("net.minecraft.server." + ver + ".ChatSerializer");
				c3 = Class.forName("net.minecraft.server." + ver + ".IChatBaseComponent");
			} else {
				c2 = Class.forName("net.minecraft.server." + ver + ".ChatComponentText");
				c3 = Class.forName("net.minecraft.server." + ver + ".IChatBaseComponent");
			}
			if (ver.startsWith("v1_12_")) {
				c6 = Class.forName("net.minecraft.server." + ver + ".ChatMessageType");
			}
			m1 = c1.getDeclaredMethod("getHandle");
			c4 = Class.forName("net.minecraft.server." + ver + ".PacketPlayOutChat");
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static void sendActionBar(Player player, String message) {
		if (!player.isOnline()) {
			return; // Player may have logged out
		}
		try {
			Object p = c1.cast(player);
			Object ppoc;
			if (useOldMethods) {
				Method m3 = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
				ppoc = c4.getConstructor(new Class<?>[] { c3, byte.class }).newInstance(cbc, (byte) 2);
			} else {
				Object o = c2.getConstructor(new Class<?>[] { String.class }).newInstance(message);
				if (ver.startsWith("v1_12_")) {
					Object[] chatMessageTypes = c6.getEnumConstants();
					Object chatMessageType = chatMessageTypes[2];
					ppoc = c4.getConstructor(new Class<?>[] { c3, c6 }).newInstance(o, chatMessageType);
				} else {
					ppoc = c4.getConstructor(new Class<?>[] { c3, byte.class }).newInstance(o, (byte) 2);
				}
			}
			Object h = m1.invoke(p);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		} catch (Exception ex) {
			ex.printStackTrace();
			works = false;
		}
	}

	public static void sendActionBar(final Player player, final String message, int duration) {
		sendActionBar(player, message);

		if (duration >= 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, "");
				}
			}.runTaskLater(plugin, duration + 1);
		}

		while (duration > 60) {
			duration -= 60;
			int sched = duration % 60;
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, message);
				}
			}.runTaskLater(plugin, sched);
		}
	}

	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(String message, int duration) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message, duration);
		}
	}

}
