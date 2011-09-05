/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob.sematrix.generator;

import java.util.Random;

import com.neophob.sematrix.resize.Resize.ResizeName;


/**
 * ripped from http://stachelig.de/
 * 		 
 * @author mvogt
 *
 */
public class PlasmaAdvanced extends Generator {

	/** The Constant TWO_PI. */
	private static final float TWO_PI = 6.283185307f;

	/** The Constant GRADIENTLEN. */
	private static final int GRADIENTLEN = 900;//1500;
	// use this factor to make things faster, esp. for high resolutions
	/** The Constant SPEEDUP. */
	private static final int SPEEDUP = 3;

	/** The Constant FADE_STEPS. */
	private static final int FADE_STEPS = 50;

	// swing/wave function parameters
	/** The Constant SWINGLEN. */
	private static final int SWINGLEN = GRADIENTLEN*3;
	
	/** The Constant SWINGMAX. */
	private static final int SWINGMAX = GRADIENTLEN / 2 - 1;

	//TODO make them configable
	/** The Constant MIN_FACTOR. */
	private static final int MIN_FACTOR = 4;
	
	/** The Constant MAX_FACTOR. */
	private static final int MAX_FACTOR = 10;

	
	/** The rf. */
	private int rf = 4;
	
	/** The gf. */
	private int gf = 2;
	
	/** The bf. */
	private int bf = 1;
	
	/** The rd. */
	private int rd = 0;
	
	/** The gd. */
	private int gd = GRADIENTLEN / gf;
	
	/** The bd. */
	private int bd = GRADIENTLEN / bf / 2;

	// gradient & swing curve arrays
	/** The fade color steps. */
	private int fadeColorSteps = 0;
	
	/** The color grad. */
	private int[] colorGrad  = new int[GRADIENTLEN];
	
	/** The color grad tmp. */
	private int[] colorGradTmp  = new int[GRADIENTLEN];

	/** The fade swing steps. */
	private int fadeSwingSteps = 0;
	
	/** The swing curve. */
	private int[] swingCurve = new int[SWINGLEN];
	
	/** The swing curve tmp. */
	private int[] swingCurveTmp = new int[SWINGLEN];

	/** The frame count. */
	private int frameCount;
	
	/** The random. */
	private Random random;

	/**
	 * Instantiates a new plasma advanced.
	 *
	 * @param controller the controller
	 */
	public PlasmaAdvanced(PixelControllerGenerator controller) {
		super(controller, GeneratorName.PLASMA_ADVANCED, ResizeName.QUALITY_RESIZE);
		frameCount=1;
		random = new Random();
		makeGradient();
		makeSwingCurve();		
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.generator.Generator#update()
	 */
	@Override
	public void update() {
		frameCount++;

		if (frameCount%55==3) {
			makeGradient();
		}
		if (fadeColorSteps>0) {
			fadeColorGradient();
		}

		if (frameCount%57==33) {
			makeSwingCurve();
		}
		if (fadeSwingSteps>0) {
			fadeSwingCurve();
		}
		
		int t = frameCount*SPEEDUP;
		int swingT = swing(t); // swingT/-Y/-YT variables are used for a little tuning ...

		for (int y = 0; y < this.internalBufferYSize; y++) {
			int swingY  = swing(y);
			int swingYT = swing(y + t);
			for (int x = 0; x < this.internalBufferXSize; x++) {
				// this is where the magic happens: map x, y, t around
				// the swing curves and lookup a color from the gradient
				// the "formula" was found by a lot of experimentation
				this.internalBuffer[y*internalBufferXSize+x] = gradient(
						swing(swing(x + swingT) + swingYT) +
						swing(swing(x + t     ) + swingY ));
			}
		}
	}

	// fill the given array with a nice swingin' curve
	// three cos waves are layered together for that
	// the wave "wraps" smoothly around, uh, if you know what i mean ;-)
	/**
	 * Make swing curve.
	 */
	void makeSwingCurve() {		
		int factor1=random.nextInt(MAX_FACTOR)+MIN_FACTOR;
		int factor2=random.nextInt(MAX_FACTOR)+MIN_FACTOR;
		int factor3=random.nextInt(MAX_FACTOR)+MIN_FACTOR;

		int halfmax = SWINGMAX/factor1;

		for( int i=0; i<SWINGLEN; i++ ) {
			float ni = i*TWO_PI/SWINGLEN; // ni goes [0..TWO_PI] -> one complete cos wave
			swingCurveTmp[i]=
				(int)( Math.cos( ni*factor1 ) * Math.cos( ni*factor2 ) * Math.cos( ni*factor3 ) * halfmax + halfmax );
		}
		fadeSwingSteps = FADE_STEPS;
	}


	// create a smooth, colorful gradient by cosinus curves in the RGB channels
	/**
	 * Make gradient.
	 */
	private void makeGradient() {
		int val = random.nextInt(12);
		switch (val) {
		case 0: rf = random.nextInt(4)+1;
		break;
		case 1: gf = random.nextInt(4)+1;
		break;
		case 2: bf = random.nextInt(4)+1;
		break;
		case 3: rd = random.nextInt(GRADIENTLEN);
		break;
		case 4: gd = random.nextInt(GRADIENTLEN);
		break;
		case 5: bd = random.nextInt(GRADIENTLEN);
		break;
		}
		//System.out.println("Gradient factors("+rf+","+gf+","+bf+"), displacement("+rd+","+gd+","+bd+")");

		// fill gradient array
		for (int i = 0; i < GRADIENTLEN; i++) {
			int r = cos256(GRADIENTLEN / rf, i + rd);
			int g = cos256(GRADIENTLEN / gf, i + gd);
			int b = cos256(GRADIENTLEN / bf, i + bd);
			colorGradTmp[i] = color(r, g, b);
			fadeColorSteps = FADE_STEPS;
		}
	}

	/**
	 * Gets the r.
	 *
	 * @param col the col
	 * @return the r
	 */
	private int getR(int col) {
		return (col>>16)&255;
	}
	
	/**
	 * Gets the g.
	 *
	 * @param col the col
	 * @return the g
	 */
	private int getG(int col) {
		return (col>>8)&255;
	}
	
	/**
	 * Gets the b.
	 *
	 * @param col the col
	 * @return the b
	 */
	private int getB(int col) {
		return (col&255);
	}

	//---------------------------
	/**
	 * Fade color gradient.
	 */
	private void fadeColorGradient() {
		fadeColorSteps--;

		if (fadeColorSteps==0) {
			//arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy(colorGradTmp, 0, colorGrad, 0, GRADIENTLEN);
			return;
		}

		for (int i = 0; i < GRADIENTLEN; i++) {
			int colorS = colorGradTmp[i]; //target
			int colorD = colorGrad[i];    //current value

			int r = getR(colorD)+( (getR(colorS) - getR(colorD)) / fadeColorSteps);
			int g = getG(colorD)+( (getG(colorS) - getG(colorD)) / fadeColorSteps);
			int b = getB(colorD)+( (getB(colorS) - getB(colorD)) / fadeColorSteps);

			colorGrad[i] = color(r, g, b);
			//		    colorGrad[i] = color(g, g, g);
		}

	}

	/**
	 * Fade swing curve.
	 */
	private void fadeSwingCurve() {
		fadeSwingSteps--;
		if (fadeSwingSteps==0) {
			//arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy(swingCurveTmp, 0, swingCurve, 0, SWINGLEN);


			return;
		}

		for (int i = 0; i < SWINGLEN; i++) {
			int x = swingCurve[i] + ( (swingCurveTmp[i]-swingCurve[i]) / fadeSwingSteps);
			swingCurve[i] = x;
		}

	}

	// helper: get cosinus sample normalized to 0..255
	/**
	 * Cos256.
	 *
	 * @param amplitude the amplitude
	 * @param x the x
	 * @return the int
	 */
	private int cos256(int amplitude, int x) {
		return (int) (Math.cos(x * TWO_PI / amplitude) * 127 + 127);
	}

	// helper: get a swing curve sample
	/**
	 * Swing.
	 *
	 * @param i the i
	 * @return the int
	 */
	private int swing(int i) {
		return swingCurve[i % SWINGLEN];
	}

	// helper: get a gradient sample
	/**
	 * Gradient.
	 *
	 * @param i the i
	 * @return the int
	 */
	private int gradient(int i) {
		return colorGrad[i % GRADIENTLEN];
	}

	/**
	 * Color.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 * @return the int
	 */
	public final int color(int x, int y, int z) {
		if (x > 255) {x = 255;} else if (x < 0) {x = 0;}
		if (y > 255) {y = 255;} else if (y < 0) {y = 0;}
		if (z > 255) {z = 255;} else if (z < 0) {z = 0;}

		return 0xff000000 | (x << 16) | (y << 8) | z;
	}

}