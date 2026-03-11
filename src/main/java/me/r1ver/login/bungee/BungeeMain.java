package me.r1ver.login.bungee;

import lombok.Getter;
import me.r1ver.login.bungee.api.YamlConfig;
import me.r1ver.login.bungee.commands.ChangePassCommand;
import me.r1ver.login.bungee.listener.ConnectListener;
import me.r1ver.login.database.MySQL;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.logging.Level;

@Getter
public class BungeeMain extends Plugin {

    @Getter
    private static BungeeMain instance;

    private MySQL mysql;
    private YamlConfig settings;

    @Override
    public void onEnable() {
        instance = this;

        try {
            setupConfigs();
            if (!setupDatabase()) {
                getLogger().severe("§cFALHA CRÍTICA: Não foi possível conectar ao banco de dados. Desativando...");
                return;
            }
            registerCommands();
            registerListeners();
            startCleanupTasks();

            getLogger().info("§aNeymarLogin v" + getDescription().getVersion() + " ativado com sucesso.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Ocorreu um erro inesperado durante a inicialização:", e);
        }
    }

    private void setupConfigs() {
        this.settings = new YamlConfig("config.yml", this);
        this.settings.saveDefaultConfig();
    }

    private boolean setupDatabase() {
        Configuration config = settings.getConfig().getSection("MySQL");

        if (config == null || config.getString("host").isEmpty()) {
            return false;
        }

        try {
            this.mysql = new MySQL(
                config.getString("user"),
                config.getString("host"),
                config.getString("database"),
                config.getString("password"),
                config.getInt("port")
            );
            this.mysql.createTables();
            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Erro ao conectar no MySQL:", e);
            return false;
        }
    }

    private void registerCommands() {
        var pm = getProxy().getPluginManager();
        pm.registerCommand(this, new ChangePassCommand());
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new ConnectListener());
    }

    private void startCleanupTasks() {
    }

    @Override
    public void onDisable() {
        if (mysql != null) {
            mysql.closeConnection();
        }
        getProxy().getScheduler().cancel(this);
        
        getLogger().info("NeymarLogin finalizado com segurança.");
        instance = null;
    }
}