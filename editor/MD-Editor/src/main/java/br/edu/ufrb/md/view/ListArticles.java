package br.edu.ufrb.md.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import br.edu.ufrb.md.model.Article;
import cj.swing.implementation.SCellRenderer;

@SuppressWarnings("serial")
public class ListArticles extends JDialog{
	
	private JList<Article> jlist;
	private DefaultListModel<Article> model;
	private boolean accept;
	private Article article;
	
	public ListArticles(List<Article> list) {
		setTitle("Artigos");
		setSize(350, 350);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);
		
		JPanel panelBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panelBottom, BorderLayout.SOUTH);
		
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accept = false;
				dispose();
			}
		});
		panelBottom.add(btnCancelar);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				article = jlist.getSelectedValue();
				accept = true;
				dispose();
			}
		});
		panelBottom.add(btnOk);
		getRootPane().setDefaultButton(btnOk);
		
		model = new DefaultListModel<>();
		for (Article article : list) {
			model.addElement(article);
		}
		
		jlist = new JList<>(model);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setFont(new Font("Consolas", Font.PLAIN, 14));
		jlist.setCellRenderer(new SCellRenderer(Color.white, new Color(232, 232, 232), Color.black));
		jlist.setSelectedIndex(0);
		
		getContentPane().add(new JScrollPane(jlist), BorderLayout.CENTER);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	
	public boolean start() {
		setVisible(true);
		return accept;
	}

	public Article getArticle() {
		return article;
	}

}
