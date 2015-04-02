package br.edu.ufrb.md.control;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private static Console instance;
	public static final Output err;
	public static final Output out;
	
	private static JTextPane textComponent;
	private static StyledDocument styledDocument;
	private static Style style;
	
	static {
		setInstance(new Console());
		err = new Err();
		out = new Out();
	}
	
	private Console() {
		textComponent = new JTextPane();
		styledDocument = textComponent.getStyledDocument();
		style = textComponent.addStyle("my style", null);
		setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane(textComponent);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		
		textComponent.setEditable(false);
	}
	
	public static Console getInstance() {
		return instance;
	}

	private static void setInstance(Console instance) {
		Console.instance = instance;
	}
	
	synchronized public void clear() {
		textComponent.setText("");
	}

	public static abstract class Output {
		
		public Output() {
		}
		
		synchronized public void print(Object text){
			StyleConstants.setForeground(style, color());
			try {
				styledDocument.insertString(styledDocument.getLength(), text.toString(), style);
				textComponent.setCaretPosition(styledDocument.getLength());
			} catch (BadLocationException e) {
			}
			textComponent.requestFocus();
		}
		
		public void println(Object text) {
			print(text.toString() + "\n");
		}
		
		public void println() {
			print("\n");
		}
		
		public void printf(String text, Object... args){
			print(String.format(text, args));
		}
				
		abstract Color color();
	}
	
	private static class Err extends Output {
		@Override
		public Color color() {
			return Color.RED;
		}
		
	}
	
	private static class Out extends Output {

		@Override
		public Color color() {
			return Color.BLACK;
		}
		
	}

}
