package com.lucas.spectercombatlog;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

	public static Main instance;
	public static boolean validInit, chave;
	public static com.lucas.spectercombatlog.Checker checker;
	public static Event e;
	public static HashMap<Player, Long> combate = new HashMap<>();

	public static Main getInstance() {
		return instance;
	}

	public void onEnable() {
		checker = new com.lucas.spectercombatlog.Checker();

		if (checker.checkKey()) {
			this.getLogger().info("§2CHECKER: Chave valida");
			this.getLogger().info("§2O plugin foi ativado!");
			chave = true;
		} else {
			this.getLogger().severe("§4CHECKER: Chave invalida");
			this.setEnabled(false);
			chave = false;
		}

		saveDefaultConfig();
		saveConfig();

		if (chave = true) {
			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getConsoleSender().sendMessage("§7==========================");
			Bukkit.getConsoleSender().sendMessage("§7| §bSpecterCombatLog       §7|");
			Bukkit.getConsoleSender().sendMessage("§7| §bVersão 1.0             §7|");
			Bukkit.getConsoleSender().sendMessage("§7| §fStatus: §aLigado       §7|");
			Bukkit.getConsoleSender().sendMessage("§7==========================");
			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getPluginManager().registerEvents(new Event(), this);
			checkerc();

			validInit = true;
		} else {

			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getConsoleSender().sendMessage("§4==========================");
			Bukkit.getConsoleSender().sendMessage("§4| §cSpecterCombatLog       §4|");
			Bukkit.getConsoleSender().sendMessage("§4| §cVersão 1.0             §4|");
			Bukkit.getConsoleSender().sendMessage("§4| §fStatus: §cDesligado       §4|");
			Bukkit.getConsoleSender().sendMessage("§4==========================");
			Bukkit.getConsoleSender().sendMessage("");

		}
	}

	public void checkerc() {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> saiu = Main.combate.keySet().stream().filter(t -> Main.combate.get(t) < System.currentTimeMillis())
						.collect(Collectors.toList());
				for (Player p : saiu) {
					Main.combate.remove(p);
					p.sendMessage("§a§lCOMBATE §aVocê saiu de combate!");
					e.sendActionBar(p, "§aVocê saiu de combate!");
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 10, 1);
				}
			}

		}.runTaskTimerAsynchronously(Main.getInstance(), 20L, 5 * 20L);
	}

	@Override
	public void onDisable() {
		if (chave) {
			chave = false;
		}
	}

	private static class Checker {
		public static void check() {}
	}
}
