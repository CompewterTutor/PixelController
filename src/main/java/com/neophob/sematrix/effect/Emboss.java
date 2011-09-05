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


/**
 * The Class Emboss.
 */
public class Emboss extends Effect {

	/**
	 * Instantiates a new emboss.
	 *
	 * @param controller the controller
	 */
	public Emboss(PixelControllerEffect controller) {
		super(controller, EffectName.EMBOSS, ResizeName.QUALITY_RESIZE);
	}

	/* (non-Javadoc)
	 * @see com.neophob.sematrix.effect.Effect#getBuffer(int[])
	 */
	public int[] getBuffer(int[] buffer) {
//		return BoxFilter.applyBoxFilter(8, 1, buffer, this.internalBufferXSize);
		int []a = BoxFilter.applyBoxFilter(8, 1, buffer, this.internalBufferXSize);
		a = BoxFilter.applyBoxFilter(4, 1, a, this.internalBufferXSize);
		return BoxFilter.applyBoxFilter(5, 1, a, this.internalBufferXSize);
	}
	

}