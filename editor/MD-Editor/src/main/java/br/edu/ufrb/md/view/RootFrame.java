package br.edu.ufrb.md.view;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ui.SizeGripIcon;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import br.edu.ufrb.md.control.Console;
import br.edu.ufrb.md.control.Control;
import br.edu.ufrb.md.model.Project;
import br.edu.ufrb.md.util.R;
import br.edu.ufrb.md.util.ToolsHelp;
import cj.utilities.ObjectManager;

@SuppressWarnings("serial")
public class RootFrame extends JFrame implements Control, SyntaxConstants, SearchListener{
	
	private RSyntaxTextArea textArea;
	private JTabbedPane tabbedPane;
	
	private String compiled = "";
	
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private StatusBar statusBar;
	
	private Project project;
	
	public RootFrame() {
		load();
		setIconImage(ToolsHelp.getIcon("favicon.png").getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("MD Editor");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setExtendedState(project.getStateWindow());
		FrameListener frame = new FrameListener();
		addWindowListener(frame);
		addWindowStateListener(frame);
		
		initSearchDialogs();
			
		JPanel contentPane = new JPanel(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		
		textArea = createTextArea();
		RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
		scrollPane.setIconRowHeaderEnabled(true);
		scrollPane.getGutter().setBookmarkingEnabled(true);
		
		tabbedPane.addTab("Editor", scrollPane);
		
		contentPane.add(tabbedPane);
		
		JTabbedPane tabbedPaneConsole = new JTabbedPane(SwingConstants.TOP);
		Console console = Console.getInstance();
		console.setSize(300, 100);
		console.setPreferredSize(console.getSize());
		tabbedPaneConsole.addTab("Console", console);
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(tabbedPaneConsole, BorderLayout.CENTER);
		statusBar = new StatusBar();
		temp.add(statusBar, BorderLayout.SOUTH);
		
		contentPane.add(temp, BorderLayout.SOUTH);
		contentPane.add(toolBar(), BorderLayout.NORTH);
						
		setContentPane(contentPane);
		setJMenuBar(createMenu());
		
		pack();
	}
	
	private void load() {
		if(R.FILE_PROJECT.exists()) {
			try {
				project = (Project) ObjectManager.openSerial(R.FILE_PROJECT);
			} catch (ClassNotFoundException | IOException e) {
				Console.err.println(e.getMessage());
				e.printStackTrace();
			}
		}else {
			project = new Project();
		}
	}
	
	public void saveProject() {
		try {
			ObjectManager.saveSerial(R.FILE_PROJECT, project);
		} catch (IOException e) {
			Console.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates our Find and Replace dialogs.
	 */
	public void initSearchDialogs() {

		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match case,
		// regex, etc.).
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

	}
	
	private RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(20, 90);
		LanguageSupportFactory.get().register(textArea);
		textArea.setSyntaxEditingStyle(SYNTAX_STYLE_LATEX);
		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(4);
		ToolTipManager.sharedInstance().registerComponent(textArea);
		return textArea;
	}
	
	private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}

	private JMenuBar createMenu() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.add(new OpenMdDatabase("open_db_min.png"));
		file.add(new JMenuItem(new ExitAction()));
		
		JMenu edit = new JMenu("Editar");
		edit.add(new JMenuItem(new UndoAction(textArea)));
		edit.add(new JMenuItem(new RedoAction(textArea)));
		edit.addSeparator();
		edit.add(new JMenuItem(new ShowFindDialogAction(findDialog, replaceDialog)));
		edit.add(new JMenuItem(new ShowReplaceDialogAction(findDialog, replaceDialog)));
				
		JMenu lang = new JMenu("Linguagem");
		ButtonGroup bg = new ButtonGroup();
		addItem(new ChangeLanguage(textArea, SYNTAX_STYLE_LATEX, "Latex"), bg, lang);
		addItem(new ChangeLanguage(textArea, SYNTAX_STYLE_HTML, "HTML"), bg, lang);
		addItem(new ChangeLanguage(textArea, SYNTAX_STYLE_CSS, "CSS"), bg, lang);
		addItem(new ChangeLanguage(textArea, SYNTAX_STYLE_PHP, "PHP"), bg, lang);
		lang.getItem(0).setSelected(true);
		
		JMenu sobre = new JMenu("Ajuda");
		sobre.add(new JMenuItem(new AboutAction(this)));
						
		bar.add(file);
		bar.add(edit);
		bar.add(lang);
		bar.add(sobre);
		
		return bar;
	}
	
	private JToolBar toolBar() {
		JToolBar bar = new JToolBar();
		bar.add(new JButton(new OpenMdDatabase("open_db.png")));
		bar.addSeparator();
		bar.add(new JButton(new CompileAction(textArea, this)));
		bar.add(new JButton(new TesteAction(true)));
		return bar;
	}
	
	/**
	 * Focuses the text area.
	 */
	public void focusTextArea() {
		textArea.requestFocusInWindow();
	}

	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if(b) 
			focusTextArea();
	}

	public String getCompiled() {
		return compiled;
	}

	public void setCompiled(String compiled) {
		this.compiled = compiled;
	}
	
	@Override
	public void searchEvent(SearchEvent e) {
		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
			default: // Prevent FindBugs warning later
			case MARK_ALL:
				result = SearchEngine.markAll(textArea, context);
				break;
			case FIND:
				result = SearchEngine.find(textArea, context);
				if (!result.wasFound()) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE:
				result = SearchEngine.replace(textArea, context);
				if (!result.wasFound()) {
					UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				}
				break;
			case REPLACE_ALL:
				result = SearchEngine.replaceAll(textArea, context);
				JOptionPane.showMessageDialog(null, result.getCount() +
						" occurrences replaced.");
				break;
		}

		String text = null;
		if (result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		}
		else if (type==SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount()>0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			}
			else {
				text = "";
			}
		}
		else {
			text = "Text not found";
		}
		statusBar.setLabel(text);
	}

	@Override
	public String getSelectedText() {
		return textArea.getSelectedText();
	}
		
	private static class StatusBar extends JPanel {

		private JLabel label;

		public StatusBar() {
			label = new JLabel("Leitura");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
		}

		public void setLabel(String label) {
			this.label.setText(label);
		}

	}
	
	public Project getProject() {
		return project;
	}
	
	public void exit() {
		
	}

	private class FrameListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent we) {
			saveProject();
			exit();
		}

		@Override
		public void windowStateChanged(WindowEvent e) {
			getProject().setStateWindow(e.getNewState());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
				}catch(Exception e) {
					e.printStackTrace();
				}
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				new RootFrame().setVisible(true);
			}
		});
	}

}
