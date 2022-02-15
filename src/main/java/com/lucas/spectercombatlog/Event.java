package com.lucas.spectercombatlog;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Event implements Listener {

    public void sendActionBar(Player p, String msg) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + msg + "\"}"), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (isInCombat(p) && Main.combate.get(p) > System.currentTimeMillis()) {
            p.setHealth(0);
            World w = p.getWorld();
            for (ItemStack t : p.getInventory().getContents()) {
                w.dropItemNaturally(p.getLocation(), t);
            }
            Bukkit.getOnlinePlayers().stream().forEach(t -> {
                t.sendMessage("§c" + p.getName() + " desconectou em combate!");
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player entity = (Player) e.getEntity();
            Player damager = (Player) e.getDamager();
            putCombat(damager, entity);
        }
    }

    public void putCombat(Player p, Player p2) {
        long l = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
        Main.combate.remove(p);
        Main.combate.remove(p2);
        Main.combate.put(p, l);
        Main.combate.put(p2, l);
        p.sendMessage("§cVocê entrou em combate com " + p2.getName());
        sendActionBar(p, "§c15 segundos para sair de combate com " + p2.getName());
        p.playSound(p.getLocation(), Sound.NOTE_BASS, 10, 1);
        p2.sendMessage("§cVocê entrou em combate com " + p.getName());
        sendActionBar(p2, "§c15 segundos para sair de combate com " + p.getName());
        p2.playSound(p2.getLocation(), Sound.NOTE_BASS, 10, 1);
    }

    public boolean isInCombat(Player p) {
        return Main.combate.containsKey(p);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (isInCombat(p) && !p.hasPermission("specterplugins.combat")) {
            e.setCancelled(true);
            p.sendMessage("§cVocê não pode usar comandos em combate.");
        }
    }
}
