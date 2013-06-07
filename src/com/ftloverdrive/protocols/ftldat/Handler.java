package com.ftloverdrive.protocols.ftldat;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.ftloverdrive.protocols.ftldat.DatURLConnection;


/**
 * A central handler for FTL resources inside *.dat files.
 *
 * A new DatURLConnection is created for each URL request.
 */
public class Handler extends URLStreamHandler {

	/**
	 * A no-args constructor, for reflection.
	 */
	public Handler() {
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new DatURLConnection(url);
	}
}
