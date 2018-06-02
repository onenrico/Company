package me.onenrico.company.object;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Seriloc {
	private Location loc;

	public Seriloc(Location realloc) {
		loc = realloc;
	}

	public Seriloc(String serializedloc) {
		Location realloc = Deserialize(serializedloc);
		loc = realloc;
	}

	public static Location centered(Location loc) {
		return loc.clone().add(0.5d, 0, 0.5d);
	}

	public static String Serialize(Location loc) {
		Map<String, Object> rawLoc = loc.serialize();
		String serializedLoc = "";
		int index = 0;
		for (String key : rawLoc.keySet()) {
			index++;
			if (index < rawLoc.size()) {
				serializedLoc += rawLoc.get(key) + "<>";
			} else {
				serializedLoc += rawLoc.get(key) + "";
			}
		}
		return serializedLoc;
	}

	public static Location Deserialize(String rawLoc) {
		rawLoc = rawLoc.replace("<r>", "<>");
		String[] deserializedLoc = rawLoc.split("<>");
		World world = null;
		for (World w : Bukkit.getWorlds()) {
			if (w.getName().equalsIgnoreCase(deserializedLoc[0])) {
				world = w;
			}
		}
		if (world == null) {
			return null;
		}
		double x = Double.valueOf(deserializedLoc[1]);
		double y = Double.valueOf(deserializedLoc[2]);
		double z = Double.valueOf(deserializedLoc[3]);
		float yaw = Float.valueOf(deserializedLoc[5]);
		float pitch = Float.valueOf(deserializedLoc[4]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	public String Serialize() {
		return Serialize(loc);
	}

	public Location Deserialize() {
		return Deserialize(Serialize(loc));
	}

	public void setWorld(World world) {
		loc.setWorld(world);
	}

	public World getWorld() {
		return loc.getWorld();
	}

	public Chunk getChunk() {
		return loc.getChunk();
	}

	public Block getBlock() {
		return loc.getBlock();
	}

	public void setX(double x) {
		loc.setX(x);
	}

	public double getX() {
		return loc.getX();
	}

	public int getBlockX() {
		return loc.getBlockX();
	}

	public void setY(double y) {
		loc.setY(y);
	}

	public double getY() {
		return loc.getY();
	}

	public int getBlockY() {
		return loc.getBlockY();
	}

	public void setZ(double z) {
		loc.setZ(z);
	}

	public double getZ() {
		return loc.getZ();
	}

	public int getBlockZ() {
		return loc.getBlockZ();
	}

	public void setYaw(float yaw) {
		loc.setYaw(yaw);
	}

	public float getYaw() {
		return loc.getYaw();
	}

	public void setPitch(float pitch) {
		loc.setPitch(pitch);
	}

	public float getPitch() {
		return loc.getPitch();
	}

	public Vector getDirection() {
		return loc.getDirection();
	}

	public Location setDirection(Vector vector) {
		return loc.setDirection(vector);
	}

	public Location add(Location vec) {
		return loc.add(vec);
	}

	public Location add(Vector vec) {
		return loc.add(vec);
	}

	public Location add(double x, double y, double z) {
		return loc.add(x, y, z);
	}

	public Location subtract(Location vec) {
		return loc.subtract(vec);
	}

	public Location subtract(Vector vec) {
		return loc.subtract(vec);
	}

	public Location subtract(double x, double y, double z) {
		return loc.subtract(x, y, z);
	}

	public double length() {
		return loc.length();
	}

	public double lengthSquared() {
		return loc.lengthSquared();
	}

	public double distance(Location o) {
		return loc.distance(o);
	}

	public double distanceSquared(Location o) {
		return loc.distanceSquared(o);
	}

	public Location multiply(double m) {
		return loc.multiply(m);
	}

	public Location zero() {
		return loc.zero();
	}

	@Override
	public boolean equals(Object obj) {
		return loc.equals(obj);
	}

	@Override
	public int hashCode() {
		return loc.hashCode();
	}

	@Override
	public String toString() {
		return loc.toString();
	}

	public Vector toVector() {
		return loc.toVector();
	}

	public Location toLoc() {
		return loc;
	}
}
