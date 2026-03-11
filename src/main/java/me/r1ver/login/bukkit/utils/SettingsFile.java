package me.r1ver.login.bukkit.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SettingsFile {

	private final Plugin plugin;
	private final String fileName;
	private final File file;
	private FileConfiguration config;

	public SettingsFile(Plugin plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
		this.file = new File(plugin.getDataFolder(), this.fileName);
	}

	public void setup() {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource(fileName, false);
		}
		reload();
	}

	public void reload() {
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Não foi possível salvar o arquivo " + fileName, e);
		}
	}

	public FileConfiguration getConfig() {
		if (this.config == null) {
			setup();
		}
		return this.config;
	}
}