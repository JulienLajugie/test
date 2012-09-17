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
package edu.yu.einstein.genplay.gui.action.multiGenome.update;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.operation.UpdateEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.VCF.MGOUpdateVCFGenotype;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.genotype.GenotypeVCFDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAVCFUpdateGenotype extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Update the genotype of a file using track variations."; 				// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Update a VCF";			// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome Genotype Update";

	private boolean success;


	/**
	 * Creates an instance of {@link MGAVCFUpdateGenotype}.
	 */
	public MGAVCFUpdateGenotype() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Get track information
			Track<?> track = MainFrame.getInstance().getTrackList().getSelectedTrack();
			MultiGenomeDrawer genomeDrawer = track.getMultiGenomeDrawer();

			// Create the export settings
			ExportSettings settings = new ExportSettings(genomeDrawer);

			GenotypeVCFDialog dialog = new GenotypeVCFDialog(settings);

			if (dialog.showDialog(getRootPane()) == MultiGenomeTrackActionDialog.APPROVE_OPTION) {
				// Create the file to update
				VCFFile VCFFileToUpdate = dialog.getVCFToGenotype();

				// Create the output file
				String outputPath = dialog.getOutputFile();

				Map<String, String> genomeNameMap = dialog.getGenomeMap();

				// Create the update engine
				UpdateEngine engine = new MGOUpdateVCFGenotype();
				engine.setFileMap(settings.getFileMap());
				engine.setVariationMap(settings.getVariationMap());
				engine.setFilterList(settings.getFilterList());
				engine.setFileToPhase(VCFFileToUpdate);
				engine.setPath(outputPath);
				engine.setGenomeNameMap(genomeNameMap);

				engine.compute();
			}
		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		success = actionResult;

		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @return true if the action has been correctly finish, false otherwise
	 */
	public boolean hasBeenDone () {
		return success;
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}