package com.nb.robot.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtils {
	// Saves uploadedInputStream content to the given file.
	public static UtilStatus writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			int read = 0;
			byte[] bytes = new byte[1024];
			File file = new File(uploadedFileLocation);
			File parentDir = file.getParentFile();
			if (parentDir!= null && !parentDir.exists() && !parentDir.mkdirs()) {
				return new UtilStatus(-1, "Failed to create direcory " +  parentDir.toString());
			}
			OutputStream out = new FileOutputStream(file);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			return new UtilStatus();
		} catch (IOException e) {
			e.printStackTrace();
			return new UtilStatus(-1, e.getMessage());
		}
	}
	
	// Returns symbolic link target of originalPath, or originalPath itself if it is not a symbolic link.
	public static String getSymbolicLinkTarget(String originalPath) {
		Path file = Paths.get(originalPath);
		if (!Files.isSymbolicLink(file)) {
			return originalPath;
		}
		try {
			Path target = Files.readSymbolicLink(file);
			return file.getParent().resolve(target).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return originalPath;
	}
}
