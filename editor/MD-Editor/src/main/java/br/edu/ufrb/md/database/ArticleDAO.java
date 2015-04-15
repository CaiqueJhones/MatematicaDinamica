package br.edu.ufrb.md.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import br.edu.ufrb.md.model.Article;

public class ArticleDAO {
	
	// a conexão com o banco de dados
	private Connection connection;

	public static void main(String[] args) {
		Connection connection = new ConnectionFactory().getConnection();
		System.out.println("Conectou");
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArticleDAO() {
		connection = new ConnectionFactory().getConnection();
		try {
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS article " +
	                   "(id INT PRIMARY KEY     NOT NULL," +
	                   " title        CHAR(150)    NOT NULL, " + 
	                   " latex        TEXT    NOT NULL, " + 
	                   " html         TEXT    NOT NULL, " + 
	                   " author       CHAR(50) NOT NULL, " + 
	                   " date         TEXT NOT NULL)"; 
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insert(Article article) {
		String sql = "insert into article " +
	            "(title,latex,html,author,date)" +
	            " values (?,?,?,?,?)";
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, article.getTitle());
			statement.setString(2, article.getLatex());
			statement.setString(3, article.getHtml());
			statement.setString(4, article.getAuthor());
			statement.setDate(5, article.getDate());
			
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
