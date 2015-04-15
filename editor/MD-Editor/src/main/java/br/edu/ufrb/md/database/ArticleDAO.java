package br.edu.ufrb.md.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ufrb.md.model.Article;

public class ArticleDAO implements Closeable {

	// a conexão com o banco de dados
	private Connection connection;

	public static void main(String[] args) {
		try (MdArticleDAO dao = new MdArticleDAO();) {
			List<Article> list = dao.readArticle();
			for (Article article : list) {
				System.out.println(article.getId() + "-" + article.getTitle());
			}
			Article article = new Article();
			article.setId(1L);
			article.setHtml("<p>alterado por caique jhones em teste de db</p>");

			dao.update(article);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param type
	 *            use as variáveis da classe <code>ConnectionFactory</code>
	 */
	public ArticleDAO(int type) {
		connection = new ConnectionFactory().getConnection(type);
		try {
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS article "
					+ "(id INT PRIMARY KEY     NOT NULL,"
					+ " title        CHAR(150)    NOT NULL, "
					+ " latex        TEXT    NOT NULL, "
					+ " html         TEXT    NOT NULL, "
					+ " author       CHAR(50) NOT NULL, "
					+ " date         TEXT NOT NULL)";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insert(Article article) {
		String sql = "insert into article "
				+ "(id,title,latex,html,author,date)" + " values (?,?,?,?,?,?)";
		try (PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setLong(1, article.getId());
			statement.setString(2, article.getTitle());
			statement.setString(3, article.getLatex());
			statement.setString(4, article.getHtml());
			statement.setString(5, article.getAuthor());
			statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void update(Article article) {
		String sql = "update article set latex=?, html=?, author=?, date=? where id=?";
		try (PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setString(1, article.getLatex());
			statement.setString(2, article.getHtml());
			statement.setString(3, article.getAuthor());
			statement.setTimestamp(4,
					new Timestamp(System.currentTimeMillis()),
					Calendar.getInstance());
			statement.setLong(5, article.getId());

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void remove(Article article) {
		try (PreparedStatement stmt = connection
				.prepareStatement("delete from article where id=?");) {
			stmt.setLong(1, article.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Article> readArticle() {
		ArrayList<Article> list = new ArrayList<>();
		String sql = "select * from article";
		try(PreparedStatement statement = connection.prepareStatement(sql);) {
			ResultSet query = statement.executeQuery();
			while(query.next()){
				Article article = new Article();
				article.setId(query.getLong("id"));
				article.setTitle(query.getString("title"));
				article.setLatex(query.getString("latex"));
				article.setHtml(query.getString("html"));
				article.setAuthor(query.getString("author"));
				
				list.add(article);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean contains(long id) {
		String sql = "select id from article where id=?";
		try(PreparedStatement statement = connection.prepareStatement(sql);) {
			statement.setLong(1, id);
			ResultSet query = statement.executeQuery();
			return query.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
