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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.TwoBitSequenceList;
import edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay.DataScalerManager;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.FileChooser;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Layer displaying a mask
 * 
 * @author Julien Lajugie
 */
public class NucleotideLayer extends AbstractLayer<NucleotideList> implements Layer<NucleotideList>, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 3779631846077486596L;// generated ID
	private static final int NUCLEOTIDE_HEIGHT = 10; // y position of the nucleotides on the track
	private transient Integer maxBaseWidth = null; // size on the screen of the widest base to display (in pixels)
	private transient Integer baseUnderMouseIndex = null; // index of the base under the mouse
	private transient boolean nucleotidePrinted = false; // true if the nucleotide are printed


	/**
	 * Creates an instance of {@link NucleotideLayer} with the same properties as the specified {@link NucleotideLayer}. The copy of the data is shallow.
	 * 
	 * @param nucleotideLayer
	 */
	private NucleotideLayer(NucleotideLayer nucleotideLayer) {
		super(nucleotideLayer);
		maxBaseWidth = nucleotideLayer.maxBaseWidth;
		baseUnderMouseIndex = null;
		nucleotidePrinted = nucleotideLayer.nucleotidePrinted;
	}


	/**
	 * Creates an instance of a {@link NucleotideLayer}
	 * 
	 * @param track
	 *            track containing the layer
	 * @param data
	 *            data of the layer
	 * @param name
	 *            name of the layer
	 */
	public NucleotideLayer(Track track, NucleotideList data, String name) {
		super(track, data, name);
		maxBaseWidth = computeMaximumBaseWidth();
	}


	@Override
	public NucleotideLayer clone() {
		return new NucleotideLayer(this);
	}


	/**
	 * @return the maximum width in pixel that a base can take up
	 */
	private int computeMaximumBaseWidth() {
		int maxWidth = 0;
		// compute the length in pixels of the widest base to display
		String[] bases = { "N", "A", "C", "G", "T", "X"};
		for (String currBase : bases) {
			maxWidth = Math.max(maxWidth, getTrack().getFontMetrics(TrackConstants.FONT_DEFAULT).stringWidth(currBase));
		}
		return maxWidth;
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			if (maxBaseWidth == null) {
				// should be null after unserialization and need to be reinitialized
				maxBaseWidth = computeMaximumBaseWidth();
			}
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			long baseToPrintCount = projectWindow.getGenomeWindow().getSize();
			// if there is enough room to print something
			nucleotidePrinted = (baseToPrintCount <= width);
			drawNucleotideBackgrounds(g, width, height);
			drawNucleotideLetters(g, width, height);
		}
	}


	/**
	 * Draws the backgrounds of the nucleotide
	 * 
	 * @param g
	 *            {@link Graphics}
	 * @param width
	 *            with of the {@link Graphics} to paint
	 * @param height
	 *            height of the {@link Graphics} to paint
	 */
	private void drawNucleotideBackgrounds(Graphics g, int width, int height) {
		if (getData() != null) {
			if (nucleotidePrinted) {
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				Nucleotide[] nucleotides = DataScalerManager.getInstance().getScaledData(this);
				for (int position = projectWindow.getGenomeWindow().getStart(); position <= projectWindow.getGenomeWindow().getStop(); position++) {
					int index = position - projectWindow.getGenomeWindow().getStart();
					if ((index >= 0) && (nucleotides[index] != null)) {
						Nucleotide nucleotide = nucleotides[index];
						// compute the position on the screen
						int x = projectWindow.genomeToScreenPosition(position);
						int nucleoWith = projectWindow.genomeToScreenPosition(position + 1) - x;
						// select a different color for each type of base
						Color nucleoColor = Colors.nucleotideToColor(nucleotide);
						if ((baseUnderMouseIndex != null) && (index == baseUnderMouseIndex)) {
							nucleoColor = Colors.addTransparency(nucleoColor, Colors.ROLLED_OVER_NUCLEOTIDE_TRANSPARENCY);
						}
						g.setColor(nucleoColor);
						g.fillRect(x, 0, nucleoWith, height);
						if (nucleoWith >= 5) {
							g.setColor(Colors.TRACK_BACKGROUND);
							g.drawRect(x, 0, nucleoWith, height - 1);
						}
					}
				}
			}
			g.setColor(Color.WHITE);
			g.drawLine(0, 0, width, 0);
			g.drawLine(0, height - 1, width, height - 1);
		}
	}


	/**
	 * Draws the letter of the nucleotides
	 * 
	 * @param g
	 * @param width
	 *            with of the {@link Graphics} to paint
	 * @param height
	 *            height of the {@link Graphics} to paint
	 */
	private void drawNucleotideLetters(Graphics g, int width, int height) {
		if (getData() != null) {
			if (nucleotidePrinted) {
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				Nucleotide[] nucleotides = DataScalerManager.getInstance().getScaledData(this);
				for (int position = projectWindow.getGenomeWindow().getStart(); position <= projectWindow.getGenomeWindow().getStop(); position++) {
					int index = position - projectWindow.getGenomeWindow().getStart();
					if ((index >= 0) && (nucleotides[index] != null)) {
						Nucleotide nucleotide = nucleotides[index];
						long baseToPrintCount = projectWindow.getGenomeWindow().getSize();
						if ((maxBaseWidth * baseToPrintCount) <= width) {
							// compute the position on the screen
							int x = projectWindow.genomeToScreenPosition(position);
							// select a different color for each type of base
							if ((baseUnderMouseIndex != null) && (index == baseUnderMouseIndex)) {
								g.setColor(Colors.BLACK);
							} else {
								g.setColor(Colors.TRACK_BACKGROUND);
							}
							g.drawString(String.valueOf(nucleotide.getCode()), x, height - NUCLEOTIDE_HEIGHT);
						}
					}
				}
			} else {
				// if we can't print all the bases we just print a message for the user
				g.setColor(Color.black);
				g.drawString("Can't display DNA sequence at this zoom level.", 0, height - NUCLEOTIDE_HEIGHT);
			}
		}
	}


	@Override
	public LayerType getType() {
		return LayerType.NUCLEOTIDE_LAYER;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
	}


	/**
	 * Resets the tooltip and the highlighted base when the mouse is dragged
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (isVisible()) {
			if (baseUnderMouseIndex != null) {
				baseUnderMouseIndex = null;
				getTrack().getGraphicsPanel().setToolTipText(null);
				getTrack().repaint();
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}


	/**
	 * Resets the tooltip and the highlighted base when the mouse exits the track
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (baseUnderMouseIndex != null) {
			baseUnderMouseIndex = null;
			getTrack().getGraphicsPanel().setToolTipText(null);
			getTrack().repaint();
		}
	}


	/**
	 * Sets the tooltip and the base with the mouse over when the mouse move
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (isVisible() && (getData() != null)) {
			getTrack().getGraphicsPanel().setToolTipText("");
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			Integer oldBaseUnderMouseIndex = baseUnderMouseIndex;
			baseUnderMouseIndex = null;
			if (!ScrollingManager.getInstance().isScrollingEnabled()) {
				// if the zoom is too out we can't print the bases and so there is none under the mouse
				if (nucleotidePrinted) {
					// retrieve the position of the mouse
					Point mousePosition = e.getPoint();
					// retrieve the list of the printed nucleotides
					Nucleotide[] printedBases = DataScalerManager.getInstance().getScaledData(this);
					// do nothing if there is no nucleotides
					if (printedBases != null) {
						baseUnderMouseIndex = projectWindow.screenToGenomeWidth(mousePosition.x);
						// we repaint the track only if the gene under the mouse changed
						if (((oldBaseUnderMouseIndex == null) && (baseUnderMouseIndex != null)) || ((oldBaseUnderMouseIndex != null) && (!oldBaseUnderMouseIndex.equals(baseUnderMouseIndex)))) {
							getTrack().repaint();
						}
					}
				} else {
					getTrack().getGraphicsPanel().setToolTipText(null);
				}
				if (baseUnderMouseIndex != null) {
					Nucleotide nucleotide = DataScalerManager.getInstance().getScaledData(this)[baseUnderMouseIndex];
					if (nucleotide != null) {
						getTrack().getGraphicsPanel().setToolTipText(nucleotide.name());
					} else {
						getTrack().getGraphicsPanel().setToolTipText(null);
					}
				}
			}
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
	}


	@Override
	public void mouseReleased(MouseEvent e) {
	}


	/**
	 * Method used for unserialization
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		maxBaseWidth = null;
		baseUnderMouseIndex = null;
		nucleotidePrinted = false;
		twoBitSequenceListUnserialization();
	}


	/**
	 * Handle the unserialization of a {@link TwoBitSequenceList}.
	 */
	private void twoBitSequenceListUnserialization() {
		// if the data is a TwoBitSequenceList we want to make sure
		// that the file is still at the same location than when
		// the save was made. If not we need to ask the user for the new location.
		if (getData() instanceof TwoBitSequenceList) {
			TwoBitSequenceList twoBitData = ((TwoBitSequenceList) getData());
			try {
				// restore the connection to the file containing the 2 bit sequences
				twoBitData.reinitDataFile();
			} catch (FileNotFoundException e) {
				String filePath = twoBitData.getDataFilePath();
				File projectDir = ProjectManager.getInstance().getProjectDirectory();
				if (projectDir != null) {
					File twoBitFile = new File(projectDir, Utils.getFileName(filePath));
					try {
						TwoBitSequenceList new2BitData = new TwoBitSequenceList(twoBitData, twoBitFile);
						setData(new2BitData);
					} catch (FileNotFoundException e1) {
						// if the file is not found we ask the user to specify the path
						// since the track can be null we need to get the project root pane
						Component rootPane = MainFrame.getInstance().getRootPane();
						int dialogRes = JOptionPane.showConfirmDialog(rootPane, "The file " + filePath + " cannot be found\nPlease locate the file or press cancel to delete the Sequence Track", "File Not Found", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
						if (dialogRes == JOptionPane.OK_OPTION) {
							File selectedFile = FileChooser.chooseFile(rootPane, FileChooser.OPEN_FILE_MODE, "Load Sequence Track", Utils.getReadableSequenceFileFilters(), true);
							if (selectedFile != null) {
								try {
									TwoBitSequenceList new2BitData = new TwoBitSequenceList(twoBitData, selectedFile);
									setData(new2BitData);
								} catch (FileNotFoundException e2) {
									twoBitSequenceListUnserialization();
								}
							} else {
								twoBitSequenceListUnserialization();
							}
						} else {
							setData(null);
						}
					}
				}
			}
		}
	}
}
