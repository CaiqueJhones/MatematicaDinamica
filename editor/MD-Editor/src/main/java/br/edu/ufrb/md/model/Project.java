package br.edu.ufrb.md.model;

import java.awt.Font;
import java.io.File;
import java.io.Serializable;

/**
 * Classe que representa um projeto.
 * @author Caique
 * @since 1.0
 * @version 1.0
 */
public class Project implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Font defaultFont;
	private int stateWindow;
	private long articleID;
	private File fileOpened;
	
	public Project() {
		defaultFont = new Font("Consolas", Font.PLAIN, 14);
	}

	public Font getDefaultFont() {
		return defaultFont;
	}

	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}

	public int getStateWindow() {
		return stateWindow;
	}

	public void setStateWindow(int stateWindow) {
		this.stateWindow = stateWindow;
	}

	public long getArticleID() {
		return articleID;
	}

	public void setArticleID(long articleID) {
		this.articleID = articleID;
	}

	public File getFileOpened() {
		return fileOpened;
	}

	public void setFileOpened(File fileOpened) {
		this.fileOpened = fileOpened;
	}

}
