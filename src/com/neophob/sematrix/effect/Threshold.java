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

package com.neophob.sematrix.effect;

import com.neophob.sematrix.resize.Resize.ResizeName;


public class Threshold extends Effect {

	private short threshold;
	
	public Threshold() {
		super(EffectName.THRESHOLD, ResizeName.QUALITY_RESIZE);
		this.threshold = 128;
	}

	public int[] getBuffer(int[] buffer) {
		int[] ret = new int[buffer.length];
		
		short cr,cg,cb;
		int col;

		for (int i=0; i<buffer.length; i++){
			col = buffer[i];
    		cr=(short) ((col>>16)&255);
    		cg=(short) ((col>>8)&255);
    		cb=(short) ( col&255);
    		
    		if (cr<this.threshold) cr=0; else cr=255;
    		if (cg<this.threshold) cg=0; else cg=255;
    		if (cb<this.threshold) cb=0; else cb=255;
    		
    		ret[i]= (cr << 16) | (cg << 8) | cb;
		}
		return BoxFilter.applyBoxFilter(0, 1, ret, this.internalBufferXSize);
	}
	
	public void setThreshold(int threshold) {
		this.threshold = (short)threshold;
	}

	public short getThreshold() {
		return threshold;
	}
	
}
