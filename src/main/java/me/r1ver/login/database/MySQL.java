package me.r1ver.login.database;

import org.bukkit.Bukkit;

import me.r1ver.login.bungee.player.BungeePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQL extends MySQLManager {

    public MySQL(String user, String host, String database, String password, int port) {
        super(user, host, database, password, port);
    }

    public void createTables() {
        CompletableFuture.runAsync(() -> {
            String query = "CREATE TABLE IF NOT EXISTS players_login (" +
                           "uuid VARCHAR(36) PRIMARY KEY, " +
                           "nick VARCHAR(16), " +
                           "ip VARCHAR(45), " +
                           "password VARCHAR(255))";
            
            try (Connection conn = startConnection();
                 PreparedStatement st = conn.prepareStatement(query)) {
                st.executeUpdate();
                Bukkit.getLogger().info("[MySQL] Tabela players_login verificada/criada (Async).");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Object> read(UUID uuid, String coluna) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT " + coluna + " FROM players_login WHERE uuid = ?";
            
            try (Connection conn = startConnection();
                 PreparedStatement st = conn.prepareStatement(query)) {
                
                st.setString(1, uuid.toString());
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        return rs.getObject(coluna);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void update(UUID uuid, String coluna, Object novoValor) {
        CompletableFuture.runAsync(() -> {
            String query = "UPDATE players_login SET " + coluna + " = ? WHERE uuid = ?";
            
            try (Connection conn = startConnection();
                 PreparedStatement st = conn.prepareStatement(query)) {
                
                st.setObject(1, novoValor);
                st.setString(2, uuid.toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void createUser(UUID uuid, String nick, String ip) {
        contains(uuid).thenAccept(exists -> {
            if (exists) return;

            CompletableFuture.runAsync(() -> {
                String query = "INSERT INTO players_login (uuid, nick, ip) VALUES (?, ?, ?)";
                try (Connection conn = startConnection();
                     PreparedStatement st = conn.prepareStatement(query)) {
                    
                    st.setString(1, uuid.toString());
                    st.setString(2, nick);
                    st.setString(3, ip);
                    st.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public CompletableFuture<Boolean> contains(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM players_login WHERE uuid = ? LIMIT 1";
            
            try (Connection conn = startConnection();
                 PreparedStatement st = conn.prepareStatement(query)) {
                
                st.setString(1, uuid.toString());
                try (ResultSet rs = st.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public void savePlayer(BungeePlayer bp) {
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO players_login (uuid, nick, ip, password) VALUES (?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE nick = VALUES(nick), ip = VALUES(ip), password = VALUES(password)";
            
            try (Connection conn = startConnection();
                 PreparedStatement st = conn.prepareStatement(query)) {
                
                st.setString(1, bp.getUuid().toString());
                st.setString(2, "Desconhecido"); 
                st.setString(3, bp.getIp());
                st.setString(4, bp.getPassword());
                
                st.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("[MySQL] Erro crítico ao salvar jogador " + bp.getUuid() + ": " + e.getMessage());
            }
        });
    }
}