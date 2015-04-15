package br.edu.ufrb.md.model;

import java.io.Serializable;


public class Article implements Serializable{
	
	private static final long serialVersionUID = 6326711992262152258L;
	private long id;
	private String title;
	private String latex;
	private String html;
	private String author;
	
	public Article() {
		title = "";
		latex = "";
		html = "";
		author = "";
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLatex() {
		return latex;
	}
	public void setLatex(String latex) {
		this.latex = latex;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	@Override
	public String toString() {
		return id+"-"+title;
	}
}
