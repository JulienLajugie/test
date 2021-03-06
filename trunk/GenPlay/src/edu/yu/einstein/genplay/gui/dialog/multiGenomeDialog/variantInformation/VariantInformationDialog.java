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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayMultiListScanner;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog.VCFLineDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.MultiGenomeDrawer;
import edu.yu.einstein.genplay.util.Images;

/**
 * This class shows variant stripe information. It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantInformationDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4932470485711131874L;

	public static final int WIDTH = 270; // width of the dialog

	private final VCFLineDialog vcfLineDialog;
	private VariantDisplayMultiListScanner iterator;

	private Variant currentVariant; // the current variant object to display
	private VCFLine currentLine; // the current variant object to display

	private final JButton jbFullLine; // button to show the full line
	private SearchOption options;

	private final JPanel headerPanel; // panel containing the global information
	private final JPanel infoPanel; // panel containing the INFO field information of the VCF
	private final JPanel formatPanel; // panel containing the FORMAT field information of the VCF
	private final JPanel navigationPanel; // panel to move forward/backward

	private final Track track;


	/**
	 * Constructor of {@link VariantInformationDialog}
	 * @param multiGenomeDrawer the multigenome drawer
	 */
	public VariantInformationDialog(MultiGenomeDrawer multiGenomeDrawer) {
		super(MainFrame.getInstance());
		vcfLineDialog = new VCFLineDialog();
		options = new SearchOption();
		track = MainFrame.getInstance().getTrackListPanel().getTrackFromGenomeDrawer(multiGenomeDrawer);
		int trackNumber = track.getNumber();
		String title = "Variant Properties";
		if (trackNumber > 0) {
			title += " (Track " + trackNumber + ")";
		}

		// Dialog settings
		setTitle(title);
		setIconImages(Images.getApplicationImages());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		// Create the full line button
		jbFullLine = new JButton("See the full line");
		jbFullLine.setToolTipText("See the whole VCF line.");
		jbFullLine.setMargin(new Insets(0, 0, 0, 0));
		jbFullLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showVCFLine();
			}
		});

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Initialize panels
		headerPanel = new JPanel();
		infoPanel = new JPanel();
		formatPanel = new JPanel();
		navigationPanel = new JPanel();

		// Add content
		add(headerPanel, gbc);

		gbc.gridy++;
		add(infoPanel, gbc);

		gbc.gridy++;
		add(formatPanel, gbc);

		gbc.gridy++;
		add(jbFullLine, gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		add(navigationPanel, gbc);
	}


	/**
	 * @return the name of the current genome
	 */
	private String getCurrentGenomeName () {
		if ((currentVariant == null) || (currentVariant instanceof MixVariant)) {
			return null;
		}
		return iterator.getCurrentVariantDisplayList(currentVariant).getGenomeName();
	}


	/**
	 * @return the iterator on variants
	 */
	public VariantDisplayMultiListScanner getIterator() {
		return iterator;
	}


	/**
	 * @return the search options
	 */
	protected SearchOption getOptions() {
		return options;
	}

	/**
	 * @return the variant
	 */
	public Variant getVariant() {
		return currentVariant;
	}


	/**
	 * Looks for the next variant and run the dialog initialization.
	 * @return true if it moves to the next variant, false otherwise
	 */
	protected boolean goToNextVariant() {
		boolean exit = false;
		while (!exit) {
			if (iterator.hasNext()) {
				Variant tmpVariant = iterator.next().get(0);
				if (isVariantValid(tmpVariant)) {
					currentVariant = tmpVariant;
					refreshDialog();
					return true;
				}
			} else {
				exit = true;
			}
		}
		return false;
	}


	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant() {
		boolean exit = false;
		while (!exit) {
			if (iterator.hasPrevious()) {
				Variant tmpVariant = iterator.previous().get(0);
				if (isVariantValid(tmpVariant)) {
					currentVariant = tmpVariant;
					refreshDialog();
					return true;
				}
			} else {
				exit = true;
			}
		}
		return false;
	}


	/**
	 * Initializes the content of the dialog box according to a variant.
	 */
	private void initContent() {
		String genomeName = getCurrentGenomeName();
		VariantInfo variantInfo;
		VariantFormat variantFormat;

		if (currentLine == null) {
			variantInfo = new VariantInfo(null);
			variantFormat = new VariantFormat(null, null, null);
		} else {
			variantInfo = new VariantInfo(currentLine);
			variantFormat = new VariantFormat(currentVariant, currentLine, genomeName);
		}


		updatePanel(headerPanel, new GlobalInformationPanel(currentVariant, currentLine, genomeName));
		updatePanel(infoPanel, variantInfo.getPane());
		updatePanel(formatPanel, variantFormat.getPane());
		NavigationPanel newNavigationPanel = new NavigationPanel(this);
		if (currentLine == null) {
			jbFullLine.setEnabled(false);
		} else {
			jbFullLine.setEnabled(true);
		}
		updatePanel(navigationPanel, newNavigationPanel);

		validate();

		pack();
	}


	/**
	 * @return true if the variant is valid according to the search options, false otherwise
	 */
	private boolean isVariantValid (Variant variant) {
		boolean variantResult = false;
		boolean genotypeResult = false;
		VariantType type = variant.getType();

		if (type == VariantType.MIX) {
			variantResult = true;
			genotypeResult = true;
		} else {
			if (((type == VariantType.INSERTION) && options.includeInsertion) ||
					((type == VariantType.DELETION) && options.includeDeletion) ||
					((type == VariantType.SNPS) && options.includeSNP) ||
					(options.includeReference &&
							((type == VariantType.REFERENCE_INSERTION) || (type == VariantType.REFERENCE_DELETION) || (type == VariantType.REFERENCE_SNP)))
					) {
				variantResult = true;
			}
			VCFLine line = variant.getVCFLine();
			if (line != null) {
				line.processForAnalyse();
				String rawName = FormattedMultiGenomeName.getRawName(iterator.getCurrentGenomeName(variant));

				if (line.genomeHasNoCall(rawName) && options.includeNoCall) {
					variantResult = true;
				}

				if ((line.isHeterozygote(rawName) && options.includeHeterozygote) ||
						(line.isHomozygote(rawName) && options.includeHomozygote)) {
					genotypeResult = true;
				}
			}
		}
		return variantResult && genotypeResult;
	}


	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newVariant the variant to display
	 */
	private void refreshDialog() {
		// Initialize the current variant
		if (currentVariant == null) {
			currentLine = null;
		} else {
			currentLine = currentVariant.getVCFLine();
			if (currentLine != null) {
				currentLine.processForAnalyse();
			}
		}

		// Initialize the content of the dialog
		initContent();

		// Relocate the screen position
		relocateScreenPosition();
	}


	/**
	 * Locates the screen position to the start position of the actual variant.
	 */
	private void relocateScreenPosition() {
		int variantStart = currentVariant.getStart();
		GenomeWindow currentGenomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		int width = currentGenomeWindow.getSize();
		int startWindow = variantStart - (width / 2);
		int stopWindow = startWindow + width;
		Chromosome chromosome = currentGenomeWindow.getChromosome();
		GenomeWindow genomeWindow = new SimpleGenomeWindow(chromosome, startWindow, stopWindow);
		ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
	}


	/**
	 * @param options the options to set
	 */
	protected void setOptions(SearchOption options) {
		this.options = options;
	}


	/**
	 * Method for showing the dialog box.
	 * @param iterator the multi list iterator
	 * @param X X position on the screen
	 * @param Y Y position on the screen
	 */
	public void show(VariantDisplayMultiListScanner iterator, int X, int Y) {
		this.iterator = iterator;
		currentVariant = iterator.getCurrentVariants().get(0);
		refreshDialog();
		setLocation(X, Y);
		setVisible(true);
	}


	/**
	 * Shows the vcf line dialog
	 */
	protected void showVCFLine() {
		vcfLineDialog.show(this, currentLine);
	}


	/**
	 * Updates a panel with another one
	 * @param previousPanel panel to update
	 * @param newPanel new panel
	 */
	private void updatePanel(JPanel previousPanel, JPanel newPanel) {
		previousPanel.removeAll();
		previousPanel.add(newPanel);
	}
}
