package de.museum.berleburg.datastorage.util;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MethodLibary {
	private MethodLibary() {
		
	}
	public static String getSQLFormattedDate(Timestamp date) {
		if(date == null) return "null";
		else return "'" + new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(date) + "'";
	}
	public static String getFileExtension(File file) {
		int pos = file.getName().lastIndexOf(".");
		if(pos < 0) return "";
		return file.getName().substring(pos, file.getName().length());
	}
}
