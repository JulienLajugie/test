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
package edu.yu.einstein.genplay.gui.track.ruler;

import java.awt.Color;
import java.awt.Graphics;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.track.Drawer;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Drawer that draws a {@link Ruler}
 * @author Julien Lajugie
 */
public class RulerDrawer implements Drawer {

	private static final int 			LINE_COUNT = 10;							// Number of line to print (must be an even number)
	private static final Color			LINE_COLOR = Colors.LIGHT_GREY;				// color of the lines
	private static final Color			TEXT_COLOR = Colors.BLACK;					// color of the text
	private static final Color			MIDDLE_LINE_COLOR = Colors.RED;				// color of the line in the middle
	private static final int 			MAJOR_TEXT_HEIGHT = 11;						// height of the absolute position text
	private static final int 			MINOR_TEXT_HEIGHT = 2;						// height of the relative position text


	@Override
	public void draw(Graphics g, int width, int height) {
		g.setFont(TrackConstants.FONT_RULER);
		drawRelativeUnits(g, width, height);
		drawAbsoluteUnits(g, width, height);
	}


	/**
	 * Draws the absolute units.
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawAbsoluteUnits(Graphics g, int width, int height) {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		// Set graphic parameters
		int halfWidth = (int)Math.round(width / 2d);
		int yText = height - MAJOR_TEXT_HEIGHT;
		g.setColor(MIDDLE_LINE_COLOR);

		// Set positions
		int positionStart = projectWindow.getGenomeWindow().getStart();
		int positionStop = projectWindow.getGenomeWindow().getStop();
		int currentMiddlePosition = (int) projectWindow.getGenomeWindow().getMiddlePosition();
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			Chromosome currentChromosome = projectWindow.getGenomeWindow().getChromosome();
			String genomeName = FormattedMultiGenomeName.getFullNameWithoutAllele(MGDisplaySettings.SELECTED_GENOME);
			AlleleType inputAlleleType = FormattedMultiGenomeName.getAlleleName(MGDisplaySettings.SELECTED_GENOME);
			positionStart = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, positionStart, currentChromosome, genomeName);
			positionStop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, positionStop, currentChromosome, genomeName);
			currentMiddlePosition = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, currentMiddlePosition, currentChromosome, genomeName);
		}

		// Draw units
		String stringToPrint = getFormattedNumber(positionStart);
		g.drawString(stringToPrint, 2, yText);
		stringToPrint = getFormattedNumber(currentMiddlePosition);
		g.drawString(stringToPrint, halfWidth + 3, yText);
		stringToPrint = getFormattedNumber(positionStop);
		g.drawString(stringToPrint, width - g.getFontMetrics().stringWidth(stringToPrint) - 1, yText);
	}


	/**
	 * Draws the relative units.
	 * @param g  {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawRelativeUnits(Graphics g, int width, int height) {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		int positionStart = projectWindow.getGenomeWindow().getStart();
		int positionStop = projectWindow.getGenomeWindow().getStop();
		int y = height - MINOR_TEXT_HEIGHT;
		int lastTextStopPos = 0;
		double gap = width / (double)LINE_COUNT;
		for (int i = 0; i < LINE_COUNT; i++) {
			int x1 = (int)Math.round(i * gap);
			int x2 = (int)Math.round((((2 * i) + 1) * gap) / 2d);
			int distanceFromMiddle = (Math.abs(i - (LINE_COUNT / 2)) * (positionStop - positionStart)) / LINE_COUNT;
			String stringToPrint = NumberFormats.getPositionFormat().format(distanceFromMiddle);
			if (x1 >= lastTextStopPos) {
				g.setColor(TEXT_COLOR);
				g.drawString(stringToPrint, x1 + 2, y);
				lastTextStopPos = x1 + g.getFontMetrics().stringWidth(stringToPrint) + 2;
			} else {
				g.setColor(LINE_COLOR);
				g.drawLine(x1, y, x1, height);
			}
			g.setColor(LINE_COLOR);
			g.drawLine(x2, y, x2, height);
		}
	}


	private String getFormattedNumber (int position) {
		if (position == -1000000000) {
			return "-";
		}
		return NumberFormats.getPositionFormat().format(position);
	}
}
