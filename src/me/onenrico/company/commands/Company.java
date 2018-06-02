package me.onenrico.company.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.onenrico.company.database.Datamanager;
import me.onenrico.company.locale.Locales;
import me.onenrico.company.object.ECompany;
import me.onenrico.company.object.Worker;
import me.onenrico.company.object.Worker.Role;
import me.onenrico.company.utils.EconomyUT;
import me.onenrico.company.utils.MessageUT;
import me.onenrico.company.utils.PermissionUT;
import me.onenrico.company.utils.PlaceholderUT;

public class Company implements CommandExecutor{
	public static HashMap<Player,Pair<ECompany,Role>> requestdata = new HashMap<>();
	String prefix = "";
	public void help(Player p)
	{
		List<String> teks = new ArrayList<>();
		if(p.hasPermission("company.admin")) {
			prefix = "&8[&ccomp&8]&6 ";
		}
		teks.add("<np>{doneline}");
		teks.add("<np>");
		teks.add("<np>&6/company "
				+prefix+ "info "
				+ "&7- &eView Your Company Info{edit}");
		teks.add("<np>&6/company playerinfo &7(&aplayer&7) "
				+ "&7- &eView Player Info{edit}");
		teks.add("<np>&6/company create "
				+ "&7(&aname&7) &7- &eCreate New Company{edit}");
		teks.add("<np>&6/company "
				+prefix+ "disband "
				+ "&7- &eDisband Your Company{edit}");
		teks.add("<np>&6/company "
				+prefix+ "sethq "
				+ "&7- &eSet Company HQ{edit}");
		teks.add("<np>&6/company "
				+prefix+ "hq "
				+ "&7- &eTeleport to Company HQ{edit}");
		teks.add("<np>&6/company "
				+prefix+ "setwage "
				+ "&7(&aplayer&7) &7(&awage&7) &7- &eSet wage for your worker{edit}");
		teks.add("<np>&6/company "
				+prefix+ "description "
				+ "&7(&adescription&7) &7- &eSet description for your company{edit}");
		teks.add("<np>&6/company "
				+prefix+ "promote "
				+ "&7(&aplayer&7) &7- &ePromote your worker Role{edit}");
		teks.add("<np>&6/company "
				+prefix+ "demote "
				+ "&7(&aplayer&7) &7- &eDemote your worker Role{edit}");
		teks.add("<np>&6/company "
				+prefix+ "fire "
				+ "&7(&aplayer&7) &7- &eFire your worker{edit}");
		teks.add("<np>&6/company "
				+prefix+ "hire "
				+ "&7(&aplayer&7) &7(&aposition&7) &7- &eHire worker{edit}");
		teks.add("<np>&6/company accept "
				+ "&7- &eAccept company job offer{edit}");
		teks.add("<np>&6/company deny "
				+ "&7- &eDeny company job offer{edit}");
		teks.add("<np>&6/company resign "
				+ "&7- &eResign from your company{edit}");
		teks.add("<np>&6/company "
				+prefix+ "settitle "
				+ "&7(&aplayer&7) &7(&atitle&7) &7- &eSet your worker title{edit}");
		teks.add("<np>&6/company "
				+prefix+ "ownership "
				+ "&7(&aplayer&7) &7- &eChange company ownership");
		teks.add("<np>&6/company "
				+prefix+ "bank "
				+ "&7(&adeposit&7/&awithdraw&7/&abalance&7) &7(&aamount&7) &7- &eManage company bank{edit}");
		teks.add("<np>");
		teks.add("<np>{doneline}");
		MessageUT.plmessage(p, teks);
	}
	
	public static double cost = 0;
	public boolean handle(Player p,String[] args) {
		return handle(p,args,false);
	}
	@SuppressWarnings("deprecation")
	public boolean handle(Player p,String[]args,boolean admin) {
		int add = 0;
		if(admin) {
			add = 1;
		}
		if(args.length >= 1) {
			String arg1 = args[add];
			PlaceholderUT pu = new PlaceholderUT();
			if(arg1.equalsIgnoreCase("info")) {
				if(PermissionUT.check(p, "company.command.info")) {
					ECompany ec = Datamanager.getCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec == null) {
						MessageUT.plmessage(p,pu.t(Locales.getValue("nothave_company")));
						return true;
					}
					pu.add("company", ec.getName());
					if(Bukkit.getOfflinePlayer(ec.getOwner()) == null
							|| Bukkit.getOfflinePlayer(ec.getOwner()).getName() == null) {
						pu.add("leader", "Unknown");
					}else {
						pu.add("leader", Bukkit.getOfflinePlayer(ec.getOwner()).getName());
					}
					pu.add("bank", ""+ec.getBalance());
					pu.add("workers", ec.getWorkersFormatted());
					pu.add("description", ec.getDescription());
					MessageUT.plmessage(p, pu.t(Locales.getValue("company_info")));
					return true;
				}
				return true;
			}
			if(arg1.equalsIgnoreCase("playerinfo")) {
				if(PermissionUT.check(p, "company.command.playerinfo")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company playerinfo "
								+ "&7(&aplayer&7) &7- &eSee player info{edit}");
						return true;
					}
					OfflinePlayer w = Bukkit.getOfflinePlayer(args[1+add]);
					if(w == null) {
						pu.add("worker", args[1+add]);
						MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
						return true;
					}
					ECompany ec = Datamanager.getCompany(w.getUniqueId());
					if(ec == null) {
						pu.add("worker", args[1+add]);
						MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
						return true;
					}
					pu.add("company", ec.getName());
					pu.add("leader", Bukkit.getOfflinePlayer(ec.getOwner()).getName());
					pu.add("bank", ""+ec.getBalance());
					pu.add("workers", ec.getWorkersFormatted());
					pu.add("description", ec.getDescription());
					MessageUT.plmessage(p, pu.t(Locales.getValue("company_info")));
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("create")) {
				if(PermissionUT.check(p, "company.command.create")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company create "
								+ "&7(&aname&7) &7- &eCreate New Company{edit}");
						return true;
					}
					Worker w = Datamanager.getWorker(p.getUniqueId());
					if(w != null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("company_have")));
						return true;
					}
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(ec != null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("company_own")));
						return true;
					}
					ec = Datamanager.getECompany(args[1+add]);
					if(ec != null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("company_exist")));
						return true;
					}
					if(!EconomyUT.has(p, cost)) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("no_money")));
						return true;
					}
					EconomyUT.subtractBal(p, cost);
					String name = MessageUT.d(args[1+add]);
					if(name.length() > 16) {
						name = name.substring(0, 16);
					}
					ec = new ECompany(name, p.getUniqueId());
					pu.add("company", name);
					Datamanager.addData(ec);
					MessageUT.plmessage(p, pu.t(Locales.getValue("created_company")));
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("disband")) {
				if(PermissionUT.check(p, "company.command.disband")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "disband "
								+ "&7- &eDisband Your Company{edit}");
						return true;
					}
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec == null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("notown_company")));
						return true;
					}
					for(Player p2 : requestdata.keySet()) {
						if(requestdata.get(p2).equals(ec)) {
							requestdata.remove(p2);
						}
					}
					pu.add("company", ec.getName());
					pu.add("executor", p.getName());
					Datamanager.deleteData(ec, null);
					MessageUT.plmessage(p, pu.t(Locales.getValue("disbanded_company")));
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("sethq")) {
				if(PermissionUT.check(p, "company.command.sethq")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "sethq "
								+ "&7- &eSet Company HQ{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
							ec = w.getCompany();
						}
					}
					if(allow) {
						pu.add("company", ec.getName());
						pu.add("executor", p.getName());
						MessageUT.plmessage(p, pu.t(Locales.getValue("hq_set")));
						ec.setHQ(p.getLocation());
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("hq")) {
				if(PermissionUT.check(p, "company.command.hq")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "hq "
								+ "&7- &eTeleport to Company HQ{edit}");
						return true;
					}
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					Location hq = null;
					if(ec != null) {
						hq = ec.getHQ();
						pu.add("company", ec.getName());
					}
					if(hq == null) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w != null) {
							hq = w.getCompany().getHQ();
							pu.add("company", w.getCompany().getName());
						}
						else if(ec == null){
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
					}
					if(hq == null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("hq_notset")));
						return true;
					}
					p.teleport(hq);
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("setwage")) {
				if(PermissionUT.check(p, "company.command.setwage")) {
					if(args.length != add + 3) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "setwage "
								+ "&7(&aplayer&7) &7(&awage&7) &7- &eSet wage for your worker{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
							ec = w.getCompany();
						}
					}
					if(allow) {
						Worker w = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[add+1]).getUniqueId());
						if (admin) {
							if(w == null) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}else {
							if(w == null || !Datamanager.getCompany(p.getUniqueId()).equals(w.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}
						double wage = 0;
						try {
							wage = Double.valueOf(args[2+add]);
							wage = Math.abs(wage);
						}catch(NumberFormatException e) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("must_number")));
							return true;
						}
						pu.add("wage", ""+wage);
						pu.add("company", ""+w.getCompany().getName());
						pu.add("executor", p.getName());
						pu.add("worker", w.format());
						MessageUT.plmessage(p, pu.t(Locales.getValue("wage_set")));
						if(Bukkit.getOfflinePlayer(w.getPlayer()).isOnline()) {
							MessageUT.plmessage(Bukkit.getPlayer(w.getPlayer())
									, pu.t(Locales.getValue("wage_changed")));
						}
						for(Worker w2 : ec.getWorkers()) {
							if(w2.getPlayer().equals(w.getPlayer())) {
								continue;
							}
							if(w2.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w2.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("wage_set_notify")));
							}
						}
						if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
							if(!ec.getOwner().equals(p.getUniqueId())) {
								MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
										pu.t(Locales.getValue("wage_set_notify")));
							}
						}
						w.setWage(wage);
						w.getCompany().save();
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("description")) {
				if(PermissionUT.check(p, "company.command.description")) {
					if(args.length < 2) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "description "
								+ "&7(&adescription&7) &7- &eSet description for your company{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
						}
						if(allow) {
							String desc = args[1+add]+" ";
							for(int x = 2+add;x<args.length;x++) {
								desc += args[x]+" ";
							}
							desc = desc.trim();
							pu.add("description", desc);
							if(ec == null) {
								ec = w.getCompany();
							}
							pu.add("company", ec.getName());
							pu.add("executor", p.getName());
							MessageUT.plmessage(p, pu.t(Locales.getValue("description_set")));
							w.getCompany().setDescription(desc);
							for(Worker w2 : w.getCompany().getWorkers()) {
								if(w2.getOffPlayer().isOnline()) {
									Player p2 = Bukkit.getPlayer(w2.getPlayer());
									MessageUT.plmessage(p2
											, pu.t(Locales.getValue("description_set_notify")));
								}
							}
							if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
								MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
										pu.t(Locales.getValue("description_set_notify")));
							}
							return true;
						}
					}
					if(allow) {
						String desc = args[1+add]+" ";
						for(int x = 2+add;x<args.length;x++) {
							desc += args[x]+" ";
						}
						desc = desc.trim();
						pu.add("description", desc);
						pu.add("company", ec.getName());
						pu.add("executor", p.getName());
						MessageUT.plmessage(p, pu.t(Locales.getValue("description_set")));
						ec.setDescription(desc);
						for(Worker w : ec.getWorkers()) {
							if(w.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("description_set_notify")));
							}
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("promote")) {
				if(PermissionUT.check(p, "company.command.promote")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "promote "
								+ "&7(&aplayer&7) &7- &ePromote your worker Role{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
						}
						if(allow) {
							Worker w2 = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
							if(w2 == null || !Datamanager.getCompany(p.getUniqueId()).equals(w2.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
							pu.add("company", w2.getCompany().getName());
							if(w2.getRole().equals(Role.MANAGER)) {
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_cannot")));
								return true;
							}
							if(w2.promote()) {
								pu.add("role", w2.getRole().toString());
								pu.add("executor", p.getName());
								pu.add("worker", w2.format());
								w2.getCompany().save();
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_promote")));
								if(Bukkit.getOfflinePlayer(w2.getPlayer()).isOnline()) {
									MessageUT.plmessage(Bukkit.getPlayer(w2.getPlayer())
											, pu.t(Locales.getValue("role_promoted")));
								}
								for(Worker w3 : w.getCompany().getWorkers()) {
									if(w3.getPlayer().equals(w2.getPlayer())) continue;
									if(w3.getOffPlayer().isOnline()) {
										Player p2 = Bukkit.getPlayer(w3.getPlayer());
										MessageUT.plmessage(p2
												, pu.t(Locales.getValue("role_promoted_notify")));
									}
								}
								if(Bukkit.getOfflinePlayer(w.getCompany().getOwner()).isOnline()) {
									if(!w.getCompany().getOwner().equals(p.getUniqueId())) {
										MessageUT.plmessage(Bukkit.getPlayer(w.getCompany().getOwner()), 
												pu.t(Locales.getValue("role_promoted_notify")));
									}
								}
								return true;
							}else {
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_exceed")));
							}
							return true;
						}
					}
					if(allow) {
						Worker w = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
						if (admin) {
							if(w == null) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}else {
							if(w == null || !Datamanager.getCompany(p.getUniqueId()).equals(w.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}
						pu.add("role", w.getRole().promote().toString());
						pu.add("company", w.getCompany().getName());
						if(w.promote()) {
							pu.add("executor", p.getName());
							pu.add("worker", w.format());
							w.getCompany().save();
							MessageUT.plmessage(p, pu.t(Locales.getValue("role_promote")));
							if(Bukkit.getOfflinePlayer(w.getPlayer()).isOnline()) {
								MessageUT.plmessage(Bukkit.getPlayer(w.getPlayer())
										, pu.t(Locales.getValue("role_promoted")));
							}
							for(Worker w3 : w.getCompany().getWorkers()) {
								if(w.getPlayer().equals(w3.getPlayer()))continue;
								if(w3.getOffPlayer().isOnline()) {
									Player p2 = Bukkit.getPlayer(w3.getPlayer());
									MessageUT.plmessage(p2
											, pu.t(Locales.getValue("role_promoted_notify")));
								}
							}
							if(Bukkit.getOfflinePlayer(w.getCompany().getOwner()).isOnline()) {
								if(!w.getCompany().getOwner().equals(p.getUniqueId())) {
									MessageUT.plmessage(Bukkit.getPlayer(w.getCompany().getOwner()), 
											pu.t(Locales.getValue("role_promoted_notify")));
								}
							}
						}else {
							MessageUT.plmessage(p, pu.t(Locales.getValue("role_exceed")));
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("demote")) {
				if(PermissionUT.check(p, "company.command.demote")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "demote "
								+ "&7(&aplayer&7) &7- &eDemote your worker Role{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
						}
						if(allow) {
							Worker w2 = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
							if(w2 == null || !Datamanager.getCompany(p.getUniqueId()).equals(w2.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
							pu.add("company", w2.getCompany().getName());
							if(w2.getRole().equals(Role.CEO)) {
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_cannot")));
								return true;
							}
							if(w2.demote()) {
								pu.add("role", w.getRole().toString());
								pu.add("executor", p.getName());
								pu.add("worker", w2.format());
								w2.getCompany().save();
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_demote")));
								if(Bukkit.getOfflinePlayer(w2.getPlayer()).isOnline()) {
									MessageUT.plmessage(Bukkit.getPlayer(w2.getPlayer())
											, pu.t(Locales.getValue("role_demoted")));
								}
								for(Worker w3 : w.getCompany().getWorkers()) {
									if(w3.getPlayer().equals(w2.getPlayer())) continue;
									if(w3.getOffPlayer().isOnline()) {
										Player p2 = Bukkit.getPlayer(w3.getPlayer());
										MessageUT.plmessage(p2
												, pu.t(Locales.getValue("role_demoted_notify")));
									}
								}
								if(Bukkit.getOfflinePlayer(w.getCompany().getOwner()).isOnline()) {
									if(!w.getCompany().getOwner().equals(p.getUniqueId())) {
										MessageUT.plmessage(Bukkit.getPlayer(w.getCompany().getOwner()), 
												pu.t(Locales.getValue("role_demoted_notify")));
									}
								}
								return true;
							}else {
								MessageUT.plmessage(p, pu.t(Locales.getValue("role_exceed")));
							}
							return true;
						}
					}
					if(allow) {
						Worker w = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
						if (admin) {
							if(w == null) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}else {
							if(w == null || !Datamanager.getCompany(p.getUniqueId()).equals(w.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}
						pu.add("role", w.getRole().demote().toString());
						pu.add("company", w.getCompany().getName());
						if(w.demote()) {
							pu.add("executor", p.getName());
							pu.add("worker", w.format());
							w.getCompany().save();
							MessageUT.plmessage(p, pu.t(Locales.getValue("role_demote")));
							if(Bukkit.getOfflinePlayer(w.getPlayer()).isOnline()) {
								MessageUT.plmessage(Bukkit.getPlayer(w.getPlayer())
										, pu.t(Locales.getValue("role_demoted")));
							}
							for(Worker w3 : w.getCompany().getWorkers()) {
								if(w3.getPlayer().equals(w.getPlayer()))continue;
								if(w3.getOffPlayer().isOnline()) {
									Player p2 = Bukkit.getPlayer(w3.getPlayer());
									MessageUT.plmessage(p2
											, pu.t(Locales.getValue("rolde_demoted_notify")));
								}
							}
							if(Bukkit.getOfflinePlayer(w.getCompany().getOwner()).isOnline()) {
								if(!w.getCompany().getOwner().equals(p.getUniqueId())) {
									MessageUT.plmessage(Bukkit.getPlayer(w.getCompany().getOwner()), 
											pu.t(Locales.getValue("role_demomoted_notify")));
								}
							}
						}else {
							MessageUT.plmessage(p, pu.t(Locales.getValue("role_exceed")));
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("fire")) {
				if(PermissionUT.check(p, "company.command.fire")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "fire "
								+ "&7(&aplayer&7) &7- &eFire your worker Role{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO) || w.getRole().equals(Role.MANAGER)) {
							allow = true;
						}
					}
					if(allow) {
						Worker w = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
						if(w == null || !Datamanager.getCompany(p.getUniqueId()).equals(w.getCompany())) {
							pu.add("worker", args[1+add]);
							MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
							return true;
						}
						if(w.fire()) {
							pu.add("executor", p.getName());
							pu.add("worker", w.getOffPlayer().getName());
							w.getCompany().save();
							MessageUT.plmessage(p, pu.t(Locales.getValue("worker_fire")));
							if(Bukkit.getOfflinePlayer(w.getPlayer()).isOnline()) {
								MessageUT.plmessage(Bukkit.getPlayer(w.getPlayer())
										, pu.t(Locales.getValue("worker_fired")));
							}
							for(Worker w3 : w.getCompany().getWorkers()) {
								if(w3.getOffPlayer().isOnline()) {
									Player p2 = Bukkit.getPlayer(w3.getPlayer());
									MessageUT.plmessage(p2
											, pu.t(Locales.getValue("worker_fired_notify")));
								}
							}
							if(Bukkit.getOfflinePlayer(w.getCompany().getOwner()).isOnline()) {
								if(!w.getCompany().getOwner().equals(p.getUniqueId())) {
									MessageUT.plmessage(Bukkit.getPlayer(w.getCompany().getOwner()), 
											pu.t(Locales.getValue("worker_fired_notify")));
								}
							}
						}else {
							MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("hire")) {
				if(PermissionUT.check(p, "company.command.hire")) {
					if(args.length != add + 3) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "hire "
								+ "&7(&aplayer&7) &7(&aposition&7) &7- &eHire worker{edit}");
						return true;
					}
					boolean allow = false;
					boolean owner = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
						owner = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO) || w.getRole().equals(Role.MANAGER)) {
							allow = true;
						}
					}
					if(allow) {
						Player target = Bukkit.getPlayer(args[1+add]);
						Role role = null;
						try {
							role = Role.valueOf(args[2+add].toUpperCase());
						}catch(Exception ex) {
							MessageUT.plmessage(p, "Available Position: CEO,Manager,Employee");
							return true;
						}
						if(!owner && 
								Datamanager.getWorker(p.getUniqueId()).getRole().getWeight() <= 
								role.getWeight()) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("role_cannot")));
							return true;
						}
						if(target != null) {
							pu.add("executor", p.getName());
							pu.add("worker", target.getName());
							if(admin) {
								pu.add("company", ec.getName());
							}else {
								pu.add("company", Datamanager.getCompany(p.getUniqueId()).getName());
							}
							pu.add("role", ""+role);
							if(Datamanager.getCompany(target.getUniqueId()) != null) {
								MessageUT.plmessage(p, pu.t(Locales.getValue("target_have_company")));
								return true;
							}
							MessageUT.plmessage(p, pu.t(Locales.getValue("worker_hire")));
							MessageUT.plmessage(target
									, pu.t(Locales.getValue("worker_hired")));
							if(admin) {
								requestdata.put(target, Pair.of(ec,role));
							}else {
								requestdata.put(target, Pair.of(Datamanager.getCompany(p.getUniqueId()),role));
							}
						}else {
							pu.add("worker", args[1+add]);
							MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("accept")) {
				if(PermissionUT.check(p, "company.command.accept")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company accept "
								+ "&7- &eAccept company job offer{edit}");
						return true;
					}
					if(requestdata.containsKey(p)) {
						ECompany ec = requestdata.get(p).getKey();
						pu.add("executor", p.getName());
						pu.add("worker", p.getName());
						MessageUT.plmessage(p, pu.t(Locales.getValue("worker_joined")));
						for(Worker w3 : ec.getWorkers()) {
							if(w3.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w3.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("worker_joined_notify")));
							}
						}
						ec.addWorker(p.getUniqueId(), requestdata.get(p).getValue());
						if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
							MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
									pu.t(Locales.getValue("worker_joined_notify")));
						}
						requestdata.remove(p);
						return true;
					}
				}
				return true;
			}else if(arg1.equalsIgnoreCase("deny")) {
				if(PermissionUT.check(p, "company.command.deny")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company deny"
								+ "&7- &eDeny company job offer{edit}");
						return true;
					}
					if(requestdata.containsKey(p)) {
						ECompany ec = requestdata.get(p).getKey();
						requestdata.remove(p);
						pu.add("executor", p.getName());
						pu.add("worker", p.getName());
						MessageUT.plmessage(p, pu.t(Locales.getValue("worker_denied")));
						for(Worker w3 : ec.getWorkers()) {
							if(w3.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w3.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("worker_denied_notify")));
							}
						}
						if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
							MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
									pu.t(Locales.getValue("worker_denied_notify")));
						}
						return true;
					}
				}
				return true;
			}else if(arg1.equalsIgnoreCase("resign")) {
				if(PermissionUT.check(p, "company.command.resign")) {
					if(args.length != add + 1) {
						MessageUT.plmessage(p, "<np>&6/company resign"
								+ "&7- &eResigxn from your company{edit}");
						return true;
					}

					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(ec != null) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "ownership "
								+ "&7(&aplayer&7) &7- &eChange company ownership");
						return true;
					}
					ec = Datamanager.getCompany(p.getUniqueId());
					if(ec == null) {
						MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
						return true;
					}
					Worker w = Datamanager.getWorker(p.getUniqueId());
					pu.add("company", ec.getName());
					pu.add("worker", w.format());
					ec.removeWorker(w);
					MessageUT.plmessage(p, pu.t(Locales.getValue("worker_resigned")));
					for(Worker w3 : ec.getWorkers()) {
						if(w3.getOffPlayer().isOnline()) {
							Player p2 = Bukkit.getPlayer(w3.getPlayer());
							MessageUT.plmessage(p2
									, pu.t(Locales.getValue("worker_resigned_notify")));
						}
					}
					if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
						MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
								pu.t(Locales.getValue("worker_resigned_notify")));
					}
				}
				return true;
			}else if(arg1.equalsIgnoreCase("settitle")) {
				if(PermissionUT.check(p, "company.command.settile")) {
					if(args.length != add + 3) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "settitle "
								+ "&7(&aplayer&7) &7(&atitle&7) &7- &eSet your worker title{edit}");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("nothave_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)) {
							allow = true;
						}
					}
					if(allow) {
						Worker w = Datamanager.getWorker(Bukkit.getOfflinePlayer(args[1+add]).getUniqueId());
						if (admin) {
							if(w == null) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}else {
							if(w == null || !Datamanager.getCompany(p.getUniqueId()).equals(w.getCompany())) {
								pu.add("worker", args[1+add]);
								MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
								return true;
							}
						}
						String title = args[2+add];
						pu.add("title", ""+title);
						pu.add("company", ""+w.getCompany().getName());
						pu.add("executor", p.getName());
						pu.add("worker", w.format());
						MessageUT.plmessage(p, pu.t(Locales.getValue("title_set")));
						if(Bukkit.getOfflinePlayer(w.getPlayer()).isOnline()) {
							MessageUT.plmessage(Bukkit.getPlayer(w.getPlayer())
									, pu.t(Locales.getValue("title_changed")));
						}
						for(Worker w2 : ec.getWorkers()) {
							if(w2.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w2.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("title_set_notify")));
							}
						}
						if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
							MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
									pu.t(Locales.getValue("title_set_notify")));
						}
						w.setTitle(title);
						w.getCompany().save();
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("ownership")) {
				if(PermissionUT.check(p, "company.command.ownership")) {
					if(args.length != add + 2) {
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "ownership "
								+ "&7(&aplayer&7) &7- &eChange company ownership");
						return true;
					}
					boolean allow = false;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					if(allow) {
						OfflinePlayer w = Bukkit.getOfflinePlayer(args[1+add]);
						if(w == null) {
							pu.add("worker", args[1+add]);
							MessageUT.plmessage(p, pu.t(Locales.getValue("worker_not_found")));
							return true;
						}
						if(Datamanager.getWorker(w.getUniqueId()) == null
								|| !Datamanager.getWorker(w.getUniqueId()).getCompany().equals(ec)) {
							pu.add("worker", args[1+add]);
							MessageUT.plmessage(p, pu.t(Locales.getValue("target_have_company")));
							return true;
						}
						pu.add("company", ""+ec.getName());
						pu.add("executor", p.getName());
						pu.add("worker", w.getName());
						MessageUT.plmessage(p, pu.t(Locales.getValue("ownership_change")));
						if(w.isOnline()) {
							MessageUT.plmessage(w.getPlayer()
									, pu.t(Locales.getValue("ownership_changed")));
						}
						for(Worker w2 : new ArrayList<>(ec.getWorkers())) {
							if(w2.getPlayer().equals(w.getUniqueId())) {
								ec.removeWorker(w2);
								continue;
							}
							if(w2.getOffPlayer().isOnline()) {
								Player p2 = Bukkit.getPlayer(w2.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("ownership_changed_notify")));
							}
						}
						ec.addWorker(ec.getOwner(), Role.EMPLOYEE);
						ec.setOwner(w.getUniqueId());
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else if(arg1.equalsIgnoreCase("bank")) {
				if(PermissionUT.check(p, "company.command.bank")) {
					if(
							(
									(args.length < 2 + add || args.length > 3 + add) 
									
							)
							||
							(
									(args.length == 3 + add) 
									&& 
									(
										!args[1+add].equalsIgnoreCase("deposit")
										&&
										!args[1+add].equalsIgnoreCase("withdraw")		
									)
							)
							||
							(
									(args.length == 2 + add) 
									&& 
									(
										!args[1+add].equalsIgnoreCase("balance")	
									)
							)
					  ) 
					{
						MessageUT.plmessage(p, "<np>&6/company "
								+prefix+ "bank "
								+ "&7(&adeposit&7/&awithdraw&7/&abalance&7) &7(&aamount&7) &7- "
								+ "&eManage company bank{edit}");
						return true;
					}
					String mode = args[1+add];
					boolean withdraw = false;
					boolean deposit = false;
					boolean balance = false;
					switch(mode.toLowerCase()) {
					case "withdraw":
						withdraw = true;
						break;
					case "deposit":
						deposit = true;
						break;
					case "balance":
						balance = true;
						break;
					}
					boolean allow = false;
					double amount = 0;
					ECompany ec = Datamanager.getOwnedCompany(p.getUniqueId());
					if(admin) {
						ec = Datamanager.getECompany(args[0]);
						if(ec == null) {
							pu.add("company", args[0]);
							MessageUT.plmessage(p,pu.t(Locales.getValue("company_not_found")));
							return true;
						}
					}
					if(ec != null) {
						allow = true;
					}
					pu.add("executor", p.getName());
					if(deposit) {
						try {
							amount = Double.valueOf(args[2+add]);
							amount = Math.abs(amount);
						}catch(NumberFormatException e) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("must_number")));
							return true;
						}
						if((!EconomyUT.has(p, amount))
								||
								(amount <= 0)
								) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("no_money")));
							return true;
						}
						pu.add("amount", ""+amount);
						if(ec == null) {
							ec = Datamanager.getCompany(p.getUniqueId());
							if(ec == null) {
								MessageUT.plmessage(p, pu.t(Locales.getValue("notown_company")));
								return true;
							}
						}
						pu.add("company", ""+ec.getName());
						ec.deposit(amount);
						EconomyUT.subtractBal(p, amount);
						MessageUT.plmessage(p, pu.t(Locales.getValue("bank_deposit")));
						for(Worker w2 : ec.getWorkers()) {
							if(w2.getOffPlayer().isOnline()) {
								if(w2.getOffPlayer().getName().equals(p.getName())) {
									continue;
								}
								Player p2 = Bukkit.getPlayer(w2.getPlayer());
								MessageUT.plmessage(p2
										, pu.t(Locales.getValue("bank_deposited")));
							}
						}
						if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {
							if(!ec.getOwner().equals(p.getUniqueId())) {
								MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
										pu.t(Locales.getValue("bank_deposited")));
							}
						}
						return true;
					}
					if(!allow) {
						Worker w = Datamanager.getWorker(p.getUniqueId());
						if(w == null) {
							MessageUT.plmessage(p, pu.t(Locales.getValue("notown_company")));
							return true;
						}
						if(w.getRole().equals(Role.CEO)){
							allow = true;
						}
					}
					if(allow) {
						if(balance) {
							pu.add("bank", ""+ec.getBalance());
							MessageUT.plmessage(p, pu.t(Locales.getValue("bank_balance")));
						}
						if(withdraw) {
							try {
								amount = Double.valueOf(args[2+add]);
								amount = Math.abs(amount);
							}catch(NumberFormatException e) {
								MessageUT.plmessage(p, pu.t(Locales.getValue("must_number")));
								return true;
							}
							pu.add("amount", ""+amount);
							if(ec.withdraw(amount)) {
								EconomyUT.addBal(p, amount);
								pu.add("amount", ""+amount);
								MessageUT.plmessage(p, pu.t(Locales.getValue("bank_withdraw")));
								for(Worker w2 : ec.getWorkers()) {
									if(w2.getOffPlayer().isOnline()) {
										if(w2.getOffPlayer().getName().equals(p.getName())) {
											continue;
										}
										Player p2 = Bukkit.getPlayer(w2.getPlayer());
										MessageUT.plmessage(p2
												, pu.t(Locales.getValue("bank_withdrawed")));
									}
								}
								if(Bukkit.getOfflinePlayer(ec.getOwner()).isOnline()) {

									if(!ec.getOwner().equals(p.getUniqueId())) {
										MessageUT.plmessage(Bukkit.getPlayer(ec.getOwner()), 
												pu.t(Locales.getValue("bank_withdrawed")));
									}
								}
							}else {
								MessageUT.plmessage(p, pu.t(Locales.getValue("no_money_company")));
							}
						}
					}else {
						MessageUT.plmessage(p, pu.t(Locales.getValue("not_permitted")));
					}
					return true;
				}
				return true;
			}else {
				if(!admin) {
					if(p.hasPermission("company.admin")) {
						if(args.length >= 2) {
							handle(p,args,true);
							return true;
						}
					}
				}
			}
		}
		help(p);
		return true;
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		Player p = null;
		if(cs instanceof Player) {
			p = (Player) cs;
		}
		handle(p,args);
		return true;
	}

}
