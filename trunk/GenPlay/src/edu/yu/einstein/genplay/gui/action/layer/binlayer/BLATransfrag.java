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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOTransfrag;
import edu.yu.einstein.genplay.core.operation.binList.BLOTransfragGeneList;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.TransfragDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Defines regions separated by gaps of a specified length and computes the average of these regions
 * @author Julien Lajugie
 * @author Chirag Gorasia
 */
public class BLATransfrag extends TrackListAction {

	private static final long serialVersionUID = 8388717083206483317L;	// generated ID
	private static final String 	ACTION_NAME = "Transfrag";			// action name
	private static final String 	DESCRIPTION =
			"Define regions separated by gaps of a specified length " +
					"and compute the average/max/sum of these regions" + HELP_TOOLTIP_SUFFIX; // tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Transfrag";
	private BinLayer 				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLATransfrag.class.getName();


	/**
	 * Creates an instance of {@link BLATransfrag}
	 */
	public BLATransfrag() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	public void trackListActionPerformed(ActionEvent arg0) {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			final BinList binList = selectedLayer.getData();
			final TransfragDialog tfDialog = new TransfragDialog(TransfragDialog.BINLIST_TRANSFRAG);
			int res = tfDialog.showTransfragDialog(getRootPane());
			if (res == TransfragDialog.APPROVE_OPTION) {
				int resType = tfDialog.getResultType();
				final ScoreOperation operationType = Utils.chooseScoreCalculation(getRootPane());
				if(operationType != null) {
					try {
						if (resType == TransfragDialog.GENERATE_GENE_LIST) {
							new TrackListActionOperationWorker<GeneList>(){
								private static final long serialVersionUID = -182674743663404937L;
								@Override
								protected void doAtTheEnd(GeneList actionResult) {
									if (actionResult != null) {
										Track selectedTrack = selectedLayer.getTrack();
										GeneLayer gl = new GeneLayer(selectedTrack, actionResult, "Transfrags from " + selectedTrack.getName());
										selectedTrack.getLayers().add(gl);
										selectedTrack.setActiveLayer(gl);
										selectedTrack.getLayers().remove(selectedTrack);
									}
								}
								@Override
								public Operation<GeneList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new BLOTransfragGeneList(binList, tfDialog.getGapSize(), operationType);
								}
							}.actionPerformed(null);
						} else if (resType == TransfragDialog.GENERATE_SCORED_LIST) {
							new TrackListActionOperationWorker<BinList>(){
								private static final long serialVersionUID = -182674743663404937L;
								@Override
								protected void doAtTheEnd(BinList actionResult) {
									if (actionResult != null) {
										selectedLayer.setData(actionResult, operation.getDescription());
									}
								}
								@Override
								public Operation<BinList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new BLOTransfrag(binList, tfDialog.getGapSize(), operationType);
								}
							}.actionPerformed(null);
						}
					} catch (Exception e) {
						ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error generating Transfrag");
					}
				}
			}
		}
	}
}
