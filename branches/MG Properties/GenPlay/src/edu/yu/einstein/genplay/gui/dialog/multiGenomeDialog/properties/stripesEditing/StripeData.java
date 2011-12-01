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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.stripesEditing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripeData {

	/** Index used for Genome column */
	public static final int GENOME_INDEX 	= 0;
	/** Index used for variant column */
	public static final int VARIANT_INDEX 	= 1;
	/** Index used for track column */
	public static final int TRACK_INDEX 	= 2;


	private String 				genome;			// name of the genome
	private List<VariantType> 	variantList;	// list of variation
	private List<Color> 		colorList;		// list of color
	private Track<?>[] 			trackList;		// list of track


	/**
	 * Constructor of {@link StripeData}
	 */
	protected StripeData() {
		this.genome = null;
		this.variantList = null;
		this.colorList = null;
		this.trackList = null;
	}


	/**
	 * Constructor of {@link StripeData}
	 * @param genome		name of the genome
	 * @param variantList	list of variation
	 * @param colorList		list of color
	 * @param trackList		list of track
	 */
	protected StripeData(String genome, List<VariantType> variantList,
			List<Color> colorList, Track<?>[] trackList) {
		this.genome = genome;
		this.variantList = variantList;
		this.colorList = colorList;
		this.trackList = trackList;
	}


	//////////////////// Setters
	/**
	 * @param genome the genome to set
	 */
	protected void setGenome(String genome) {
		this.genome = genome;
	}

	/**
	 * @param variantList the variantList to set
	 */
	protected void setVariantList(List<VariantType> variantList) {
		this.variantList = variantList;
	}

	/**
	 * @param colorList the colorList to set
	 */
	protected void setColorList(List<Color> colorList) {
		this.colorList = colorList;
	}

	/**
	 * @param trackList the trackList to set
	 */
	protected void setTrackList(Track<?>[] trackList) {
		this.trackList = trackList;
	}


	//////////////////// Getters
	/**
	 * @return the genome
	 */
	protected String getGenome() {
		return genome;
	}

	/**
	 * @return the variantList
	 */
	protected List<VariantType> getVariantList() {
		return variantList;
	}

	/**
	 * @return the colorList
	 */
	protected List<Color> getColorList() {
		return colorList;
	}

	/**
	 * @return the trackList
	 */
	protected Track<?>[] getTrackList() {
		return trackList;
	}


	//////////////////// Getters for display
	/**
	 * @return the genome
	 */
	protected String getGenomeForDisplay() {
		return genome;
	}

	/**
	 * @return the variantList
	 */
	protected JPanel getVariantListForDisplay() {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		panel.setLayout(layout);
		for (int i = 0; i < variantList.size(); i++) {
			JLabel label = new JLabel(variantList.get(i).toString());
			label.setForeground(colorList.get(i));
			panel.add(label);
			if (i < (variantList.size() - 1)) {
				panel.add(new JLabel(", "));
			}
		}
		return panel;
	}

	/**
	 * @return the trackList
	 */
	protected String getTrackListForDisplay() {
		String text = "";
		for (int i = 0; i < trackList.length; i++) {
			text += trackList[i];
			if (i < (trackList.length - 1)) {
				text += ", ";
			}
		}
		return text;
	}

}