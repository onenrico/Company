package me.onenrico.company.object;

import java.util.Comparator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.onenrico.company.database.Datamanager;
import me.onenrico.company.locale.Locales;
import me.onenrico.company.main.Core;
import me.onenrico.company.utils.EconomyUT;
import me.onenrico.company.utils.MathUT;
import me.onenrico.company.utils.MessageUT;
import me.onenrico.company.utils.PlaceholderUT;

public class Worker {
	private UUID player;
	private ECompany company;
	private Role role;
	private double wage = 0;
	private String title = "";
	private int onlinesecond = 0;
	public static int secondtopay;

	public static Comparator<Worker> weightComparator = new Comparator<Worker>() {
		@Override
		public int compare(Worker w1, Worker w2) {
			return w2.getRole().getWeight() - w1.getRole().getWeight();
		}
	};
	public Worker(ECompany company,UUID player,Role role) {
		this.company = company;
		this.player = player;
		this.role = role;
	}
	public Worker(ECompany company,UUID player,Role role,String title) {
		this.company = company;
		this.player = player;
		this.role = role;
		this.title = title;
	}
	public Worker(ECompany company,UUID player,Role role,String title,int onlinesecond) {
		this.company = company;
		this.player = player;
		this.role = role;
		this.title = title;
		this.onlinesecond = onlinesecond;
	}
	public Worker(ECompany company,UUID player,Role role,String title,int onlinesecond,double wage) {
		this.company = company;
		this.player = player;
		this.role = role;
		this.title = title;
		this.onlinesecond = onlinesecond;
		this.wage = wage;
	}
	public static Worker fromString(String workerString) {
		String[] data = workerString.split("<@>");
		UUID player = UUID.fromString(data[0]);
		Worker w = Datamanager.getWorker(player);
		if(w != null) {
			return w;
		}
		ECompany company = Datamanager.getECompany(data[1]);
		Role role = Role.valueOf(data[2]);
		String title = data[3];
		int onlinesecond = MathUT.strInt(data[4]);
		double wage = Double.valueOf(data[5]);
		return new Worker(company,player,role,title,onlinesecond,wage);
	}
	public String toString() {
		return player+"<@>"+company.getName()+"<@>"+role.toString()+
				"<@>"+title+"<@>"+onlinesecond+"<@>"+wage;
	}
	public String format() {
		String wag = " &8[&a$"+wage+"&8]";
		if(wage <= 0) {
			wag = " &8[&aFree Worker&8]";
		}
		return role.getPrefix()+title+" &r"+getOffPlayer().getName()+wag;
	}
	public double getWage() {
		return this.wage;
	}
	public void setWage(double wage) {
		this.wage = wage;
	}
	public boolean fire() {
		if(Core.cache.containsKey(getOffPlayer())) {
			Core.cache.remove(getOffPlayer());
		}
		return company.removeWorker(this);
	}
	public boolean promote() {
		Role r = role;
		setRole(role.promote());
		if(r.equals(role)) {
			return false;
		}
		return true;
	}
	public boolean demote() {
		Role r = role;
		setRole(role.demote());
		if(r.equals(role)) {
			return false;
		}
		return true;
	}
	public UUID getPlayer() {
		return player;
	}
	public OfflinePlayer getOffPlayer() {
		return Bukkit.getOfflinePlayer(player);
	}
	public ECompany getCompany() {
		return company;
	}
	public Role getRole() {
		return role;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOnlineSecond() {
		return onlinesecond;
	}
	public void addOnlineSecond(int second) {
		onlinesecond += second;
		if(onlinesecond >= secondtopay) {
			pay();
		}
	}
	public void pay() {
		if(getOffPlayer().isOnline()) {
			setOnlineSecond(0);
			Player p = (Player) getOffPlayer();
			PlaceholderUT pu = new PlaceholderUT();
			pu.add("amount", ""+wage);
			pu.add("company", ""+company);
			if(company.withdraw(wage)) {
				MessageUT.plmessage(p, pu.t(Locales.getValue("wage_payment")));
				EconomyUT.addBal(p, wage);
			}else {
				MessageUT.plmessage(p, pu.t(Locales.getValue("wage_lack")));
			}
		}
	}
	public void setOnlineSecond(int second) {
		onlinesecond = second;
	}
	public void setRole(Role role) {
		this.role = role;
	}

	public enum Role {
		CEO(3),
		MANAGER(2),
		EMPLOYEE(1)
		;
		public static String CEO_PREFIX;
		public static String MANAGER_PREFIX;
		public static String EMPLOYEE_PREFIX;
		private int weight;

		Role(int weight) {
			this.weight = weight;
		}
		public Role promote() {
			switch(this) {
			case EMPLOYEE:
				return MANAGER;
			default:
				return CEO;
			}
		}
		public Role demote() {
			switch(this) {
			case CEO:
				return MANAGER;
			default:
				return EMPLOYEE;
			}
		}
		public int getWeight() {
			return weight;
		}
		public String getPrefix() {
			switch(this) {
			case CEO:
				return CEO_PREFIX;
			case MANAGER:
				return MANAGER_PREFIX;
			default:
				return EMPLOYEE_PREFIX;
			}
		}
	}
}
