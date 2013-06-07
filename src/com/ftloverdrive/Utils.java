package com.ftloverdrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class Utils {
	/**
	 * Registers a suite of protocol handlers for new URL objects.
	 *
	 * The VM-wide system property "java.protocol.handler.pkgs"
	 * is appended to include a custom package. The package's
	 * children must be packages named for various protocols,
	 * each of which must contain a "Handler" class that extends
	 * java.net.URLStreamHandler.
	 *
	 * A handler will be lazily instantiated and cached. It'll
	 * create a fresh URLConnection each new URL.
	 *
	 * Warning: Technically there's a race condition (get then set).
	 * This method could be synchronized, but that wouldn't
	 * protect the property from other methods. *shrug*
	 */
	public static void registerProtocolHandlers(String packageName) {
		String handlers = System.getProperty("java.protocol.handler.pkgs");
		if (handlers != null) {
			handlers += "|"+ packageName;
		} else {
			handlers = packageName;
		}
		System.setProperty("java.protocol.handler.pkgs", handlers);
	}

	private static boolean isDatsDirValid(File d) {
		return (d.exists() && d.isDirectory() && new File(d, "data.dat").exists());
	}

	public static File findFTLDatsDir() {
		String steamPath = "Steam/steamapps/common/FTL Faster Than Light/resources";
		String gogPath = "GOG.com/Faster Than Light/resources";

		String xdgDataHome = System.getenv("XDG_DATA_HOME");
		if (xdgDataHome == null)
			xdgDataHome = System.getProperty("user.home") +"/.local/share";

		File[] paths = new File[] {
			// Windows - Steam
			new File( new File(""+System.getenv("ProgramFiles(x86)")), steamPath ),
			new File( new File(""+System.getenv("ProgramFiles")), steamPath ),
			// Windows - GOG
			new File( new File(""+System.getenv("ProgramFiles(x86)")), gogPath ),
			new File( new File(""+System.getenv("ProgramFiles")), gogPath ),
			// Linux - Steam
			new File( xdgDataHome +"/Steam/SteamApps/common/FTL Faster Than Light/data/resources" ),
			// OSX - Steam
			new File( System.getProperty("user.home") +"/Library/Application Support/Steam/SteamApps/common/FTL Faster Than Light/FTL.app/Contents/Resources" ),
			// OSX
			new File( "/Applications/FTL.app/Contents/Resources" ),
			// TODO: Vhati's non-standard location. Remove this.
			new File( "E:/Games/FasterThanLight/resources" )
		};

		File datsDir = null;

		for ( File path: paths ) {
			if ( isDatsDirValid(path) ) {
				datsDir = path;
				break;
			}
		}

		return datsDir;
	}

/*
	// TODO: port this Swing dialog to TWL.
	// Call after finding fails or is overridden by the user.

	private static File promptForFTLDatsDir() {
		File datsDir = null;

		String message = "Overdrive uses images and data from FTL,\n";
		message += "but the path to FTL's resources could not be guessed.\n\n";
		message += "You will now be prompted to locate FTL manually.\n";
		message += "Select '(FTL dir)/resources/data.dat'.\n";
		message += "Or 'FTL.app', if you're on OSX.";
		JOptionPane.showMessageDialog(null,  message, "FTL Not Found", JOptionPane.INFORMATION_MESSAGE);

		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find data.dat or FTL.app");
		fc.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "FTL Data File - (FTL dir)/resources/data.dat";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals("data.dat") || f.getName().equals("FTL.app");
			}
		});
		fc.setMultiSelectionEnabled(false);

		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (f.getName().equals("data.dat"))
				datsDir = f.getParentFile();
			else if (f.getName().endsWith(".app") && f.isDirectory()) {
				File contentsPath = new File(f, "Contents");
				if(contentsPath.exists() && contentsPath.isDirectory() && new File(contentsPath, "Resources").exists())
					datsDir = new File(contentsPath, "Resources");
			}
			System.out.println( "User selected: " + datsDir.getAbsolutePath() );
		} else {
			System.out.println( "User cancelled FTL dats path selection." );
		}

		if (datsDir != null && isDatsDirValid(datsDir)) {
			return datsDir;
		}

		return null;
	}
*/

	public static File getUserDataDir() {
		//TODO: Return a different location for Android.
		//  (or other platform-specific user-owned paths).

		// Use the first entry in CLASSPATH assumed to be the main app path.
		try {
			return new File( Utils.class.getClassLoader().getResource("").toURI() );
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
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
				return Utils.copyFilesRecusively(new File(originUrl.toURI()), destination);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
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
			while ((len = is.read(buf)) >= 0) {
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
			return true;
		}
		catch (final IOException e) {
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
