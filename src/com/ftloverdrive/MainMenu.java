package com.ftloverdrive;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class MainMenu extends State {
	private static final int CONTINUE = 0;
	@SuppressWarnings("unused")
	private static final int START = 1;
	private static final int TUTORIAL = 2;
	@SuppressWarnings("unused")
	private static final int STATS = 3;
	private static final int OPTIONS = 4;
	private static final int CREDITS = 5;
	private static final int QUIT = 6;
	private Button[] buttons;
	private String[] buttonNames = { "continue", "start", "tutorial", "stats", "options", "credits", "quit" };

	public MainMenu() {
		buttons = new Button[7];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button();
			buttons[i].setTheme("button-" + buttonNames[i]);
			add(buttons[i]);
		}

		/*for (Button button : buttons) {
			final Button btn = button;
			btn.addCallback(new Runnable() {

				@Override
				public void run() {
					btn.setEnabled(!btn.isEnabled());
				}
			});
		}*/

		buttons[MainMenu.CONTINUE].setEnabled(false);

		// Since we have some buttons that are NYI, why not use them for some other useful debugging stuff for now? ;-)
		buttons[MainMenu.TUTORIAL].setText("Test Resizable Frames");
		buttons[MainMenu.TUTORIAL].addCallback(new Runnable() {
			@Override
			public void run() {
				ResizableFrame frame = new ResizableFrame();
				frame.setSize(400, 400);
				frame.setPosition(300, 200);
				frame.setTitle("ResizableFrame: \"Lorem Ipsum\"");
				frame.setTheme("/resizableframe");
				frame.setResizableAxis(ResizableAxis.BOTH);
				frame.setDraggable(true);
				
				HTMLTextAreaModel tam = new HTMLTextAreaModel("<span style='font-family: bold;'>Lorem ipsum</span> dolor sit amet<img src='door'></img>, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
				TextArea ta = new TextArea(tam);
				ta.setTheme("/textarea");
				frame.add(ta);
				ta.adjustSize();
				add(frame);
			}
		});

		buttons[MainMenu.OPTIONS].addCallback(new Runnable() {
			@Override
			public void run() {
				new Options(MainMenu.this).openPopupCentered();
			}
		});

		buttons[MainMenu.CREDITS].setText("Print FTL Path");
		buttons[MainMenu.CREDITS].addCallback(new Runnable() {
			@Override
			public void run() {
				System.out.println(Utils.locateFTLPath());
			}
		});

		buttons[MainMenu.QUIT].addCallback(new Runnable() {
			@Override
			public void run() {
				OverDrive.getInstance().quit();
			}
		});
	}

	@Override
	protected void layout() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].adjustSize();
			buttons[i].setPosition((int) (getInnerWidth() * 0.94) - buttons[i].getWidth(),
					(int) (getInnerHeight() * (0.38 + i * 0.08)));
		}
	}
}
