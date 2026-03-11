package me.r1ver.login.bungee.listener;

import me.r1ver.login.bungee.BungeeMain;
import me.r1ver.login.bungee.player.BungeePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ConnectListener implements Listener {

	private static final Map<String, Long> PREMIUM_CACHE = new ConcurrentHashMap<>();
	private static final long CACHE_DURATION = TimeUnit.HOURS.toMillis(1);

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(PreLoginEvent event) {
		PendingConnection connection = event.getConnection();
		String name = connection.getName();

		if (name == null || !name.matches("[a-zA-Z0-9_]{3,16}")) {
			event.setCancelled(true);
			event.setCancelReason("§cSeu nick possui caracteres inválidos ou tamanho incorreto!");
			return;
		}

		event.registerIntent(BungeeMain.getInstance());
		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), () -> {
			try {
				boolean isPremium = checkPremiumStatus(name);
				connection.setOnlineMode(isPremium);
			} finally {
				event.completeIntent(BungeeMain.getInstance());
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onServerConnect(ServerConnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		if (player.getServer() != null)
			return;

		UUID uuid = player.getUniqueId();
		BungeePlayer bp = BungeePlayer.get(uuid);
		@SuppressWarnings("deprecation")
		String ip = player.getAddress().getAddress().getHostAddress();

		bp.setIp(ip);
		bp.setType(player.getPendingConnection().isOnlineMode() ? BungeePlayer.PlayerType.ORIGINAL
				: BungeePlayer.PlayerType.CRACKED);
		loadPlayerData(bp, player.getName());

		String configKey = bp.isPremium() ? "server_premium" : "server_crack";
		String serverName = BungeeMain.getInstance().getSettings().getConfig().getString(configKey);

		event.setTarget(ProxyServer.getInstance().getServerInfo(serverName));
	}

	private void loadPlayerData(BungeePlayer bp, String name) {
		var mysql = BungeeMain.getInstance().getMysql();
		mysql.contains(bp.getUuid()).thenAccept(exists -> {
			if (!exists) {
				mysql.createUser(bp.getUuid(), name, bp.getIp());
			} else {
				mysql.read(bp.getUuid(), "password").thenAccept(pass -> {
					if (pass != null)
						bp.setPassword(pass.toString());
				});
			}
		}).join();
	}

	private boolean checkPremiumStatus(String name) {
		String lowerName = name.toLowerCase();
		Long expiry = PREMIUM_CACHE.get(lowerName);

		if (expiry != null && expiry > System.currentTimeMillis()) {
			return true;
		}

		try {
			URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + name);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);

			int response = conn.getResponseCode();
			if (response == 200) {
				PREMIUM_CACHE.put(lowerName, System.currentTimeMillis() + CACHE_DURATION);
				return true;
			}
		} catch (IOException ignored) {
		}

		return false;
	}

	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();

		if (!BungeePlayer.exists(uuid))
			return;
		BungeePlayer bp = BungeePlayer.get(uuid);

		ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.getInstance(), () -> {
			BungeeMain.getInstance().getMysql().savePlayer(bp);
			BungeePlayer.invalidate(uuid);
		});
	}
}