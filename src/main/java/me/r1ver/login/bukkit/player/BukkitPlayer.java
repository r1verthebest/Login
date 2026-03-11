package me.r1ver.login.bukkit.player;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class BukkitPlayer {

	private static final Map<UUID, BukkitPlayer> CACHE = new ConcurrentHashMap<>();

	private final UUID uuid;
	private String password;
	private String ip;
	private LoginStatus status;

	public BukkitPlayer(UUID uuid) {
		this.uuid = uuid;
		this.status = LoginStatus.REGISTER;
	}

	public enum LoginStatus {
		LOGIN, REGISTER, AUTHENTICATED;
	}

	public boolean isAuthenticated() {
		return this.status == LoginStatus.AUTHENTICATED;
	}

	public static BukkitPlayer get(UUID uuid) {
		return CACHE.computeIfAbsent(uuid, BukkitPlayer::new);
	}

	public static void remove(UUID uuid) {
		CACHE.remove(uuid);
	}

	public static boolean isCached(UUID uuid) {
		return CACHE.containsKey(uuid);
	}
}