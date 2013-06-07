package com.ftloverdrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

import com.ftloverdrive.Archive;


public class DataManager {
	/** Singleton. */
	private static DataManager instance = new DataManager();

	private boolean ready = false;
	private HashMap<String,Archive> datMap = new HashMap<String,Archive>();

	public static DataManager getInstance() {
		return instance;
	}

	public void init(File datsDir) throws IOException {
		// TODO: Close everything and reset if called a second time.

		ready  = false;
		boolean meltdown = false;

		try {
			Archive dataDat = new Archive(new File(datsDir, "data.dat"));
			Archive resourceDat = new Archive(new File(datsDir, "resource.dat"));

			datMap.put("data.dat", dataDat);
			datMap.put("resource.dat", resourceDat);

			// TODO: Populate lookup tables and such with dat contents.

			ready = true;
		}
		catch (IOException e) {
			meltdown = true;
			throw e;
		}
		finally {
			if (meltdown) close();
		}
	}

	public void close() {
		// TODO: Close everything.
	}

	/**
	 * Retrieves the contents of a file inside an FTL dat.
	 *
	 * @param datName either "data.dat" or "resource.dat"
	 * @param innerPath the inner file's path
	 */
	public InputStream getDatInputStream(String datName, String innerPath) throws IOException {
		if (!ready) throw new RuntimeException("DataManager is not ready. Call init() first.");

		Archive tmpDat = datMap.get(datName);
		if (tmpDat != null) {
			return tmpDat.getInputStream(innerPath);
		} else {
			throw new FileNotFoundException("FTL dat ("+ datName +") is not available, for requested inner path ("+ innerPath +").");
		}
	}
}
