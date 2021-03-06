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
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksDensity;
import edu.yu.einstein.genplay.dataStructure.enums.PeakFinderType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;



/**
 * Panel for the input of a density peak finder
 * @author Julien Lajugie
 * @version 0.1
 */
class DensityFinderPanel extends JPanel implements PeakFinderPanel {

	private static final long serialVersionUID = 3770930911273486277L;	// generated ID
	private static final int 			INSET = 10;						// inset border to subcomponents
	private static final String 		NAME =
			PeakFinderType.DENSITY.toString(); 							// name of the peak finder
	private static float 				defaultLowThreshold =
			Float.NEGATIVE_INFINITY;									// default low threshold
	private static float 				defaultHighThreshold =
			Float.POSITIVE_INFINITY;									// default high threshold
	private static int 					defaultRegionWidth = 10;		// default region width
	private static double 				defaultPercentage = 1;			// default percentage
	private final JTextArea 			jtaDescription;					// description of the peak finder
	private final JLabel 				jlLowThreshold;					// label low threshold
	private final JLabel 				jlHighTreshold;					// label high threshold
	private final JLabel 				jlRegionWidth;					// label region width
	private final JLabel				jlPercentage;					// label percentage
	private final JFormattedTextField 	jftfLowThreshold;				// input box low threshold
	private final JFormattedTextField 	jftfHighThreshold;				// input box high threshold
	private final JFormattedTextField 	jftfRegionWidth;				// input box region width
	private final JFormattedTextField 	jftfPercentage;					// input box percentage
	private final BLOFindPeaksDensity 	bloFindPeaks;					// BinList operation to set


	/**
	 * Creates an instance of {@link DensityFinderPanel}
	 */
	DensityFinderPanel(BLOFindPeaksDensity bloFindPeaks) {
		super();

		this.bloFindPeaks = bloFindPeaks;

		setName(NAME);
		setBorder(BorderFactory.createTitledBorder("Input"));

		jtaDescription = new JTextArea("Please refere to the help file for an explanation of the parameters L, H, S and P");
		jtaDescription.setEditable(false);
		jtaDescription.setBackground(getBackground());
		jtaDescription.setLineWrap(true);
		jtaDescription.setWrapStyleWord(true);

		jlLowThreshold = new JLabel("Enter the low threshold L");
		jftfLowThreshold = new JFormattedTextField(NumberFormat.getInstance());
		jftfLowThreshold.setValue(defaultLowThreshold);
		jftfLowThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfLowThreshold.setColumns(8);

		jlHighTreshold = new JLabel("Enter the high threshold H");
		jftfHighThreshold = new JFormattedTextField(NumberFormat.getInstance());
		jftfHighThreshold.setValue(defaultHighThreshold);
		jftfHighThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfHighThreshold.setColumns(8);

		jlRegionWidth = new JLabel("Enter the region half size S (in windows)");
		jftfRegionWidth = new JFormattedTextField(NumberFormat.getInstance());
		jftfRegionWidth.setValue(defaultRegionWidth);
		jftfRegionWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfRegionWidth.setColumns(8);
		((NumberFormatter) jftfRegionWidth.getFormatter()).setMinimum(1);


		jlPercentage = new JLabel("Enter percentage P");
		// create the formatter for the percentage input box
		NumberFormat percentageFormat = NumberFormat.getPercentInstance();
		NumberFormatter nf = new NumberFormatter(percentageFormat);
		nf.setMinimum(0.0);
		nf.setMaximum(1.0);
		jftfPercentage = new JFormattedTextField(nf);
		jftfPercentage.setValue(defaultPercentage);
		jftfPercentage.setHorizontalAlignment(SwingConstants.RIGHT);
		jftfPercentage.setColumns(8);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(INSET, INSET, 30, INSET);
		add(jtaDescription, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlLowThreshold, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jftfLowThreshold, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlHighTreshold, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jftfHighThreshold, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlRegionWidth, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jftfRegionWidth, c);

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.insets = new Insets(0, INSET, INSET, 0);
		add(jlPercentage, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, INSET, INSET);
		add(jftfPercentage, c);
	}


	/**
	 * @return the high threshold parameter of the density peak finder
	 */
	private float getHighThreshold() {
		float highThreshold = ((Number) jftfHighThreshold.getValue()).floatValue();
		return highThreshold;
	}


	/**
	 * @return the low threshold parameter of the density peak finder
	 */
	private float getLowThreshold() {
		float lowThreshold = ((Number) jftfLowThreshold.getValue()).floatValue();
		return lowThreshold;
	}


	/**
	 * @return the percentage parameter of the density peak finder
	 */
	private double getPercentage() {
		double percentage = ((Number) jftfPercentage.getValue()).doubleValue();
		return percentage;
	}


	/**
	 * @return the half window width parameter of the density peak finder
	 */
	private int getRegionWidth() {
		int windowWidth = ((Number) jftfRegionWidth.getValue()).intValue();
		return windowWidth;
	}


	@Override
	public void saveInput() {
		defaultRegionWidth = getRegionWidth();
		defaultLowThreshold = getLowThreshold();
		defaultHighThreshold = getHighThreshold();
		defaultPercentage = getPercentage();
	}


	@Override
	public String toString() {
		return getName();
	}


	@Override
	public Operation<BinList[]> validateInput() {
		if (getLowThreshold() >= getHighThreshold()) {
			JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Invalid Input", JOptionPane.WARNING_MESSAGE);
			return null;
		} else {
			bloFindPeaks.setDensity(getPercentage());
			bloFindPeaks.setHalfWidth(getRegionWidth());
			bloFindPeaks.setHighThreshold(getHighThreshold());
			bloFindPeaks.setLowThreshold(getLowThreshold());
			return bloFindPeaks;
		}
	}
}
