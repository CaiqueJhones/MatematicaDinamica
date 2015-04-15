package br.edu.ufrb.md.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import br.edu.ufrb.md.util.ToolsHelp;

@SuppressWarnings("serial")
public class PanelLoad extends JPanel{
	
	public PanelLoad(String info) {
		setLayout(new BorderLayout());
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		add(progressBar, BorderLayout.SOUTH);
		
		JLabel lblInfo = new JLabel(info);
		lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblInfo.setIcon(ToolsHelp.getIcon("info.png"));
		add(lblInfo, BorderLayout.CENTER);
	}

}
