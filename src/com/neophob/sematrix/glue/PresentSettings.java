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

package com.neophob.sematrix.glue;

import java.util.ArrayList;
import java.util.List;

/**
 * simple class to store present set's
 * @author michu
 *
 */
public class PresentSettings {
	private List<String> present;

	public List<String> getPresent() {
		return present;
	}

	public void setPresent(List<String> present) {
		this.present = present;
	}
	
	public void setPresent(String[] present) {
		List<String> list=new ArrayList<String>();
		for (String s: present) {
			list.add(s);
		}
		this.present=list;
	}
	
	public String getSettingsAsString() {		
		if (present==null) {
			return "";
		}
		
		String ret="";
		
		for (String s: present) {
			ret+=s+";";
		}
		return ret;
	}
	
}
