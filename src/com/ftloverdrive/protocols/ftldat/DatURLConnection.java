package com.ftloverdrive.protocols.ftldat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.ftloverdrive.DataManager;


/**
 * A URL Connection to a path inside an FTL dat.
 *
 * Example: InputStream is = new URL("...").openStream();
 *
 * ftldat://data.dat/data/names.xml
 * ftldat://resource.dat/img/generalUI/general_close.png
 *
 * Where the 'hostname' is one of the managed dat files.
 */
public class DatURLConnection extends URLConnection {

	public DatURLConnection(URL url) {
		super(url);
	}

	@Override
	public void connect() throws IOException {
	}


	@Override
	public Object getContent(Class[] classes) throws IOException {
		// TWL's de.matthiasmann.twl.utilsXMLParser makes a call
		// in case the connection returns an XmlPullParser.

		// Just return null to opt-out without UnknownServiceException.
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		String datName = this.getURL().getHost();
		String innerPath = this.getURL().getPath().replaceAll("^/", "");
		return DataManager.getInstance().getDatInputStream(datName, innerPath);
	}
}
