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
package edu.yu.einstein.genplay.gui.track.layer.foreground;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.gui.track.TrackScore;
import edu.yu.einstein.genplay.gui.track.layer.AbstractLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Foreground layer of a track.
 * This layer displays the name of the track, the score at the center of the track if needed and the multi-genome legend if needed.
 * @author Julien Lajugie
 */
public class ForegroundLayer extends AbstractLayer<ForegroundData> implements Layer<ForegroundData> {

	private static final long serialVersionUID = -6813481315069255351L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private LegendDrawer legendDrawer;


	/**
	 * Creates an instance of {@link ForegroundLayer}
	 * @param track track in which the foreground layer is displayed
	 */
	public ForegroundLayer(Track track) {
		super(track, new ForegroundData());
		legendDrawer = new LegendDrawer(this);
	}


	@Override
	public ForegroundLayer clone() {
		return new ForegroundLayer(getTrack());
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			drawMiddleVerticalLine(g, width, height);
			drawScore(g, width, height);
			if (legendDrawer == null) {
				legendDrawer = new LegendDrawer(this);
			}
			legendDrawer.draw(g, width, height);
		}
	}


	/**
	 * Draws the main line in the middle of the track
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawMiddleVerticalLine(Graphics g, int width, int height) {
		int y1 = 0;
		int y2 = height;
		int x = (int)Math.round(width / (double)2);
		g.setColor(Colors.TRACK_MIDDLE_LINE);
		g.drawLine(x, y1, x, y2);
	}


	/**
	 * Draws the score of the track
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawScore(Graphics g, int width, int height) {
		ForegroundData data = getData();
		TrackScore trackScore = getTrack().getScore();
		if ((data != null) && (trackScore != null) && (trackScore.getCurrentScore() != null)) {
			float currentScore = trackScore.getCurrentScore();
			if (!Float.isNaN(currentScore)) {
				int scoreYPosition = 0;
				if (data.getScorePosition() == TrackConstants.BOTTOM_SCORE_POSITION) {
					scoreYPosition =  getTrack().getHeight() - 2;
				} else if (data.getScorePosition() == TrackConstants.TOP_SCORE_POSITION) {
					scoreYPosition = g.getFontMetrics().getHeight();
				}
				g.setColor(data.getScoreColor());
				g.drawString(NumberFormats.getScoreFormat().format(currentScore), (width / 2) + 3, scoreYPosition);
			}
		}
	}


	@Override
	public LayerType getType() {
		return LayerType.FOREGROUND_LAYER;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	//	/**
	//	 * Draws the header of the track.
	//	 * The header of the track is composed of:
	//	 * - the name
	//	 * - the stripe legend (for multi-genome)
	//	 * It allows a fine width distribution between these 2 texts.
	//	 * @param g
	//	 * @param multiGenomeDrawer
	//	 * @param fm
	//	 * @param name
	//	 * @param width
	//	 * @param backgroundColor
	//	 */
	//	public void drawHeaderTrack (Graphics g, MultiGenomeDrawer multiGenomeDrawer, FontMetrics fm, String name, int width, Color backgroundColor) {
	//		this.multiGenomeDrawer = multiGenomeDrawer;
	//		this.fm = fm;
	//		this.name = name;
	//		this.width = width;
	//		this.backgroundColor = backgroundColor;
	//
	//
	//		// Initializes the modified text length for display
	//		int trackTextDisplayWidth = 0;
	//		int legendTextDisplayWidth = 0;
	//
	//		if (validTrackName() && initializeLegend()) {				// if both track name and legend have to be drawed
	//			int widthOffset = 2;									// space between the border of the rectangle and the text.
	//			int totalWidth = this.width - (widthOffset * 4);		// width of the track
	//			int trackNameWidth = this.fm.stringWidth(this.name);			// width of the track name
	//			int legendWidth = this.fm.stringWidth(getLegend());			// width of the legend
	//
	//			if ((trackNameWidth + legendWidth) > totalWidth) {		// if track name and legend are longer than the width available
	//				int diff = (trackNameWidth + legendWidth) - totalWidth;	// we calculate the difference
	//				trackTextDisplayWidth = (trackNameWidth - (diff / 2));	// we subtract its half to the track name length
	//				legendTextDisplayWidth = (legendWidth - (diff / 2));	// we subtract its half to the legend length
	//
	//				// Fix the bug of the negative length
	//				// Value under 0 is considered as "no change" action further in the program
	//				if (trackTextDisplayWidth < 1) {
	//					trackTextDisplayWidth = 1;
	//				}
	//				if (legendTextDisplayWidth < 1) {
	//					legendTextDisplayWidth = 1;
	//				}
	//			}
	//		}
	//
	//		// Calls methods for drawing
	//		drawName(g, trackTextDisplayWidth);
	//		drawLegend(g, legendTextDisplayWidth);
	//
	//		//repaint(); // if uncommented, the activation/deactivation of the "Show Legend" option in the option dialog takes effect immediately!
	//	}
	//
	//
	//	/**
	//	 * Draws the legend of the stripe in a multi genome project
	//	 * @param g
	//	 */
	//	private void drawLegend (Graphics g, int displayTextWidth) {
	//		if (initializeLegend()) {
	//			if (stripeLegendText.size() > 0) {
	//				int widthOffset = 2;												// space between the border of the rectangle and the text.
	//				int x = 1;															// starts the rectangle drawing (at the left border of the frame)
	//				int rectWidth = widthOffset * 2;									// width of the rectangle, initialized with the double offset (left and right)
	//				if (displayTextWidth > 0) {											// if the text length has changed
	//					rectWidth += displayTextWidth;									// we take it into account
	//				} else {															// if not
	//					rectWidth += fm.stringWidth(getLegend());						// it is the native one
	//				}
	//				int textHeight = fm.getHeight();									// height of the text
	//
	//				// Draws
	//				g.setColor(this.backgroundColor);
	//				g.fillRect(x, 1, rectWidth, textHeight + widthOffset);
	//				g.setColor(Colors.GREEN);
	//				g.drawRect(x, 1, rectWidth - 1, textHeight + widthOffset);
	//
	//				// Draws the legend (text containing various colors)
	//				x++;			// shift the start x position of the text by +1 (do not touch the rectangle)
	//				textHeight--;	// shift the start y position of the text by -1 (center of the rectangle)
	//				for (int i = 0; i < stripeLegendText.size(); i++) {									// scans all text fragment of the legend
	//					g.setColor(stripeLegendColor.get(i));											// set the Graphic with the right color (associated to the text fragment)
	//					if (i > 0) {																	// if it is not the first text to write
	//						x += fm.stringWidth(stripeLegendText.get(i - 1));							// we move the x position after the previous text
	//					}
	//					String text = stripeLegendText.get(i);											// here is the current fragment to draw
	//					if (displayTextWidth > 0 && (x + fm.stringWidth(text)) > displayTextWidth) {	// if the length of the text (given by x and the current length of text) is larger than the limit length
	//						String newText = "";
	//						int charIndex = 0;
	//						while ((x + fm.stringWidth(newText + "...")) <= displayTextWidth) {			// we add char one by one to the new text until reaching the length limit
	//							newText += text.charAt(charIndex);
	//							charIndex++;
	//						}
	//						text = newText + "...";														// the text to draw is the new one + "..."
	//						g.drawString(text, x, textHeight);											// draws the text
	//						break;																		// stops the scan (exit loop)
	//					}
	//					g.drawString(text, x, textHeight);
	//				}
	//			}
	//		}
	//	}
	//
	//
	//	/**
	//	 * Initializes the list of text and the list of their associated color in order to draw the legend.
	//	 * @return true if it has been initialized, false otherwise.
	//	 */
	//	private boolean initializeLegend () {
	//		if (validLegend()) {
	//			// Sets parameters
	//			stripeLegendText = new ArrayList<String>();
	//			stripeLegendColor = new ArrayList<Color>();
	//
	//			// Gets the sorted genome names list
	//			List<String> genomeNames = multiGenomeDrawer.getRequiredGenomesList();
	//			Collections.sort(genomeNames);
	//
	//			// Color for text
	//			Color textColor = Colors.BLACK;
	//
	//			// Association text/color
	//			int genomeCounter = 0;
	//			for (String genomeName: genomeNames) {
	//
	//				// Gets variant type / color mapping
	//				Map<VariantType, Color> colors = multiGenomeDrawer.getVariantColorMap(genomeName);
	//
	//				// Gets the real size of the list
	//				int colorsSize = colors.size();
	//
	//				// If variant type/color exist
	//				if (colorsSize > 0) {
	//					genomeCounter++;									// add a genome
	//					if (genomeCounter > 1) {
	//						stripeLegendText.add(" " + genomeName + " (");	// add a white space, the name and " ("
	//					} else {
	//						stripeLegendText.add(genomeName + " (");		// add the name and " ("
	//					}
	//					stripeLegendColor.add(textColor);					// add the text color for the genome
	//
	//					// Association variant type/color
	//					int colorCounter = 0;
	//
	//					for (VariantType type: colors.keySet()) {
	//						colorCounter++;
	//						// Add the variant type shortcut
	//						if (type == VariantType.INSERTION) {
	//							stripeLegendText.add("I");
	//						} else if (type == VariantType.DELETION) {
	//							stripeLegendText.add("D");
	//						} else if (type == VariantType.SNPS) {
	//							stripeLegendText.add("SNPs");
	//						}
	//						stripeLegendColor.add(colors.get(type));		// add the chosen color
	//
	//						if (colorCounter < colorsSize) {				// if there is other selected variation type
	//							stripeLegendText.add(", ");					// add a ", "
	//							stripeLegendColor.add(textColor);			// with the text color
	//						}
	//					}
	//
	//					stripeLegendText.add(")");							// add a ")" for closing
	//					stripeLegendColor.add(textColor);
	//				}
	//			}
	//			return true;
	//		} else {
	//			return false;
	//		}
	//
	//	}
	//
	//	/**
	//	 * @return true if the stripe legend has to be drawn, false otherwise.
	//	 */
	//	private boolean validLegend () {
	//		return (ProjectManager.getInstance().isMultiGenomeProject() &&
	//				multiGenomeDrawer != null &&
	//				multiGenomeDrawer.getStripesList() != null &&
	//				ProjectManager.getInstance().getProjectConfiguration().isLegend());
	//	}
	//
	//
	//
	//
	//	/**
	//	 * @return the legend of the multigenome variant formated in one line
	//	 */
	//	private String getFormatedLegend() {
	//		List<String> variantLegend = getData().getVariantLegendText();
	//		String legend = "";
	//		if (variantLegend != null) {
	//			for (String text: variantLegend) {
	//				legend += text;
	//			}
	//		}
	//		return legend;
	//	}
}