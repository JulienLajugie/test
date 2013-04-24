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
package edu.yu.einstein.genplay.gui.action.layer.maskLayer;


import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTwoLayers;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.GenericSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Applies a mask to a fixed/variable windows layer
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public final class MLAApplyMask extends TrackListActionOperationWorker<SCWList> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Apply Mask";						// action name
	private static final String 			DESCRIPTION = "Apply mask on layer";			// tooltip
	private MaskLayer 						selectedLayer;									// selected layer
	private SCWLayer						maskedLayer;									// masked layer
	private Track							resultTrack;									// track where the result layer will be added


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = MLAApplyMask.class.getName();


	/**
	 * Creates an instance of {@link MLAApplyMask}
	 */
	public MLAApplyMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			GenericSCWLayer newLayer = new GenericSCWLayer(resultTrack, actionResult, maskedLayer.getName() + " masked");
			// add info to the history
			newLayer.getHistory().add(maskedLayer.getName() + " masked by " + selectedLayer.getName(), Colors.GREY);
			resultTrack.getLayers().add(newLayer);
			resultTrack.setActiveLayer(newLayer);
		}
	}


	@Override
	public Operation<SCWList> initializeOperation() {
		selectedLayer = (MaskLayer) getValue("Layer");
		if (selectedLayer != null) {
			LayerChooserDialog layerChooserDialog = new LayerChooserDialog();
			layerChooserDialog.setLayers(getTrackListPanel().getModel().getAllLayers());
			LayerType[] selectableLayers = {LayerType.BIN_LAYER, LayerType.SCW_LAYER, LayerType.MASK_LAYER};
			layerChooserDialog.setSelectableLayerTypes(selectableLayers);
			layerChooserDialog.setMultiselectable(false);
			if (layerChooserDialog.showDialog(getRootPane(), "Select Layer to Mask") == LayerChooserDialog.APPROVE_OPTION) {
				maskedLayer = (SCWLayer) layerChooserDialog.getSelectedLayer();
				if (maskedLayer != null) {
					SCWList data = maskedLayer.getData();
					SCWList mask = selectedLayer.getData();
					resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());
					if (resultTrack != null) {
						ScoreOperation scm = ScoreOperation.MULTIPLICATION;
						operation = new SCWLOTwoLayers(data, mask, scm);
						return operation;
					}
				}
			}
		}
		return null;
	}
}
