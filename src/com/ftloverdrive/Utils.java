package com.ftloverdrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {
	// TODO Make it cross-platform
	public static File locateFTLPath() {
		File programFiles = new File(System.getenv("ProgramFiles"));
		// (isDirectory() on a non-existent file returns false)
		if (!programFiles.isDirectory()) return null;

		File ftl = new File(programFiles, "FTL");

		if (ftl.isDirectory()) return ftl;

		File steam = new File(programFiles, "Steam");
		if (steam.isDirectory()) {
			ftl = new File(steam, "steamapps/common/FTL Faster Than Light");
			if (ftl.isDirectory()) return ftl;
		}

		return null;
	}
	
	// Following methods are based on http://stackoverflow.com/a/3348150
	public static boolean copyFile(final File toCopy, final File destFile) {
		try {
			return Utils.copyStream(new FileInputStream(toCopy), new FileOutputStream(destFile));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean copyFilesRecusively(final File toCopy, final File destDir) {
		assert destDir.isDirectory();

		if (!toCopy.isDirectory())
			return Utils.copyFile(toCopy, new File(destDir, toCopy.getName()));
		else {
			final File newDestDir = new File(destDir, toCopy.getName());
			if (!ensureDirectoryExists(newDestDir)) return false;
			for (final File child : toCopy.listFiles()) {
				if (!Utils.copyFilesRecusively(child, newDestDir)) return false;
			}
		}
		return true;
	}

	public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection)
			throws IOException {
		final JarFile jarFile = jarConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(jarConnection.getEntryName())) {
				final String filename = removeStart(entry.getName(), jarConnection.getEntryName());

				final File f = new File(destDir, filename);
				if (!entry.isDirectory()) {
					final InputStream entryInputStream = jarFile.getInputStream(entry);
					if (!Utils.copyStream(entryInputStream, f)) return false;
					entryInputStream.close();
				} else {
					if (!Utils.ensureDirectoryExists(f))
						throw new IOException("Could not create directory: " + f.getAbsolutePath());
				}
			}
		}
		return true;
	}

	public static boolean copyResourcesRecursively(final URL originUrl, final File destination) {
		try {
			final URLConnection urlConnection = originUrl.openConnection();
			if (urlConnection instanceof JarURLConnection) {
				//Not the most efficient method, but who cares. We do it only once.
				String filename = originUrl.getFile().replaceAll("^.*/(?=.)", "");
				return Utils.copyJarResourcesRecursively(new File(destination, filename), (JarURLConnection) urlConnection);
			} else {
				return Utils.copyFilesRecusively(new File(originUrl.getPath()), destination);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyStream(final InputStream is, final File f) {
		try {
			return Utils.copyStream(is, new FileOutputStream(f));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyStream(final InputStream is, final OutputStream os) {
		try {
			final byte[] buf = new byte[1024];

			int len = 0;
			while ((len = is.read(buf)) > 0) {
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean ensureDirectoryExists(final File f) {
		return f.exists() || f.mkdirs();
	}

	public static String removeStart(String str, String remove) {
		if (Utils.isEmpty(str) || Utils.isEmpty(remove)) return str;
		if (str.startsWith(remove)) return str.substring(remove.length());
		return str;
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static void deleteFolder(File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				deleteFolder(f);
			} else {
				f.delete();
			}
		}
		dir.delete();
	}
}
