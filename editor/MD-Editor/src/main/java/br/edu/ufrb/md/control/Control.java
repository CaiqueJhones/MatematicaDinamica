package br.edu.ufrb.md.control;

import static br.edu.ufrb.md.util.ToolsHelp.compile;
import static br.edu.ufrb.md.util.ToolsHelp.getIcon;
import static br.edu.ufrb.md.util.ToolsHelp.openWithDesktop;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import br.edu.ufrb.md.util.R;
import br.edu.ufrb.md.view.AboutDialog;
import br.edu.ufrb.md.view.RootFrame;

public interface Control {
	
	@SuppressWarnings("serial")
	public class ExitAction extends AbstractAction {
		
		public ExitAction() {
			putValue(NAME, "Sair");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
		
	}
	
	@SuppressWarnings("serial")
	public class UndoAction extends AbstractAction {
		
		RSyntaxTextArea textArea;
		
		public UndoAction(RSyntaxTextArea textArea) {
			super("Desfazer", getIcon("undo.png"));
			this.textArea = textArea;
			setEnabled(false);
			textArea.addCaretListener(new Observable(textArea, this));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.undoLastAction();
		}
				
	}
	
	@SuppressWarnings("serial")
	public class RedoAction extends AbstractAction {
		
		RSyntaxTextArea textArea;
		
		public RedoAction(RSyntaxTextArea textArea) {
			super("Refazer", getIcon("redo.png"));
			this.textArea = textArea;
			setEnabled(false);
			textArea.addCaretListener(new Observable(textArea, this));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.redoLastAction();
		}
				
	}
	
	@SuppressWarnings("serial")
	public class CompileAction extends AbstractAction {
		RSyntaxTextArea textArea;
		RootFrame frame;
				
		public CompileAction(RSyntaxTextArea textArea, RootFrame frame) {
			super("", getIcon("record.png"));
			this.textArea = textArea;
			this.frame = frame;
			putValue(SHORT_DESCRIPTION, "Compilar.");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9,
				Event.CTRL_MASK));
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Console.getInstance().clear();
						Console.out.println("---------Compilando---------");
						String parse = LatexParse.parse(textArea.getText());
						int ini = 0, fin = 0;
						Matcher matcher = Pattern.compile("\\<inicializacao\\>").matcher(parse);
						if(matcher.find())
							ini = matcher.end();
						matcher = Pattern.compile("\\<finalizacao\\>").matcher(parse);
						if(matcher.find())
							fin = matcher.start();
						//System.out.println(parse.substring(ini, fin));
						frame.setCompiled(parse.substring(ini, fin));
						compile(frame.getCompiled());
					} catch (Exception e2) {
						Console.err.println("Erro na compilação do projeto!");
					}
				}
				
			}).start();
		}
		
	}
	
	@SuppressWarnings("serial")
	public class TesteAction extends AbstractAction {
		
		public TesteAction(boolean button) {
			if(!button)
				putValue(NAME, "Abrir no navegador");
			putValue(LARGE_ICON_KEY, getIcon("play.png"));
			putValue(SHORT_DESCRIPTION, "Abrir no navegador");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F10,
					Event.CTRL_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			openWithDesktop(R.FILE_INDEX);
		}
		
	}
	
	@SuppressWarnings("serial")
	public class ChangeLanguage extends AbstractAction {
		
		RSyntaxTextArea textArea;
		String style;
		
		public ChangeLanguage(RSyntaxTextArea textArea, String style,
				String name) {
			this.textArea = textArea;
			this.style = style;
			putValue(NAME, name);
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.setSyntaxEditingStyle(style);
		}
		
	}
	
	@SuppressWarnings("serial")
	public class ShowFindDialogAction extends AbstractAction {
		
		FindDialog findDialog;
		ReplaceDialog replaceDialog;
		
		public ShowFindDialogAction(FindDialog findDialog,
				ReplaceDialog replaceDialog) {
			super("Pesquisar");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
			this.findDialog = findDialog;
			this.replaceDialog = replaceDialog;
		}

		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}


	@SuppressWarnings("serial")
	public class ShowReplaceDialogAction extends AbstractAction {
		
		FindDialog findDialog;
		ReplaceDialog replaceDialog;
		
		public ShowReplaceDialogAction(FindDialog findDialog,
				ReplaceDialog replaceDialog) {
			super("Substituir");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
			this.findDialog = findDialog;
			this.replaceDialog = replaceDialog;
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}
	
	/**
	 * Displays an "About" dialog.
	 */
	@SuppressWarnings("serial")
	static class AboutAction extends AbstractAction {

		private RootFrame demo;

		public AboutAction(RootFrame demo) {
			this.demo = demo;
			putValue(NAME, "Sobre");
		}

		public void actionPerformed(ActionEvent e) {
			AboutDialog ad = new AboutDialog((RootFrame)SwingUtilities.
					getWindowAncestor(demo));
			ad.setLocationRelativeTo(demo);
			ad.setVisible(true);
		}

	}
	
	class Observable implements CaretListener {
		
		private RSyntaxTextArea textArea;
		private AbstractAction action;

		

		public Observable(RSyntaxTextArea textArea, AbstractAction action) {
			this.textArea = textArea;
			this.action = action;
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			if(action instanceof UndoAction)
				action.setEnabled(textArea.canUndo());
			else
				action.setEnabled(textArea.canRedo());
		}
		
	}
	
	public class MyCloseActionHandler implements ActionListener {

	    private String tabName;
	    private JTabbedPane tabPane;

	    public MyCloseActionHandler(String tabName, JTabbedPane tabPane) {
	        this.tabName = tabName;
	        this.tabPane = tabPane;
	    }

	    public String getTabName() {
	        return tabName;
	    }

	    public void actionPerformed(ActionEvent evt) {

	        int index = tabPane.indexOfTab(getTabName());
	        if (index >= 0) {

	            tabPane.removeTabAt(index);
	            // It would probably be worthwhile getting the source
	            // casting it back to a JButton and removing
	            // the action handler reference ;)

	        }

	    }

	}   

}
