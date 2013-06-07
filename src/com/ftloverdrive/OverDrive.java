package com.ftloverdrive;

import java.io.File;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.ActionMap.Action;
import de.matthiasmann.twl.FPSCounter;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Timer;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

import com.ftloverdrive.DataManager;
import com.ftloverdrive.Utils;


public class OverDrive extends Widget {
	private static final int TICKS_PER_SECOND = 20;

	public static void main(String[] args) {
		Utils.registerProtocolHandlers("com.ftloverdrive.protocols");

		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.create();
			Display.setTitle("FTL: Overdrive");
			Display.setVSyncEnabled(true);

			final OverDrive overdrive = new OverDrive();
			overdrive.renderer = new LWJGLRenderer();
			overdrive.getOrCreateActionMap().addMapping(overdrive);
			GUI gui = new GUI(overdrive, overdrive.renderer);

			overdrive.loadTheme();
			
			Timer timer = gui.createTimer();
			timer.setContinuous(true);
			timer.setCallback(new Runnable() {
				public void run() {
					overdrive.tick();
				}
			});
			timer.setDelay(100 / TICKS_PER_SECOND);
			timer.start();

			while (!Display.isCloseRequested() && !overdrive.quit) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

				gui.update();
				Display.update();
				// this call will burn the time between vsyncs
				GL11.glGetError();
				// process new native messages since Display.update();
				Display.processMessages();
				// now update Mouse events
				Mouse.poll();
				// and Keyboard too
				Keyboard.poll();
			}

			gui.destroy();
			overdrive.theme.destroy();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Display.destroy();
	}

	private static OverDrive instance; 
	
	public static OverDrive getInstance() {
		return instance;
	}

	private boolean quit;
	private LWJGLRenderer renderer;
	private ThemeManager theme;

	private State currentState;
	private FPSCounter fpsCounter;

	public OverDrive() throws LWJGLException {
		if (instance != null) throw new RuntimeException("OverDrive already exists!");
		instance = this;
		
		renderer = new LWJGLRenderer();
		setCurrentState(new MainMenu());

		fpsCounter = new FPSCounter();
		add(fpsCounter);
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State newState) {
		if (currentState != null) {
			removeChild(currentState);
		}
		currentState = newState;
		add(currentState);
	}
	
	protected void tick() {
		currentState.tick();
	}

	@Override
	protected void layout() {
		currentState.setSize(getInnerWidth(), getInnerHeight());
		currentState.setPosition(0, 0);

		fpsCounter.adjustSize();
		// Top-right corner
		fpsCounter.setPosition(getInnerWidth() - fpsCounter.getWidth(), getInnerY());
	}

	@Action
	public void quit() {
		quit = true;
	}

	@Action
	public void toggleFps() {
		fpsCounter.setVisible(!fpsCounter.isVisible());
	}

	/**
	 * Loads the TWL theme and sets up FTL dat parsing.
	 *
	 * If {UserDataDir}/theme/ does not exist, the dir will be created
	 * by extracting a theme from within this jar.
	 */
	@Action
	public void loadTheme() throws IOException {
		File dataDir = Utils.getUserDataDir();

		try {
			System.out.println("Reloading theme...");

			// TODO: Load a minimal theme to find/prompt for datsDir.
			//   Read prefs if that path has already been set.
			File datsDir = Utils.findFTLDatsDir();
			DataManager.getInstance().init(datsDir);

			// TODO: Then load a fuller theme that relies on FTL resources.

			// If there are multiple "theme/" dirs in the classpath, getResource() gets confused.
			//java.util.Enumeration<java.net.URL> urls = OverDrive.class.getClassLoader().getResources("theme/");
			//while (urls.hasMoreElements()) System.out.println( urls.nextElement() );
			//
			// Several workarounds:
			//   - Make the names unique.
			//   - Enumerate all matching resources and do an instanceof check for JarURLConnection.
			//   - Get the URL of this class, and compare it's jar path against matches' URLs
			//     to ensure they're in the same archive.
			//   - Don't jar resources at all. When compiling and packaging, copy ./overdrive.jar,
			//     ./lib/ and ./resource/* (acting as a skel dir) into a staging directory.
			//
			//     The last is tidiest, and a matter of putting xcopy in the batch scripts, but I'm
			//     unfamiliar with how Eclipse or maven would be set up for that. -Vhati

			// TODO: For now, delete the non-jar theme dir to prevent conflict.
			File tmpThemeDir = new File(dataDir, "theme");

			if (!tmpThemeDir.exists()) {
				// The leading slash means this resource could be from anywhere in CLASSPATH.
				Utils.copyResourcesRecursively(OverDrive.class.getResource("/theme/"), dataDir);
			}

			theme = ThemeManager.createThemeManager(new File(tmpThemeDir, "FTL-OD.xml").toURI().toURL(), renderer);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to load the theme!");
			return;
		}
		getGUI().applyTheme(theme);
		System.out.println("Reloaded theme!");
	}
}
