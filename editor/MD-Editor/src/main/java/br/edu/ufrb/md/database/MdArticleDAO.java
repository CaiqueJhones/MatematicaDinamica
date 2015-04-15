package br.edu.ufrb.md.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ufrb.md.model.Article;

public class MdArticleDAO implements Closeable{
	
	// a conexão com o banco de dados
	private Connection connection;
	
	public MdArticleDAO() {
		connection = new ConnectionFactory().getConnection(ConnectionFactory.MYSQL);
	}
	
	public void update(Article article) {
		String sql = "update rwfyq_content set introtext=?, modified=? where id=?";
		try(PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, article.getHtml());
			statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()), Calendar.getInstance());
			statement.setLong(3, article.getId());
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ler os artigos no site md
	 * @return id e title do artigo.
	 */
	public List<Article> readArticle() {
		ArrayList<Article> list = new ArrayList<>();
		String sql = "select id, title from rwfyq_content";
		try(PreparedStatement statement = connection.prepareStatement(sql);) {
			ResultSet query = statement.executeQuery();
			while(query.next()){
				Article article = new Article();
				article.setId(query.getLong("id"));
				article.setTitle(query.getString("title"));
				
				list.add(article);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
