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

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Metaballs extends Generator {	

	private static final int NUM_BLOBS = 5;

	private int[] blobPx = { 10, 40, 36, 33, 44,32, 22 };
	private int[] blobPy = { 4, 60, 45, 21, 13, 41, 32 };

	// Movement vector for each blob
	private int[] blobDx = { 1, 1, 1, 1, 1, 1, 1 };
	private int[] blobDy = { 1, 1, 1, 1, 1, 1, 1  };
	private int[][] vy,vx; 

	private int a=1;

	public Metaballs() {
		super(GeneratorName.METABALLS, ResizeName.QUALITY_RESIZE);
		vy = new int[NUM_BLOBS][getInternalBufferYSize()];
		vx = new int[NUM_BLOBS][getInternalBufferXSize()];
	}

	@Override
	public void update() {
		float f;
		for (int i=1; i<NUM_BLOBS; ++i) {
			f = (float)Math.sin((i+1)*3+5*blobPx[i]);
			//f = (float)Math.sin((i+1)*3+5*blobPx[i]);
			f*=3f;
			if (f<0) f=0-f;
			f+=0.5f;			
			blobPx[i]+=blobDx[i]*f;

			f = (float)Math.cos(a%256+(i+3)*blobPy[i]);
//			f = (float)Math.cos(a%256+3*blobPy[i]);
			f*=3f;
			if (f<0) f=0-f;
			f+=0.5f;			
			blobPy[i]+=(int)(blobDy[i]*f);

			// bounce across screen
			if (blobPx[i] < 0) {
				blobDx[i] = 1;
			}
			if (blobPx[i] > internalBufferXSize) {
				blobDx[i] = -1;
			}
			if (blobPy[i] < 0) {
				blobDy[i] = 1;
			}
			if (blobPy[i] > internalBufferYSize) {
				blobDy[i]=-1;
			}

			for (int x = 0; x < internalBufferXSize; x++) {
				vx[i][x] = (blobPx[i]-x)*(blobPx[i]-x);
			}

			for (int y = 0; y < internalBufferYSize; y++) {
				vy[i][y] = (blobPy[i]-y)*(blobPy[i]-y); 
			}
		}

		a++;
		if (a>0xffff) a=1;

		for (int y = 0; y < internalBufferYSize; y++) {
			for (int x = 0; x < internalBufferXSize; x++) {
				int m = 1;
				for (int i = 1; i < NUM_BLOBS; i++) {
					// Increase this number to make your blobs bigger
					m += 60000/(vy[i][y] + vx[i][x]+1);
				}
				//pg.pixels[x+y*pg.width] = color(0, m+x, (x+m+y)/2);
				int g = m+x;
				int b = (x+m+y)/3;
				if (g>255) g=255;
				if (b>255) b=255;
				this.internalBuffer[y*internalBufferXSize+x] = (0 << 16) | (g << 8) | (b);
			}
		}

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
}
