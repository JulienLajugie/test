/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.enums;


/**
 * A type of indel for vcf
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum TrackEventEnum {


	/**
	 * 
	 */
	SELECTED ("selected"),
	/**
	 * 
	 */
	DEFAULT_SIZE ("defaultSize"),
	/**
	 * 
	 */
	TRACK_RIGHT_CLICKED ("trackRightClicked"),
	/**
	 * 
	 */
	TRACK_DRAGGED_RELEASED ("trackDraggedReleased"),
	/**
	 * 
	 */
	RESIZE ("resize"),
	/**
	 * 
	 */
	TRACK_DRAGGED ("trackDragged");


	private final String name;


	/**
	 * Private constructor. Creates an instance of {@link TrackEventEnum}
	 * @param name
	 */
	private TrackEventEnum(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}