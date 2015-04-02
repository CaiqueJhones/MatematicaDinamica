package br.edu.ufrb.md.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import br.edu.ufrb.md.control.Console;

public final class ToolsHelp {

	public static URL url(String directory, String fileName) {
		ClassLoader cl = ToolsHelp.class.getClassLoader();
		StringBuilder append = new StringBuilder("br/edu/ufrb/md/")
			.append(directory).append("/").append(fileName);
		return cl.getResource(append.toString());
	}
	
	public static InputStream readFile(String directory, String fileName) {
		ClassLoader cl = ToolsHelp.class.getClassLoader();
		StringBuilder append = new StringBuilder("br/edu/ufrb/md/")
			.append(directory).append("/").append(fileName);
		return cl.getResourceAsStream(append.toString());
	}
	
	public static InputStream readProperty(String fileName) {
		return readFile("properties", fileName+".properties");
	}
	
	public static OutputStream outputProperty(String fileName) throws FileNotFoundException {
		return new FileOutputStream(url("properties", fileName+".properties").getFile());
	}
	
	public static ImageIcon getIcon(String fileName) {
		return new ImageIcon(url("icons", fileName));
	}
	
	public static void openWithDesktop(File f) {
		try {
			if (!f.exists())
				return;
			Desktop d = Desktop.getDesktop();
			d.open(f);
		} catch (IOException e1) {
			Console.err.println(e1.getMessage());
		}
	}
	
	public static void compile(String html) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
					readFile("data", "index.html")));
				FileOutputStream out = new FileOutputStream(R.FILE_INDEX);) {
			
			String line = "";
			StringBuilder builder = new StringBuilder();

			while ((line = in.readLine()) != null) {
				builder.append(line).append("\n");
			}
			
			String temp = builder.toString().replace("JAVA", html);
			
			out.write(temp.getBytes("UTF-8"));
			out.flush();
		} catch (IOException e1) {
			Console.err.println(e1.getMessage());
			e1.printStackTrace();
		}
	}
}
