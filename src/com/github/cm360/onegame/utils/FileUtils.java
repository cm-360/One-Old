package com.github.cm360.onegame.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static FileInputStream getResourceStream(String path) throws FileNotFoundException {
		File resourceFile = new File(String.format("resources/%s", path).replace('/', File.separatorChar));
		Logger.log("FILE_INFO", String.format("File requested: '%s'", resourceFile));
		return new FileInputStream(resourceFile);
	}
	
	public static String getStringContent(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = br.read()) != -1)
			builder.append((char) c);
		is.close();
		return builder.toString();
	}

}
