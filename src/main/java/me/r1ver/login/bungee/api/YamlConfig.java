package me.r1ver.login.bungee.api;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class YamlConfig {

	private final String fileName;
	private final Plugin plugin;
	private final File configFile;

	private Configuration config;

	public YamlConfig(String fileName, Plugin plugin) {
		this.fileName = fileName;
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), fileName);
	}

	public void saveDefaultConfig() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}

		if (!configFile.exists()) {
			try (InputStream is = plugin.getResourceAsStream(fileName)) {
				if (is != null) {
					Files.copy(is, configFile.toPath());
				} else {
					configFile.createNewFile();
				}
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "Não foi possível criar o arquivo: " + fileName, e);
			}
		}

		reloadConfig();
	}

	public void reloadConfig() {
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Erro ao carregar o arquivo: " + fileName, e);
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Erro ao salvar o arquivo: " + fileName, e);
		}
	}

	public Configuration getConfig() {
		if (config == null)
			reloadConfig();
		return config;
	}
}