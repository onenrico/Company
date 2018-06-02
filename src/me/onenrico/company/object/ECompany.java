package me.onenrico.company.object;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import me.onenrico.company.database.Datamanager;
import me.onenrico.company.object.Worker.Role;

public class ECompany {
	private List<Worker> workers = new ArrayList<>();
	private UUID owner;
	private String name;
	private double bank = 0;
	private Location hq = null;
	private String description = "&7No description set.";
	public static Comparator<ECompany> balanceComparator = new Comparator<ECompany>() {
		@Override
		public int compare(ECompany w1, ECompany w2) {
			return Double.compare(w2.getBalance(), w1.getBalance());
		}
	};
	public ECompany(String name,UUID owner) {
		this.name = name;
		this.owner = owner;
	}
	public ECompany(String name) {
		this.name = name;
	}
	public void refresh() {
		this.owner = UUID.fromString(getColumn("Owner"));
		this.bank = Double.parseDouble(getColumn("Bank"));
		String temp = getColumn("HQ");
		if(temp == null) {
			this.hq = null;
		}else {
			this.hq = Seriloc.Deserialize(temp);
		}
		this.workers = fromSimpleWorker(getColumn("Workers"));
		this.description = getColumn("Description");
	}
	private String getColumn(String data) {
		return Datamanager.getColumn(this, data);
	}
	public String getDescription() {
		if(description == null)
		{
			description = "&7No description set.";
		}
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
		save();
	}
	public List<Worker> fromSimpleWorker(String simple){
		List<Worker> w = new ArrayList<>();
		if(simple.isEmpty()) {
			return w;
		}
		for(String s : simple.split("<!>")) {
			Worker wo = Worker.fromString(s);
			w.add(wo);
		}
		return w;
	}
	public Location getHQ() {
		return hq;
	}
	public void setHQ(Location hq) {
		this.hq = hq;
		save();
	}
	public String getHQSimple() {
		return hq == null ? "" : Seriloc.Serialize(hq);
	}
	public String getWorkersSimple() {
		String result = "";
		for(Worker w : getWorkers()) {
			result += w.toString() + "<!>";
		}
		if(!result.isEmpty()) {
			result = result.substring(0,result.length() - 3);
		}
		return result;
	}
	public String getWorkersFormatted() {
		String result = "";
		for(Worker w : getWorkers()) {
			result += w.format() + "\n";
		}
		if(!result.isEmpty()) {
//			result = result.substring(0,result.length() - 2);
		}else {
			result += "&7No workers.";
		}
		return result;
	}
	public List<Worker> getWorkers(){
		workers.sort(Worker.weightComparator);
		return this.workers;
	}
	public void deposit(double balance) {
		bank += balance;
		save();
	}
	public boolean withdraw(double balance) {
		if(balance > bank) {
			return false;
		}
		bank -= balance;
		save();
		return true;
	}
	public double getBalance() {
		return this.bank;
	}
	public String getName() {
		return name;
	}
	public UUID getOwner() {
		return this.owner;
	}
	public void setOwner(UUID owner) {
		this.owner = owner;
		save();
	}
	public boolean isWorker(UUID member) {
		return 	
			workers.stream().filter(e -> 
			e.getPlayer().equals(member))
			.findFirst().orElse(null)
			!= null ? true : false;
	}
	public boolean addWorker(UUID member,Role role) {
		if(isWorker(member)) {
			return false;
		}else {
			Worker w = new Worker(this,member,role);
			workers.add(w);
			workers.sort(Worker.weightComparator);
			save();
			return true;
		}
	}
	public boolean removeWorker(Worker worker) {
		if(!isWorker(worker.getPlayer())) {
			return false;
		}else {
			workers.remove(worker);
			save();
			return true;
		}
	}
	public void save() {
		Datamanager.save(this);
	}

}
