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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOLoessRegression;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.GenomeWidthChooser;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;


/**
 * Computes a Loess regression on the selected {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLALoessRegression extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 6749094444366905914L;	// generated ID
	private static final String 	ACTION_NAME = "Loess Regression";	// action name
	private static final String 	DESCRIPTION =
			"Compute a Loess regression on the selected layer";				// tooltip
	private BinLayer 				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLALoessRegression.class.getName();


	/**
	 * Creates an instance of {@link BLALoessRegression}
	 */
	public BLALoessRegression() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			BinList binList = selectedLayer.getData();
			int windowSize = binList.getBinSize();
			if(windowSize > 0) {
				Integer movingWindowWidth = GenomeWidthChooser.getMovingWindowSize(getRootPane(), windowSize);
				if(movingWindowWidth != null) {
					int fillNull = JOptionPane.showConfirmDialog(getRootPane(), "Do you want to extrapolate the null windows", "Extrapolate null windows", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					Operation<BinList> operation = null;
					if (fillNull == JOptionPane.YES_OPTION) {
						operation = new BLOLoessRegression(binList, movingWindowWidth, true);
					} else if (fillNull == JOptionPane.NO_OPTION) {
						operation = new BLOLoessRegression(binList, movingWindowWidth, false);
					}
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}
