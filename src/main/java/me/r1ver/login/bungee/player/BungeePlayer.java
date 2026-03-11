package me.r1ver.login.bungee.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.r1ver.login.bukkit.SHA1Encrypt;
import me.r1ver.login.bungee.BungeeMain;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@RequiredArgsConstructor
public class BungeePlayer {

	private static final Map<UUID, BungeePlayer> CACHE = new ConcurrentHashMap<>();

	private final UUID uuid;

	@Setter(AccessLevel.NONE)
	private String password;

	private PlayerType type;
	private String ip;

	public enum PlayerType {
		CRACKED, ORIGINAL;
	}

	public boolean isPremium() {
		return this.type == PlayerType.ORIGINAL;
	}

	public boolean hasPasswordDefined() {
		return password != null && !password.isEmpty() && !password.equalsIgnoreCase("Indefinido");
	}

	public boolean verifyPassword(String input) {
		if (input == null || password == null)
			return false;
		return password.equals(SHA1Encrypt.encrypt(input));
	}

	public void changePassword(String newRawPassword) {
		String hashed = SHA1Encrypt.encrypt(newRawPassword);
		this.password = hashed;
		BungeeMain.getInstance().getMysql().update(uuid, "password", hashed);
	}

	public static BungeePlayer get(UUID uuid) {
		return CACHE.computeIfAbsent(uuid, BungeePlayer::new);
	}

	public static void invalidate(UUID uuid) {
		CACHE.remove(uuid);
	}

	public static boolean exists(UUID uuid) {
		return CACHE.containsKey(uuid);
	}
	
	public void setPassword(String password) {
        this.password = password;
    }
}