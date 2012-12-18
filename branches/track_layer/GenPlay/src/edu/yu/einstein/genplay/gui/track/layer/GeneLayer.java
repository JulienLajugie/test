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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.List;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;


/**
 * Layer displaying a {@link GeneList}
 * @author Julien Lajugie
 */
public class GeneLayer extends AbstractLayer<GeneList> implements Layer<GeneList>, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private static final double				MIN_X_RATIO_PRINT_NAME =
			GeneList.MIN_X_RATIO_PRINT_NAME;								// the name of the genes are printed if the ratio is higher than this value
	private static final double 			SCORE_SATURATION = 0.01d;		// saturation of the score of the exon for the display
	private static final short				GENE_HEIGHT = 6;				// size of a gene in pixel
	private static final short				UTR_HEIGHT = 3;					// height of a UTR region of a gene in pixel
	protected static final DecimalFormat 	SCORE_FORMAT =
			new DecimalFormat("#.###");										// decimal format for the score
	private int 							firstLineToDisplay;				// number of the first line to be displayed
	private int 							geneLinesCount;					// number of line of genes
	private int 							mouseStartDragY = -1;			// position of the mouse when start dragging
	private Gene 							geneUnderMouse = null;			// gene under the cursor of the mouse


	/**
	 * Creates an instance of a {@link GeneLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public GeneLayer(Track track, GeneList data, String name) {
		super(track, data, name);
		firstLineToDisplay = 0;
		geneLinesCount = 0;
		mouseStartDragY = -1;
		geneUnderMouse = null;
	}



	/**
	 * Draws the genes
	 * @param g {@link Graphics}
	 */
	@Override
	public void draw(Graphics g, int width, int height) {
		// we retrieve the project window
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		// we retrieve the minimum and maximum scores displayed in the track
		double max = getTrack().getScore().getMaximumScore();
		double min = getTrack().getScore().getMinimumScore();
		// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME
		boolean isGeneNamePrinted = projectWindow.getXRatio() > MIN_X_RATIO_PRINT_NAME;
		// set the data font metrics
		getData().setFontMetrics(g.getFontMetrics());
		// Retrieve the genes to print
		List<List<Gene>> genesToPrint = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
		if ((genesToPrint != null) && (genesToPrint.size() > 0)){
			// Compute the maximum number of line displayable
			int displayedLineCount = 0;
			if (isGeneNamePrinted) {
				displayedLineCount = ((height - (2 * GENE_HEIGHT)) / (GENE_HEIGHT * 3)) + 1;
			} else {
				displayedLineCount = ((height - GENE_HEIGHT) / (GENE_HEIGHT * 2)) + 1;
			}
			// calculate how many scroll on the Y axis are necessary to show all the genes
			geneLinesCount = (genesToPrint.size() - displayedLineCount) + 2;
			// For each line of genes on the screen
			for (int i = 0; i < displayedLineCount; i++) {
				// Calculate the height of the gene
				int currentHeight;
				if (isGeneNamePrinted) {
					currentHeight = (i * (GENE_HEIGHT * 3)) + (2 * GENE_HEIGHT);
				} else {
					currentHeight = (i * (GENE_HEIGHT * 2)) + GENE_HEIGHT;
				}
				// Calculate which line has to be printed depending on the position of the scroll bar
				int currentLine = i + firstLineToDisplay;
				if (currentLine < genesToPrint.size()) {
					// For each gene of the current line
					for (Gene geneToPrint : genesToPrint.get(currentLine)) {
						// retrieve the screen x coordinate of the start and stop position
						int x1 = projectWindow.genomeToScreenPosition(geneToPrint.getStart());
						int x2 = projectWindow.genomeToScreenPosition(geneToPrint.getStop());
						if (x2 != 0) {
							// Choose the color depending on if the gene is under the mouse and on the strand
							boolean isHighlighted = ((geneUnderMouse != null) && (geneToPrint.equals(geneUnderMouse)));
							g.setColor(GenPlayColor.geneToColor(geneToPrint.getStrand(), isHighlighted));
							// Draw the gene
							g.drawLine(x1, currentHeight, x2, currentHeight);
							// Draw the name of the gene if the zoom is small enough
							if (isGeneNamePrinted) {
								String geneName = geneToPrint.getName();
								if (geneToPrint.getStart() < projectWindow.getGenomeWindow().getStart()) {
									int newX = (int)Math.round((geneToPrint.getStart() - projectWindow.getGenomeWindow().getStart()) * projectWindow.getXRatio());	// former method
									g.drawString(geneName, newX, currentHeight - 1);
								} else {
									g.drawString(geneName, x1, currentHeight - 1);
								}
							}
							// For each exon of the current gene
							if (geneToPrint.getExonStarts() != null) {
								for (int j = 0; j < geneToPrint.getExonStarts().length; j++) {
									int exonX = projectWindow.genomeToScreenPosition(geneToPrint.getExonStarts()[j]);
									if (geneToPrint.getExonStops()[j] >= projectWindow.getGenomeWindow().getStart()) {
										int exonWidth = projectWindow.genomeToScreenPosition(geneToPrint.getExonStops()[j]) - exonX;
										if (exonWidth < 1) {
											exonWidth = 1;
										}
										// if we have some exon score values
										if (geneToPrint.getExonScores() != null) {
											// if we have just one exon score
											if (geneToPrint.getExonScores().length == 1) {
												g.setColor(GenPlayColor.scoreToColor(geneToPrint.getExonScores()[0], min, max));
											} else { // if we have values for each exon
												g.setColor(GenPlayColor.scoreToColor(geneToPrint.getExonScores()[j], min, max));
											}
										}
										// case where the exon is not at all in a UTR (untranslated region)
										if ((geneToPrint.getExonStarts()[j] >= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] <= geneToPrint.getUTR3Bound())) {
											g.fillRect(exonX, currentHeight + 1, exonWidth, GENE_HEIGHT);
										} else {
											// case where the whole exon is in a UTR
											if ((geneToPrint.getExonStops()[j] <= geneToPrint.getUTR5Bound()) || (geneToPrint.getExonStarts()[j] >= geneToPrint.getUTR3Bound())) {
												g.fillRect(exonX, currentHeight + 1, exonWidth, UTR_HEIGHT);
											} else {
												// case where the exon is in both UTR
												if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR3Bound())) {
													int UTR5Width = projectWindow.genomeToScreenPosition(geneToPrint.getUTR5Bound()) - exonX;
													int TRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR3Bound()) - exonX - UTR5Width;
													int UTR3Width = exonWidth - UTR5Width - TRWidth;
													g.fillRect(exonX, currentHeight + 1, UTR5Width, UTR_HEIGHT);
													g.fillRect(exonX + UTR5Width, currentHeight + 1, TRWidth, GENE_HEIGHT);
													g.fillRect(exonX + UTR5Width + TRWidth, currentHeight + 1, UTR3Width, UTR_HEIGHT);

												} else {
													// case where part of the exon is in the UTR and part is not
													if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR5Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR5Bound())) {
														// case where part is in the 5'UTR
														int UTRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR5Bound()) - exonX;
														g.fillRect(exonX, currentHeight + 1, UTRWidth, UTR_HEIGHT);
														g.fillRect(exonX + UTRWidth, currentHeight + 1, exonWidth - UTRWidth, GENE_HEIGHT);
													} else if ((geneToPrint.getExonStarts()[j] <= geneToPrint.getUTR3Bound()) && (geneToPrint.getExonStops()[j] >= geneToPrint.getUTR3Bound())) {
														// case where part is in the 3' UTR
														int TRWidth = projectWindow.genomeToScreenPosition(geneToPrint.getUTR3Bound()) - exonX; // TRWidth is the with of the TRANSLATED region
														g.fillRect(exonX, currentHeight + 1, TRWidth, GENE_HEIGHT);
														g.fillRect(exonX + TRWidth, currentHeight + 1, exonWidth - TRWidth, UTR_HEIGHT);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}


	/**
	 * Changes the scroll position of the panel when mouse dragged with the right button
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// we retrieve the project window
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		// we print the gene names if the x ratio > MIN_X_RATIO_PRINT_NAME
		boolean isGeneNamePrinted = projectWindow.getXRatio() > MIN_X_RATIO_PRINT_NAME;
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			int distance = 0;
			if (isGeneNamePrinted) {
				distance = (mouseStartDragY - e.getY()) / (3 * GENE_HEIGHT);
			} else {
				distance = (mouseStartDragY - e.getY()) / (2 * GENE_HEIGHT);
			}
			if (Math.abs(distance) > 0) {
				if (((distance < 0) && ((distance + firstLineToDisplay) >= 0))
						|| ((distance > 0) && ((distance + firstLineToDisplay) <= geneLinesCount))) {
					firstLineToDisplay += distance;
					mouseStartDragY = e.getY();
					getTrack().repaint();
				}
			}
		}
	}


	/**
	 * Retrieves the gene under the cursor of the mouse if there is one
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (!ScrollingManager.getInstance().isScrollingEnabled()) {
			// we retrieve the project window
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			Gene oldGeneUnderMouse = geneUnderMouse;
			geneUnderMouse = null;
			// retrieve the position of the mouse
			Point mousePosition = e.getPoint();
			// check if the name of genes is printed
			boolean isGeneNamePrinted = projectWindow.getXRatio() > MIN_X_RATIO_PRINT_NAME;
			// retrieve the list of the printed genes
			List<List<Gene>> printedGenes = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
			// do nothing if there is no genes
			if (printedGenes == null) {
				return;
			}
			// look for how many lines of genes are printed
			int displayedLineCount = printedGenes.size();

			// search if the mouse is on a line where there is genes printed on the track
			int mouseLine = -1;
			int i = 0;
			while ((mouseLine == -1) &&  (i < displayedLineCount)) {
				if (isGeneNamePrinted) {
					if ((mousePosition.y >= ((i * GENE_HEIGHT * 3) + GENE_HEIGHT)) &&
							(mousePosition.y <= ((i * GENE_HEIGHT * 3) + (3 * GENE_HEIGHT)))) {
						mouseLine = i;
					}
				} else {
					if ((mousePosition.y >= ((i * GENE_HEIGHT * 2) + GENE_HEIGHT)) &&
							(mousePosition.y <= ((i * GENE_HEIGHT * 2) + (2 * GENE_HEIGHT)))) {
						mouseLine = i;
					}
				}
				i++;
			}
			// if we found something
			if (mouseLine != -1) {
				// line of genes where the mouse is
				mouseLine += firstLineToDisplay;
				if (mouseLine < printedGenes.size()) {
					// search if the x position of the mouse is on a gene too
					int j = 0;
					while ((j < printedGenes.get(mouseLine).size()) && (geneUnderMouse == null)) {
						Gene currentGene = printedGenes.get(mouseLine).get(j);
						if ((mousePosition.x >= projectWindow.genomeToScreenPosition(currentGene.getStart())) &&
								(mousePosition.x <= projectWindow.genomeToScreenPosition(currentGene.getStop()))) {
							// we found a gene under the mouse
							geneUnderMouse = currentGene;
						}
						j++;
					}
				}
			}
			// unset the tool text and the mouse cursor if there is no gene under the mouse
			if (geneUnderMouse == null) {
				getTrack().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				getTrack().setToolTipText(null);
			} else {
				// if there is a gene under the mouse we also check
				// if there is an exon with a score under the mouse cursor
				Double scoreUnderMouse = null;
				if ((geneUnderMouse.getExonScores() != null) && (geneUnderMouse.getExonScores().length > 0)) {
					for (int k = 0; (k < geneUnderMouse.getExonStarts().length) && (scoreUnderMouse == null); k++) {
						if ((mousePosition.x >= projectWindow.genomeToScreenPosition(geneUnderMouse.getExonStarts()[k])) &&
								(mousePosition.x <= projectWindow.genomeToScreenPosition(geneUnderMouse.getExonStops()[k]))) {
							if (geneUnderMouse.getExonScores().length == 1) {
								scoreUnderMouse = geneUnderMouse.getExonScores()[0];
							} else {
								scoreUnderMouse = geneUnderMouse.getExonScores()[k];
							}
						}
					}
				}
				// set the cursor and the tooltip text if there is a gene under the mouse cursor
				getTrack().setCursor(new Cursor(Cursor.HAND_CURSOR));
				if (scoreUnderMouse == null) {
					// if there is a gene but no exon score
					getTrack().setToolTipText(geneUnderMouse.getName());
				} else {
					// if there is a gene and an exon score
					getTrack().setToolTipText(geneUnderMouse.getName() + ": " +  SCORE_FORMAT.format(scoreUnderMouse));
				}
			}
			// we repaint the track only if the gene under the mouse changed
			if (((oldGeneUnderMouse == null) && (geneUnderMouse != null))
					|| ((oldGeneUnderMouse != null) && (!oldGeneUnderMouse.equals(geneUnderMouse)))) {
				getTrack().repaint();
			}
		}
	}


	/**
	 * Sets the variable mouseStartDragY when the user press the right button of the mouse
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			mouseStartDragY = e.getY();
		}
	}


	/**
	 * Changes the scroll position of the panel when the wheel of the mouse is used with the right button
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("hhooe");
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (((e.getWheelRotation() < 0) && ((e.getWheelRotation() + firstLineToDisplay) >= 0))
					|| ((e.getWheelRotation() > 0) && ((e.getWheelRotation() + firstLineToDisplay) <= geneLinesCount))) {
				firstLineToDisplay += e.getWheelRotation();
				getTrack().repaint();
			}
		}
	}


/*	@Override
	public double getCurrentScoreToDisplay() {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			short currentChromosome = ProjectManager.getInstance().getProjectChromosome().getIndex(projectWindow.getGenomeWindow().getChromosome());
			int indexMid = (int) (projectWindow.getGenomeWindow().getMiddlePosition() / (double) getData().getBinSize());
			if ((getData().get(currentChromosome) != null) && (indexMid < getData().size(currentChromosome))) {
				return getData().get(currentChromosome, indexMid);
			}
		}
		return 0;
	}


	@Override
	public double getMaximumScoreToDisplay() {
		return new BLOMaxScoreToDisplay(getData()).compute();
	}


	@Override
	public double getMinimumScoreToDisplay() {
		return new BLOMinScoreToDisplay(getData()).compute();
	}*/


	@Override
	public LayerType getType() {
		return LayerType.GENE_LAYER;
	}


	@Override
	public void mouseClicked(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}
}
