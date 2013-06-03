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

public class OverDrive extends Widget {
	private static final int TICKS_PER_SECOND = 20;

	public static void main(String[] args) {
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
		} catch (Exception e) {
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

	@Action
	public void loadTheme() throws IOException {
		loadTheme(false);
	}
	
	public void loadTheme(boolean forceExtractResource) throws IOException {
		File ftlDir = Utils.locateFTLPath();
		// Will steam reset the FTL folder every time you try to play vanilla FTL?
		// If it will, this should be moved somewhere else.
		File ftlOdDir = new File(ftlDir, "OverDrive");
		
		File resourceDatFile = new File(ftlDir, "resources/resource.dat");
		File resourceDatDir = new File(ftlOdDir, "resource.dat");
		
		boolean extractResource = forceExtractResource || !resourceDatDir.exists() || resourceDatDir.listFiles().length == 0;
		
		try {
			System.out.println("Reloading theme" + (extractResource ? " and resource.dat" : "") + "...");
			Utils.copyResourcesRecursively(OverDrive.class.getResource("/theme/"), ftlOdDir);
			File themeDir = new File(ftlOdDir, "theme");
			
			if (forceExtractResource) {
				Utils.deleteFolder(resourceDatDir);
			}
			if (extractResource) {
				resourceDatDir.mkdir();
				Archive resourceDat = new Archive(resourceDatFile);
				resourceDat.extractToFolder(resourceDatDir);
			}
			
			theme = ThemeManager.createThemeManager(new File(themeDir, "FTL-OD.xml").toURI().toURL(), renderer);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to load the theme!");
			return;
		}
		getGUI().applyTheme(theme);
		System.out.println("Reloaded theme" + (extractResource ? " and resource.dat" : "") + "!");
	}
}