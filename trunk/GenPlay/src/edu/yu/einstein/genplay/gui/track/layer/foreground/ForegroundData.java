/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer.foreground;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * This class gather the data displayed in a track foreground layer
 * @author Julien Lajugie
 */
public class ForegroundData implements Serializable {

	private static final long serialVersionUID = -4415909180096090497L; // generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Color			scoreColor;			// color of the score
	private int 			scorePosition; 		// position of the score (top or bottom)
	private List<String>	variantLegendText;	// legend of the variants for multi genome track (for MG project)
	private List<Color>		variantLegendColor;	// color of the variants for multi genome track (for MG project)


	/**
	 * Creates an instance of {@link ForegroundData}
	 */
	public ForegroundData() {
		this.setScoreColor(Colors.TRACK_SCORE);
		this.setScorePosition(TrackConstants.BOTTOM_SCORE_POSITION);
		this.setVariantLegendText(new ArrayList<String>());
		this.setVariantLegendColor(new ArrayList<Color>());
	}


	/**
	 * @return the color of the score
	 */
	public Color getScoreColor() {
		return scoreColor;
	}


	/**
	 * @return the position of the score
	 */
	public int getScorePosition() {
		return scorePosition;
	}


	/**
	 * @return the colors of the variants (MG project)
	 */
	public List<Color> getVariantLegendColor() {
		return variantLegendColor;
	}


	/**
	 * @return the legends of the variant (MG project)
	 */
	public List<String> getVariantLegendText() {
		return variantLegendText;
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		setScoreColor((Color)in.readObject());
		setScorePosition(in.readInt());
		setVariantLegendText((List<String>)in.readObject());
		setVariantLegendColor((List<Color>)in.readObject());
	}


	/**
	 * Sets the color of the score
	 * @param scoreColor the color to set
	 */
	public void setScoreColor(Color scoreColor) {
		this.scoreColor = scoreColor;
	}


	/**
	 * Sets the position of the score (top or bottom of the track)
	 * @param scorePosition the position to set (use {@link TrackConstants})
	 */
	public void setScorePosition(int scorePosition) {
		this.scorePosition = scorePosition;
	}


	/**
	 * Sets the colors of the variants
	 * @param variantLegendColor colors to set
	 */
	public void setVariantLegendColor(List<Color> variantLegendColor) {
		this.variantLegendColor = variantLegendColor;
	}


	/**
	 * Sets the legends of the variant (MG project)
	 * @param variantLegendText the text to set
	 */
	public void setVariantLegendText(List<String> variantLegendText) {
		this.variantLegendText = variantLegendText;
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(getScoreColor());
		out.writeInt(getScorePosition());
		out.writeObject(getVariantLegendText());
		out.writeObject(getVariantLegendColor());
	}
}
