package me.r1ver.login.bukkit.listener;

import me.r1ver.login.BukkitMain;
import me.r1ver.login.bukkit.player.BukkitPlayer;
import me.r1ver.login.bukkit.player.BukkitPlayer.LoginStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListeners implements Listener {

	private final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();
	private final Location spawnLoc = new Location(Bukkit.getWorld("world"), 0.5, 240, 0.5);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(null);
		p.teleport(spawnLoc);
		p.setHealth(20);
		p.setFoodLevel(20);

		BukkitPlayer bp = BukkitPlayer.get(p.getUniqueId());
		BukkitMain.getInstance().getMysql().read(p.getUniqueId(), "password").thenAccept(pass -> {
			Bukkit.getScheduler().runTask(BukkitMain.getInstance(), () -> {
				if (pass != null) {
					bp.setPassword(pass.toString());
					bp.setStatus(LoginStatus.LOGIN);
					clearChatAndMessage(p, "§b§lLOGIN §fBem-vindo de volta! Use: §b/login <senha>");
				} else {
					bp.setStatus(LoginStatus.REGISTER);
					clearChatAndMessage(p, "§b§lREGISTRO §fCrie sua conta: §b/registrar <senha> <senha>");
				}
				startTimeoutTask(p);
			});
		});
	}

	private void clearChatAndMessage(Player p, String msg) {
		for (int i = 0; i < 40; i++)
			p.sendMessage("");
		p.sendMessage(msg);
	}

	private void startTimeoutTask(Player p) {
		BukkitTask task = Bukkit.getScheduler().runTaskLater(BukkitMain.getInstance(), () -> {
			if (p.isOnline() && !BukkitPlayer.get(p.getUniqueId()).isAuthenticated()) {
				p.kickPlayer("§cTempo de autenticação esgotado.");
			}
		}, 45 * 20L);
		timeoutTasks.put(p.getUniqueId(), task);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		if (timeoutTasks.containsKey(uuid)) {
			timeoutTasks.get(uuid).cancel();
			timeoutTasks.remove(uuid);
		}
		BukkitPlayer.remove(uuid);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		BukkitPlayer bp = BukkitPlayer.get(e.getPlayer().getUniqueId());
		if (!bp.isAuthenticated()) {
			if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
				e.setTo(e.getFrom());
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (!BukkitPlayer.get(e.getPlayer().getUniqueId()).isAuthenticated())
			e.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (!isAuth(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (!isAuth(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (!isAuth(e.getPlayer()))
			e.setCancelled(true);
	}

	private boolean isAuth(Player p) {
		return BukkitPlayer.get(p.getUniqueId()).isAuthenticated();
	}
}