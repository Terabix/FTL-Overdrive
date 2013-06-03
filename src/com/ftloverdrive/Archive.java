package com.ftloverdrive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Archive {
	private class ArchiveFile {
		public String filename;
		public int offset;
		public int size;

		@Override
		public String toString() {
			return filename + " (" + size + ")";
		}
	}

	private Map<String, ArchiveFile> files;

	private File file;;

	public Archive(File file) throws IOException {
		// Initialize
		this.file = file;
		files = new HashMap<String, ArchiveFile>();
		if (!file.exists()) return;

		// Open the file to read
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");

			// Read the header
			int filecnt = changeEndianness(raf.readInt());
			List<Integer> offsets = new ArrayList<Integer>();

			// Read the offsets
			for (int i = 0; i < filecnt; i++) {
				int offset = changeEndianness(raf.readInt());
				if (offset == 0) {
					break;
				}
				offsets.add(offset);
			}

			// Read the file entries
			for (int i = 0; i < filecnt; i++) {
				raf.seek(offsets.get(i));

				ArchiveFile f = new ArchiveFile();
				f.size = changeEndianness(raf.readInt());
				int pathsize = changeEndianness(raf.readInt());
				byte[] path = new byte[pathsize];
				raf.read(path);
				f.filename = new String(path);
				f.offset = offsets.get(i) + 8 + pathsize;
				files.put(f.filename, f);
			}
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}
	
	private int changeEndianness(int x) {
		return ((x >> 24) & 0xFF) | ((x >> 8) & 0xFF00) | ((x << 8) & 0xFF0000) | ((x << 24) & 0xFF000000);
	}

	public InputStream getInputStream(String filename) throws IOException {
		// Locate the file
		if (!files.containsKey(filename)) return null;
		ArchiveFile f = files.get(filename);

		// Copy into temporary buffer
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			raf.seek(f.offset);
			byte[] data = new byte[f.size];
			raf.read(data);

			// Return
			return new ByteArrayInputStream(data);
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	public boolean extractToFolder(final File destDir) throws IOException {
		int i = 0;
		for (ArchiveFile entry : files.values()) {
			File f = new File(destDir, entry.filename);


			if (!Utils.ensureDirectoryExists(f.getParentFile()))
				throw new IOException("Could not create directory: " + f.getParentFile().getAbsolutePath());
			
			InputStream entryInputStream = getInputStream(entry.filename);
			if (!Utils.copyStream(entryInputStream, f)) return false;
			entryInputStream.close();
			
			i++;
			if (i % 100 == 0) {
				System.out.println("Extracted " + i + " out of " + files.size() + " files.");
			}
		}
		return true;
	}
}
