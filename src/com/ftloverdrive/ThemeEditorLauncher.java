package com.ftloverdrive;

import java.io.File;
import java.lang.reflect.Method;


public class ThemeEditorLauncher {

	public static void main(String[] args) {
		Utils.registerProtocolHandlers("com.ftloverdrive.protocols");

		try {
			// TODO: Just read overdrive prefs, without finding or prompting?
			File datsDir = Utils.findFTLDatsDir();

			DataManager.getInstance().init(datsDir);

			// Search for the The Editor's class and invoke its main().
			Class<?> c = Class.forName("de.matthiasmann.twlthemeeditor.Main");
			Method m = c.getMethod("main", String[].class);
			m.invoke(null, new Object[]{new String[0]});
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
