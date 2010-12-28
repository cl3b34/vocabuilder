/**
 * 
 */
package br.boirque.vocabuilder.controller;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

/**
 * Helps to access midlet's display 
 * 
 * @author javapisets
 * 
 */
public final class DisplayManager {

	private static Display display;
	private static Displayable current;

	public static void initialize(Display d) {
		DisplayManager.display = d;
		current = d.getCurrent();
	};

	public static void setCurrent(Displayable d) {

		current = d;
		if (current instanceof javax.microedition.lcdui.Alert
				&& display.getCurrent() instanceof javax.microedition.lcdui.Alert) {
			// Switch to a black canvas temporarily!
			display.setCurrent(new BlackCanvas());
		} else
			display.setCurrent(current);

	};
	
	public static void setCurrent(Alert a, Displayable d) {

		current = a;
		//TODO: have to be adopted for alert-alert transition
		display.setCurrent(a, d);

	};
	public static Displayable getCurrent() {

		if (current != null ) 
			return current;
		else
			return display.getCurrent();
		

	};

	private static class BlackCanvas extends Canvas {
		protected void paint(Graphics g) {
			// Paint canvas to black to reduce flicker.
			// Note: You may decide to change this to suite your needs.
			g.setColor(0x000000);
			g.fillRect(0, 0, getWidth(), getHeight());

			// Now show the alert that we really wanted!
			DisplayManager.setCurrent(DisplayManager.getCurrent());
		}
	}

}
