/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2010  Andrzej JR Hunt
 *    
 *    simple rowLog is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    simple rowLog is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with simple rowLog.  If not, see <http://www.gnu.org/licenses/>.
 *.
 *
 *	Changelog:
 *  21/01/2011: Bugfix: on setProgress, we first check whether the splash screen
 *              is still visible, to prevent exceptions if it's been closed.
 *	24/01/2010:	Created
 */

package org.ahunt.simpleRowLog.launcher;

import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.grlea.log.SimpleLogger;

/**
 * Splash screen manager for simple rowLog. Takes care of the progress bar, and
 * may do more in the future.
 * 
 * @author Andrzej JR Hunt
 * @version 0.1
 * 
 */
public class SplashManager {

	private static final SimpleLogger log = new SimpleLogger(
			SplashManager.class);

	/**
	 * The top left x-coordinate for the progress bar.
	 */
	private static int topLeftX = 71;
	/**
	 * The top left y-coordinate for the progress bar.
	 */
	private static int topLeftY = 252;
	/**
	 * The width of the progress bar.
	 */
	private static int width = 290;
	/**
	 * The height of the progress bar,
	 */
	private static int height = 16;

	/**
	 * The splash screen, itself.
	 */
	final SplashScreen splash;

	/**
	 * Whether or not the splash screen is disabled.
	 */
	private boolean noSplash;

	/**
	 * The drawing area.
	 */
	private Graphics2D g;

	/**
	 * The progress bar image when on.
	 */
	private BufferedImage progressbarOn;

	/**
	 * The progress bar image when off.
	 */
	private BufferedImage progressbarOff;

	/**
	 * Start the Splash manager. This takes care of the splash screen display.
	 * 
	 * @param progress
	 *            The current progress in percent. Must be between 0 and 100
	 *            inclusive.
	 */
	public SplashManager(int progress) {
		log.entry("SplashManager(int");
		splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			log.debug("Splash screen is null.");
			noSplash = true;
			return;
		}
		g = splash.createGraphics();
		if (g == null) {
			log.debug("Couldn't create graphics.");
			noSplash = true;
			return;
		}
		try {
			progressbarOn = ImageIO.read(new File(
					"img/splash/splash_progressBarOn.png"));
			progressbarOff = ImageIO.read(new File(
					"img/splash/splash_progressBarOff.png"));
		} catch (Exception e) {
			log.debug("Progress images couldn't be loaded.");
			log.dbe(org.grlea.log.DebugLevel.L5_DEBUG, e);
			noSplash = true;
			// TODO: log
		}
		setProgress(progress);
	}

	/**
	 * Set the progress the progress bar is to display. Note that if the
	 * progress bar is already displaying a certain value then it cannot be
	 * decreased.
	 * 
	 * @param progress
	 *            The current progress in percent. Must be between 0 and 100
	 *            inclusive.
	 */
	public void setProgress(int progress) {
		if (noSplash || !splash.isVisible())
			return; // If disabled we do nothing.
		// Draw the on part (left)
		g.drawImage(progressbarOn, topLeftX, topLeftY, width * progress / 100,
				height, null);
		// Draw the off part (right)
		g.drawImage(progressbarOff, topLeftX + width * progress / 100,
				topLeftY, width - width * progress / 100, height, null);
		// Text
		g.drawString(progress + "%", topLeftX + width / 2 - 10, topLeftY
				+ height - 4);
		splash.update();
	}

}