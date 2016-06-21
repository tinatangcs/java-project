package edu.gwu.ood.frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class FrameUtil {

	
	public static Color COLOR_WHITE = new Color(null,255,255,255);
	public static Color COLOR_BLACK = new Color(null,0,0,0);
	public static Color COLOR_RED = new Color(null,255,0,0);
	public static Color COLOR_LIGHT_GRAY = new Color(null,185, 220, 249);
	
	public static Color COLOR_CAMERA_BG = new Color(null,32, 72, 103);
	
	public static Color[] INDICATOR_BGS = {
		new Color(null, 241, 69, 61),
		new Color(null, 44, 152, 240),
		new Color(null, 253, 151, 39),
		new Color(null, 140, 193, 82)
	};
	
	public static Color[] INDICATOR_FGS = {
		new Color(null, 244, 105, 98),
		new Color(null, 104, 182, 243),
		new Color(null, 253, 182, 87),
		new Color(null, 175, 212, 132)
	};
	
	public static Color[] INDICATOR_TITLECOLORS = {
		new Color(null, 248, 178, 176),
		new Color(null, 177, 220, 249),
		new Color(null, 254, 221, 180),
		new Color(null, 219, 239, 221)
	};
	
	
	public static Color[] RED_FLASHING = {
		new Color(null, 254, 22, 22),
		new Color(null, 243, 152, 93),
		new Color(null, 251, 247, 79),
		new Color(null, 246, 243, 190),
		new Color(null, 254, 129, 107)
	};
	
	
	
	public static Font FONT_VALUE = new Font(null, "Arial", 40, SWT.NONE);
	public static Font FONT_UNIT = new Font(null, "Arial", 11, SWT.NONE);
	public static Font FONT_NAME = new Font(null, "Arial", 10, SWT.NONE);
	public static Font FONT_TIME = new Font(null, "Arial", 10, SWT.NONE);
	
	public static String MSG_REMOVE_MODEL = "REMOVE_MODEL";
	public static String MSG_ADD_EXTRA_MODEL = "ADD_EXTRA_MODEL";
}
