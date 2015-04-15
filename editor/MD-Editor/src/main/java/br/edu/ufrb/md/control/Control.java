package br.edu.ufrb.md.control;

import static br.edu.ufrb.md.util.ToolsHelp.compile;
import static br.edu.ufrb.md.util.ToolsHelp.getIcon;
import static br.edu.ufrb.md.util.ToolsHelp.openWithDesktop;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import br.edu.ufrb.md.database.ArticleDAO;
import br.edu.ufrb.md.database.ConnectionFactory;
import br.edu.ufrb.md.database.MdArticleDAO;
import br.edu.ufrb.md.model.Article;
import br.edu.ufrb.md.util.R;
import br.edu.ufrb.md.view.AboutDialog;
import br.edu.ufrb.md.view.ListArticles;
import br.edu.ufrb.md.view.LoadArticlesDialog;
import br.edu.ufrb.md.view.RootFrame;
import cj.utilities.EditFileFilter;

public interface Control {
	
	int DB_LOCAL = 1;
	int DB_REMOTE = 2;
	
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
	
	@SuppressWarnings("serial")
	class OpenMdDatabase extends AbstractAction {
		
		public OpenMdDatabase(String icon) {
			if(icon.contains("min"))
				putValue(NAME, "Abrir artigo remoto");
			putValue(SMALL_ICON, getIcon(icon));
			putValue(SHORT_DESCRIPTION, "Abrir artigo do site.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new Task(new LoadArticlesDialog("Lendo artigos...")).execute();
		}
		
		class Task extends SwingWorker<List<Article>, Void> {
			JDialog dialog;
			
			public Task(JDialog dialog) {
				this.dialog = dialog;
				dialog.setVisible(true);
			}

			@Override
			protected List<Article> doInBackground() throws Exception {
				try(MdArticleDAO dao = new MdArticleDAO();) {
					List<Article> list = dao.readArticle();
					return list;
				} catch (Exception e1) {
					Console.err.println(e1.getMessage());
					Console.err.println("Não foi possível se conectar!");
					e1.printStackTrace();
				}
				return null;
			}

			@Override
			protected void done() {
				dialog.dispose();
				try {
					ListArticles listArticles = new ListArticles(get());
					if(listArticles.start()){
						Console.out.println(listArticles.getArticle().getTitle());
						
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}				
			}
			
			
		}
		
	}
	
	@SuppressWarnings("serial")
	class SaveInDatabase extends AbstractAction {
		
		private int local;
		private RootFrame rootFrame;
		
		public SaveInDatabase(RootFrame frame, int local, String icon) {
			this.local = local;
			this.rootFrame = frame;
			putValue(SMALL_ICON, getIcon(icon));
			switch (local) {
			case DB_LOCAL:
				if(icon.contains("min"))
					putValue(NAME, "Salvar localmente");
				putValue(SHORT_DESCRIPTION, "Salvar artigo em um banco de dados local.");
				break;
			case DB_REMOTE:
				if(icon.contains("min"))
					putValue(NAME, "Salvar no site");
				putValue(SHORT_DESCRIPTION, "Salvar artigo diretamente no site.");
				break;
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(Execute.getInstance().getArticle() == null)
				return;
			StringBuilder builder = new StringBuilder();
			Article article = rootFrame.getArticle();
			builder.append(article.getTitle()).append("?");
			int op = JOptionPane.showConfirmDialog(rootFrame, builder, "Deseja salvar?", 
					JOptionPane.YES_NO_OPTION);
			if(op == JOptionPane.CANCEL_OPTION)
				return;
			switch (local) {
			case DB_LOCAL:
				try(ArticleDAO dao = new ArticleDAO(ConnectionFactory.SQLITE);) {
					save(dao);
				} catch (Exception e1) {
					Console.err.println(e1.getMessage());
					Console.err.println("Não foi possível se conectar!");
					e1.printStackTrace();
				}
				break;
			case DB_REMOTE:
				new Task(new LoadArticlesDialog("Salvando artigo...")).execute();
				break;
			}
		}
		
		public void save(ArticleDAO dao) {
			Article article = rootFrame.getArticle();
			if(article == null || article.getId() == 0)
				return ;
			if(dao.contains(article.getId()))
				dao.update(article);
			else
				dao.insert(article);
			
			Execute.getInstance().setFileDatabase(article.getTitle());
			Console.out.println("Salvo!!!");
		}
		
		class Task extends SwingWorker<Void, Void> {
			JDialog dialog;
			
			public Task(JDialog dialog) {
				this.dialog = dialog;
				dialog.setVisible(true);
			}

			@Override
			protected Void doInBackground() throws Exception {
				try(ArticleDAO dao = new ArticleDAO(ConnectionFactory.MYSQL);) {
					save(dao);
				} catch (Exception e1) {
					Console.err.println(e1.getMessage());
					Console.err.println("Não foi possível se conectar!");
					e1.printStackTrace();
				}
				return null;
			}

			@Override
			protected void done() {
				dialog.dispose();		
			}
			
			
		}
	
	}
	
	@SuppressWarnings("serial")
	class SaveFile extends AbstractAction {
		
		private RSyntaxTextArea textArea;
	
		public SaveFile(RSyntaxTextArea textArea, String icon) {
			super();
			if(icon.contains("min"))
				putValue(NAME, "Salvar localmente");
			putValue(SHORT_DESCRIPTION, "Salvar em um arquivo local.");
			putValue(SMALL_ICON, getIcon(icon));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK));
			this.textArea = textArea;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			File file = Execute.getInstance().getFileOpened();
			if(file == null)
				openFrame();
			else
				save(file);
		}
		
		private void openFrame() {
			String style = textArea.getSyntaxEditingStyle();
			String[] desc = new String[2];
			if(style.equals(SyntaxConstants.SYNTAX_STYLE_LATEX)){
				desc[0] = "Arquivo latex";
				desc[1] = "tex";
			}else if(style.equals(SyntaxConstants.SYNTAX_STYLE_CSS)){
				desc[0] = "Arquivo de cascata";
				desc[1] = "css";
			}else if(style.equals(SyntaxConstants.SYNTAX_STYLE_PHP)){
				desc[0] = "Arquivo php";
				desc[1] = "php";
			}if(style.equals(SyntaxConstants.SYNTAX_STYLE_HTML)){
				desc[0] = "Arquivo html";
				desc[1] = "html";
			}
			EditFileFilter filter = new EditFileFilter(desc[0], desc[1]);
			JFrame frame = new JFrame();
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(filter);
			if(chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
				return;
			save(chooser.getSelectedFile());
		}
		
		private void save(File file) {
			try(FileOutputStream out = new FileOutputStream(file);
					PrintStream print = new PrintStream(out)) {
				print.print(textArea.getText());
				print.flush();
				Execute.getInstance().setFileOpened(file);
				Console.out.println("Arquivo Salvo!");
			} catch (IOException e) {
				Console.out.println("Não foi possível salvar!");
				e.printStackTrace();
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	class NewArticle extends AbstractAction {
		
		public NewArticle() {
			putValue(NAME, "Novo artigo");
			putValue(SHORT_DESCRIPTION, "Novo artigo (localmente).");
			putValue(SMALL_ICON, getIcon("article.png"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Execute execute = Execute.getInstance();
			String title = JOptionPane.showInputDialog(execute.getFrame(), "Título");
			if(title != null) {
				Article article = new Article();
				article.setId(System.currentTimeMillis());
				article.setTitle(title);
				article.setAuthor("Caique Jhones");
				
				try(ArticleDAO dao = new ArticleDAO(ConnectionFactory.SQLITE)) {
					dao.insert(article);
					execute.setArticle(article);
				} catch (IOException e) {
					Console.err.println("Erro ao criar artigo!");
					e.printStackTrace();
				}
			}
		}
		
	}
}
