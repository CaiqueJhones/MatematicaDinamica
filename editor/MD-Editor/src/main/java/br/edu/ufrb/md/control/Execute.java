package br.edu.ufrb.md.control;

import java.io.File;

import br.edu.ufrb.md.model.Article;
import br.edu.ufrb.md.view.RootFrame;

public class Execute {
	
	private static Execute instance;
	private RootFrame frame;
	
	private Execute(RootFrame frame) {
		super();
		this.frame = frame;
		instance = this;
	}
	
	public static void setInstance(RootFrame frame) {
		instance = new Execute(frame);
	}

	public static Execute getInstance() {
		return instance;
	}
	
	public RootFrame getFrame() {
		return frame;
	}

	public void setFileOpened(File file) {
		frame.getProject().setFileOpened(file);
		frame.setTitle("MD Editor - "+file.getAbsolutePath());
	}
	
	public File getFileOpened() {
		return frame.getProject().getFileOpened();
	}
	
	public void setFileDatabase(String name) {
		frame.getProject().setFileOpened(null);
		frame.setTitle("MD Editor"+name);
	}
	
	public void setArticle(Article article) {
		frame.setArticle(article);
	}
	
	public Article getArticle() {
		return frame.getArticle();
	}
}
