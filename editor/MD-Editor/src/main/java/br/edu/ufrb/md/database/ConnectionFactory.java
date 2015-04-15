package br.edu.ufrb.md.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import br.edu.ufrb.md.util.ToolsHelp;

public class ConnectionFactory {

	public static final int MYSQL = 1;
	public static final int SQLITE = 2;

	public Connection getConnection(int type) {
		try {
			if (type == 1) {
				Properties properties = new Properties();
				properties.load(ToolsHelp.readProperty("conf_db"));
				String url = properties.getProperty("url");
				String user = properties.getProperty("user");
				String pass = properties.getProperty("pass");
				return DriverManager.getConnection(
						"jdbc:mysql://"+url, user, pass);
			} else {
				Class.forName("org.sqlite.JDBC");
				return DriverManager
						.getConnection("jdbc:sqlite:data/database/articles.db");
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
