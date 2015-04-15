package br.edu.ufrb.md.view;

import java.awt.BorderLayout;

import javax.swing.JDialog;

@SuppressWarnings("serial")
public class LoadArticlesDialog extends JDialog {
	
	public LoadArticlesDialog() {
		setTitle("Carregando");
		setBounds(100, 100, 450, 150);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new PanelLoad("Lendo artigos"), BorderLayout.CENTER);
	}
		
}
