package br.edu.ufrb.md.util;

import java.awt.Font;
import java.io.File;

public final class R {
		
	private R() {
	}
	
	public static final File FILE_OBJECTS = new File("data/Objects");
	public static final File FILE_INDEX = new File("data", "index.html");
	public static final Font FONT_UI = new Font("DialogInput", Font.PLAIN, 12);
	
	public static boolean mutable;

}
