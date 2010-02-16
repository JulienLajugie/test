/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Subtracts a constant from the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SubtractionConstantAction extends TrackListAction {


	private static final long serialVersionUID = 9085714881046182620L;	// generated ID
	private static final String 	ACTION_NAME = "Subtraction (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Subtract a constant from the scores of the selected track";	// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SubtractionConstantAction";


	/**
	 * Creates an instance of {@link SubtractionConstantAction}
	 * @param trackList a {@link TrackList}
	 */
	public SubtractionConstantAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Subtracts a constant from the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to subtract: f(x)=x - C", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				final BinList binList = ((BinListTrack)selectedTrack).getBinList();
				final DataPrecision precision = Utils.choosePrecision(getRootPane());
				if (precision != null) {
					final String description;
					if (precision != binList.getPrecision()) {
						description = "Subtract constant C = " + constant + ", Precision changed: New Precision = " + precision;
					} else {
						description = "Subtract constant C = " + constant;
					}
					// thread for the action
					new ActionWorker<BinList>(trackList) {
						@Override
						protected BinList doAction() {
							return BinListOperations.subtraction(binList, constant.doubleValue(), precision);
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							selectedTrack.setBinList(actionResult, description);
						}
					}.execute();
				}
			}
		}
	}
}