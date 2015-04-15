package br.edu.ufrb.md.control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public final class LatexParse {

	private static Matcher matcher;

	private static String toHTML;

	private static HashMap<String, Integer> theoremCount;
	private static HashMap<String, String> theoremName;
	private static HashMap<String, String> theoremPointer;
	
	private static int labelCount;
	private static HashMap<String, Integer> label;
	private static int acertoAtual, erroAtual, emptyAtual;
	
	private static int quizAtual, altAtual;

	public static String parse(String input) {
		if(input == null || input.isEmpty())
			return "";
		quizAtual = altAtual = labelCount = 0;
		acertoAtual = erroAtual = emptyAtual = 0;
		theoremCount = new HashMap<>();
		theoremName = new HashMap<>();
		theoremPointer = new HashMap<>();
		label = new HashMap<>();
		toHTML = input;

		final long INIT = System.currentTimeMillis();

		Console.out.println("Convertendo comentários");
		// comentÃ¡rio
		toHTML = toHTML.replaceAll("\\\\%", "\u00A9");
		matcher = Pattern.compile("%(.*)").matcher(toHTML);
		toHTML = toHTML.replaceAll("\u00A9", "%");
		while (matcher.find()) {
			replaceComments(matcher.group(0), matcher.group(1));
		}

		// retira os asteriscos
		replaceAsterisco();

		Console.out.println("Convertendo novos comandos");
		// newcommand
		matcher = Pattern.compile(
				"\\\\newcommand\\{([\\w-\\\\]+)\\}[[0-9]*]*\\{(.*)\\}").matcher(
				toHTML);
		while (matcher.find()) {
			replaceNewCommand(matcher.group(0), matcher.group(1),
					matcher.group(2));
		}

		Console.out.println("Convertendo teorema");
		// newthoerem do tipo \newtheorem{nome}{texto}
		matcher = Pattern.compile("\\\\newtheorem\\{(\\w+)\\}\\{(.+)\\}")
				.matcher(toHTML);
		while (matcher.find()) {
			newTheorem(matcher.group(0), matcher.group(1), matcher.group(2),
					null);
		}

		// newthoerem do tipo \newtheorem{nome}[contador]{texto}
		matcher = Pattern.compile(
				"\\\\newtheorem\\{(\\w+)\\}\\[(\\w+)\\]\\{(.+)\\}").matcher(
				toHTML);
		while (matcher.find()) {
			newTheorem(matcher.group(0), matcher.group(1), matcher.group(3),
					matcher.group(2));
		}
		
		Console.out.println("Convertendo md comandos");
		// comaando do tipo \comando{nome}{texto}
		matcher = Pattern.compile("\\\\([a-z]+)\\{(\\w+)\\}\\{([\\w\\W\\s][^\\}]*)\\}")
				.matcher(toHTML);
		while (matcher.find()) {
			replaceMyCommands(matcher.group(0), matcher.group(1), matcher.group(2),
					matcher.group(3));
		}

		Console.out.println("Convertendo itens");
		// item
		matcher = Pattern.compile("\\\\([a-z]+)\\{*\\}*(.+)").matcher(toHTML);
		while (matcher.find()) {
			replaceItems(matcher.group(0), matcher.group(1), matcher.group(2));
		}

		Console.out.println("Convertendo comandos");
		// Comandos do tipo \comando{com}
		matcher = Pattern.compile("\\\\([a-z]+)\\{(.[^\\}]{0,})\\}").matcher(toHTML);
		while (matcher.find()) {
			replaceCommands(matcher.group(0), matcher.group(1),
					matcher.group(2));
		}

		// comandos do tipo \comando
		matcher = Pattern.compile("\\\\([a-z]+)").matcher(toHTML);
		while (matcher.find()) {
			replaceLine(matcher.group(0));
		}

		Console.out.println("Convertendo pré-ambulo");
		// preambulo
		matcher = Pattern.compile("\\\\([a-z]+)[[\\w-,]*]*\\{[a-z]*\\}*")
				.matcher(toHTML);
		while (matcher.find()) {
			replacePreAmbule(matcher.group(0), matcher.group(1));
		}

		Console.out.println("Convertendo equações..");
		// Comandos do tipo $$equaÃ§Ã£o$$
		matcher = Pattern.compile("\\$+\\$([\\w\\s\\W][^\\$]+)\\$?\\$")
				.matcher(toHTML);
		while (matcher.find()) {
			replaceEquation2(matcher.group(0), matcher.group(1));
		}

		// Comandos do tipo $equaÃ§Ã£o$
		matcher = Pattern.compile("\\$+([\\w\\W\\s][^\\$]*)\\$+").matcher(
				toHTML);
		while (matcher.find()) {
			replaceEquation(matcher.group(0), matcher.group(1));
		}
		
		toHTML = toHTML.replace("\\\\", "<br/>");
		toHTML = toHTML.replace("\n\n", "</p><p>");


		Console.out.printf("----------Compilado em %d segundos!---------------",
				(System.currentTimeMillis() - INIT) / 1000);
		return toHTML;//replace("\n", "").replace("\r", "");
	}
	
	private static void replaceMyCommands(String text, String key1, String key2, String key3) {
		if(key1.equals("mensagem")){
			if(key2.equals("acerto")){
				acertoAtual++;
				toHTML = toHTML.replaceFirst("\\\\mensagem\\{acerto\\}\\{([\\w\\W\\s][^\\}]*)\\}", 
						"<p id='acerto_form"+acertoAtual+"' style='display:none;' class='acerto-box'>"
						+key3+"</p>");
			}else if(key2.equals("empty")){
				emptyAtual++;
				toHTML = toHTML.replaceFirst("\\\\mensagem\\{empty\\}\\{([\\w\\W\\s][^\\}]*)\\}", 
						"<p id='empty_form"+emptyAtual+"' style='display:none;' class='erro-box'>"
						+key3+"</p>");
			}else if(key2.equals("erro")){
				erroAtual++;
				toHTML = toHTML.replaceFirst("\\\\mensagem\\{erro\\}\\{([\\w\\W\\s][^\\}]*)\\}", 
						"<p id='erro_form"+erroAtual+"' style='display:none;' class='erro-box'>"
						+key3+"</p>");
			}else if(key2.equals("info")){
				toHTML = toHTML.replace(text,"<p class='text-info'>"+key3+"</p>");
			}else if(key2.equals("warn")){
				toHTML = toHTML.replace(text,"<p class='text-warn'>"+key3+"</p>");
			}else if(key2.equals("error")){
				toHTML = toHTML.replace(text,"<p class='text-error'>"+key3+"</p>");
			}else {
				toHTML = toHTML.replace(text,"<p>"+key3+"</p>");
			}
			
		}else if(key1.equals("end")){
			if(key2.equals("quiz")){
				toHTML = toHTML.replace(text, "<p><input onclick=\"javascript:solution(this.form,'"
						+ key3+"');\""
						+ " type='BUTTON' value='RESULTADO' class='button-quiz'/></p></form></div></div></p>");
			}
		}else if(key1.equals("begin")){
			if(key2.equals("esconder")){
				toHTML = toHTML.replace(text, "<div id='"+key3+"' class='hid'>");
			}
		}
	}

	/*
	 * Comandos simples
	 */
	private static void replaceLine(String text) {
		if (text.equals("\\par")) {
			toHTML = toHTML.replace(text, "");
		} else if (text.equals("\\noindent")) {
			toHTML = toHTML.replace(text, "");
		} else if (text.equals("\\newline")) {
			toHTML = toHTML.replace(text, "<br>");
		} else if (text.equals("\\ldots")) {
			toHTML = toHTML.replace(text, "...");
		} else if (text.equals("\\maketitle")) {
			toHTML = toHTML.replace(text, "");
		} else if (text.equals("\\setlength")) {
			toHTML = toHTML.replace(text, "");
		} else if (text.equals("\\space")) {
			toHTML = toHTML.replace(text, " ");
		} else if (text.equals("\\backslash")) {
			toHTML = toHTML.replace(text, "\\");
		} else if (text.equals("\\endalternativa")) {
			toHTML = toHTML.replace(text, "</label>");
		}
	}

	private static void replaceCommands(String txt, String key1, String key2) {
		if (key1.equals("begin")) {
			if (key2.equals("document")) {
				toHTML = toHTML.replace(txt, "<inicializacao><p>");
			}
			
			else if (key2.equals("quiz")) {
				quizAtual++;
				toHTML = toHTML.replaceFirst("\\\\begin\\{quiz\\}",
						"<p><span class='list-quiz'>Pergunta"+quizAtual+"</span><div class='theorem'><div class='lab'><form name='form"+quizAtual+"'>");
			}

			else if (key2.equals("equation")) {
				toHTML = toHTML.replace(txt, "$$");
			}
			
			else if (key2.equals("esconder")) {
				toHTML = toHTML.replace(txt, "<div class='hid'>");
			}

			if (key2.equals("slider")) {
				toHTML = toHTML
						.replace(txt,
								"<div class='toogle_title'>Clique para exibir</div><div class='toogle_panel'>");
			}

			if (key2.equals("slider_app")) {
				toHTML = toHTML
						.replace(
								txt,
								"<div class='toogle_title'>Clique para exibir o grÃ¡fico</div><div class='toogle_panel'><iframe width='540' height='360' src='");
			}

			else if (key2.equals("enumerate")) {
				toHTML = toHTML.replace(txt, "<ol>");
			}

			else if (key2.equals("itemize")) {
				toHTML = toHTML.replace(txt, "<ul>");
			}

			else if (key2.equals("quote")) {
				toHTML = toHTML.replace(txt, "<cite>");
			}

			else if (key2.equals("flushleft")) {
				toHTML = toHTML.replace(txt, "<p style='text-align:left'>");
			}

			else if (key2.equals("flushright")) {
				toHTML = toHTML.replace(txt, "<p style='text-align:right'>");
			}

			else if (key2.equals("center")) {
				toHTML = toHTML.replace(txt, "<p style='text-align:center'>");
			}

			else if (key2.equals("comment")) {
				toHTML = toHTML.replace(txt, "<!-- ");
			}

			else if (key2.equals("description")) {
				toHTML = toHTML.replace(txt, "");
			}

			else if (theoremName.containsKey(key2)) {
				replaceTheorem(key2, txt);
			}

		}

		else if (key1.equals("end")) {

			if (key2.equals("document")) {
				toHTML = toHTML.replace(txt, "</p><finalizacao>");
			}
			
			else if (key2.equals("quiz")) {
				toHTML = toHTML.replace(txt, "<p><input onclick=\"javascript:solution(this.form);\""
						+ " type='BUTTON' value='RESULTADO' class='button-quiz'/></p></form></div></div></p>");
			}

			else if (key2.equals("equation")) {
				toHTML = toHTML.replace(txt, "$$");
			}
			
			else if (key2.equals("esconder")) {
				toHTML = toHTML.replace(txt, "</div");
			}

			if (key2.equals("slider")) {
				toHTML = toHTML.replace(txt, "</div>");
			}

			if (key2.equals("slider_app")) {
				toHTML = toHTML.replace(txt, "'></iframe></div>");
			}

			else if (key2.equals("enumerate")) {
				toHTML = toHTML.replace(txt, "</ol>");
			}

			else if (key2.equals("itemize")) {
				toHTML = toHTML.replace(txt, "</ul>");
			}

			else if (key2.equals("quote")) {
				toHTML = toHTML.replace(txt, "</cite>");
			}

			else if (key2.equals("flushleft")) {
				toHTML = toHTML.replace(txt, "</p");
			}

			else if (key2.equals("flushright")) {
				toHTML = toHTML.replace(txt, "</p>");
			}

			else if (key2.equals("center")) {
				toHTML = toHTML.replace(txt, "</p>");
			}

			else if (key2.equals("description")) {
				toHTML = toHTML.replace(txt, "");
			}

			else if (key2.equals("comment")) {
				toHTML = toHTML.replace(txt, " -->");
			}

			else if (theoremName.containsKey(key2)) {
				toHTML = toHTML.replace(txt, "</div>");
			}

		}
		
		else if(key1.equals("pergunta")) {
			toHTML = toHTML.replace(txt, "<p>" + key2 + "</p>");
		}
		
		else if(key1.equals("resposta")) {
			toHTML = toHTML.replace(txt,"<input TYPE='HIDDEN' NAME='resposta' VALUE='"+key2+"'>");
		}

		else if (key1.equals("title")) {
			toHTML = toHTML.replace(txt, "<h2>" + key2 + "</h2>");
		}

		else if (key1.equals("section") || key1.equals("chapter")) {
			toHTML = toHTML.replace(txt, "<h3>" + key2 + "</h3>");
		}

		else if (key1.equals("subsection")) {
			toHTML = toHTML.replace(txt, "<h4>" + key2 + "</h4>");
		}

		else if (key1.equals("subsubsection")) {
			toHTML = toHTML.replace(txt, "<h5>" + key2 + "</h5>");
		}

		else if (key1.equals("textbf")) {
			toHTML = toHTML.replace(txt, "<strong>" + key2 + "</strong>");
		}

		else if (key1.equals("emph")) {
			toHTML = toHTML.replace(txt, "<em>" + key2 + "</em>");
		}

		else if (key1.equals("underline")) {
			toHTML = toHTML.replace(txt, "<u>" + key2 + "</u>");
		}

		else if (key1.equals("fbox")) {
			toHTML = toHTML.replace(txt, "<table border='1'><tr><td>" + key2
					+ "</td></tr></table");
		}
		// comentado pois o MathJax jï¿½ faz isso
		else if(key1.equals("label")){
			labelCount++;
			label.put(key2, labelCount);
			toHTML = toHTML.replaceFirst("\\\\label\\{(.[^\\}]{0,})\\}", "<span id='"+key2+"'></span>");
		}

		else if(key1.equals("ref")){
			toHTML = toHTML.replace(txt, "<a href='#"+key2+"'>"+label.get(key2)+"</a>");
		}

		else if (key1.equals("author")) {
			toHTML = toHTML.replace(txt, "<p>" + key2 + "</p>");
		}

		else if (key1.equals("date")) {
			toHTML = toHTML.replace(txt, "<p>" + key2 + "</p>");
		}

		// Nï¿½o usados pelo html
		else if (key1.equals("documentclass")) {
			toHTML = toHTML.replace(txt, "");
		}

		else if (key1.equals("pagestyle")) {
			toHTML = toHTML.replace(txt, "");
		}

		else if (key1.equals("thispagestyle")) {
			toHTML = toHTML.replace(txt, "");
		}

		else if (key1.equals("noident")) {
			toHTML = toHTML.replace(txt, "");
		}

		else if (key1.equals("usepackage")) {
			toHTML = toHTML.replace(txt, "");
		}

		else if (key1.equals("parindent")) {
			toHTML = toHTML.replace(txt, "");
		}
		
		else if (key1.equals("alternativa")) {
			altAtual++;
			toHTML = toHTML.replaceFirst("\\\\alternativa\\{"+key2+"\\}", 
					"<input TYPE='RADIO' ID='alt"+altAtual+"' NAME=\"alternativa\" VALUE='"+
							key2+"'><label for='alt"+altAtual+"'>");
		}
		
	}

	private static void replaceItems(String txt, String key1, String key2) {
		if (key1.equals("item")) {
			toHTML = toHTML.replace(txt, "<li>" + key2 + "</li>");
		}
	}

	private static void replaceComments(String txt, String value) {
		toHTML = toHTML.replace(txt, "<!-- " + value + " -->");
	}

	private static void replacePreAmbule(String txt, String key) {
		if (key.equals("documentclass") || key.equals("usepackage")) {
			toHTML = toHTML.replace(txt, "");
		}
	}

	private static void replaceNewCommand(String txt, String name, String value) {
		toHTML = toHTML.replace(txt, "");
		toHTML = toHTML.replace(name, value);
	}

	private static void replaceEquation(String txt, String key) {
		toHTML = toHTML.replace(txt, "<img alt=\"" + key
				+ "\"src=\"data:image/png;base64," + generateImage(key, true)
				+ "\"/>");
	}

	private static void replaceEquation2(String txt, String key) {
		toHTML = toHTML.replace(txt, "<img alt=\"" + key
				+ "\"src=\"data:image/png;base64," + generateImage(key, false)
				+ "\"/>");
	}

	private static void newTheorem(String txt, String name, String value,
			String pointer) {
		if (pointer == null)
			theoremCount.put(name, 0);
		else
			theoremPointer.put(name, pointer);
		theoremName.put(name, value);
		toHTML = toHTML.replace(txt, "");
	}

	/*
	 * por enquanto sï¿½ do estilo definition
	 */
	private static void replaceTheorem(String name, String txt) {
		if (theoremCount.containsKey(name)) {
			int count = theoremCount.get(name);
			theoremCount.put(name, ++count);
			toHTML = toHTML.replaceFirst("\\\\begin\\{"+name+"\\}", "</br><span class='list-quiz'>"
					+ theoremName.get(name) + " " + theoremCount.get(name)
					+ "</span><div class='theorem'>");
		} else if (theoremPointer.containsKey(name)) {
			String p = theoremPointer.get(name);
			theoremCount.put(p, theoremCount.get(p) + 1);
			toHTML = toHTML.replaceFirst("\\\\begin\\{"+name+"\\}", "</br><span class='list-quiz'>"
					+ theoremName.get(p) + " " + theoremCount.get(name)
					+ "</span><div class='theorem'>");
		}
	}

	private static void replaceAsterisco() {
		toHTML = toHTML.replace("\\section*", "\\section");
		toHTML = toHTML.replace("\\subsection*", "\\subsection");
		toHTML = toHTML.replace("\\subsubsection*", "\\subsubsection");
		toHTML = toHTML.replace("\\chapter*", "\\chapter");
	}

	private static String generateImage(String latex, boolean isInline) {
		try {
			Console.out.println(latex);
			TeXFormula formula = new TeXFormula(latex);
			TeXIcon icon = null;

			if (isInline) {
				icon = formula.new TeXIconBuilder()
						.setStyle(TeXConstants.STYLE_DISPLAY).setSize(15)
						.build();
			} else {
				icon = formula.new TeXIconBuilder()
						.setStyle(TeXConstants.STYLE_DISPLAY).setSize(20)
						.build();
			}

			icon.setInsets(new Insets(0, 0, 0, 0));

			BufferedImage image = new BufferedImage(icon.getIconWidth(),
					icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			JLabel jl = new JLabel();
			jl.setForeground(new Color(0, 0, 0));
			icon.paintIcon(jl, g2, 0, 0);

			g2.dispose();

			ByteArrayOutputStream bin = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bin);

			String encode = Base64.encode(bin.toByteArray());
			return encode;
		} catch (Exception e) {
			Console.err.println("Erro ao converter: " + latex);
			// Log.warningError(e);
		}
		return "";
	}

}
