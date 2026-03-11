package me.r1ver.login.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLManager {

	private final String user, password, url;
	private Connection connection;

	public MySQLManager(String user, String host, String password, String database, int port) {
		this.user = user;
		this.password = password;
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
	}

	public synchronized Connection startConnection() {
		try {
			if (isConnected()) {
				return connection;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, password);
			return connection;
		} catch (SQLException | ClassNotFoundException e) {
			System.err.println("Erro ao estabelecer a conexão ao MySQL: " + e.getMessage());
			return null;
		}
	}

	public void closeConnection() {
		try {
			if (isConnected()) {
				connection.close();
				System.out.println("[MySQL] Conexão fechada com sucesso.");
			}
		} catch (SQLException e) {
			System.err.println("Erro ao fechar a conexão ao MySQL: " + e.getMessage());
		}
	}

	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException ex) {
			return false;
		}
	}

	public void recallConnection() {
		if (!isConnected()) {
			startConnection();
		}
	}

	public Connection getConnection() {
		return isConnected() ? connection : startConnection();
	}
}