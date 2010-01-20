/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.binList;

import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;


/**
 * Generates a file showing the repartition of the score values of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ShowRepartitionAction extends TrackListAction {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a csv file showing the repartition of the scores of the selected track";	// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "showRepartition";


	/**
	 * Creates an instance of {@link ShowRepartitionAction}
	 * @param trackList a {@link TrackList}
	 */
	public ShowRepartitionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Generates a file showing the repartition of the score values of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0"), 0, 1000, 1);
			if (scoreBin != null) {
				final JFileChooser saveFC = new JFileChooser(trackList.getConfigurationManager().getDefaultDirectory());
				saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
				saveFC.setDialogTitle("Bin repartition " + selectedTrack.getName());
				saveFC.setSelectedFile(new File(".csv"));
				int returnVal = saveFC.showSaveDialog(getRootPane());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (!Utils.cancelBecauseFileExist(getRootPane(), saveFC.getSelectedFile())) {
						final BinList binList = ((BinListTrack)selectedTrack).getBinList();
						// thread for the action
						new ActionWorker<Void>(trackList) {
							@Override
							protected Void doAction() {
								try {
									BinListOperations.repartition(binList, scoreBin.doubleValue(), saveFC.getSelectedFile());
									return null;
								} catch (Exception e) {
									ExceptionManager.handleException(getRootPane(), e, "Error while calculating the repartition");
									return null;
								}
							}
							@Override
							protected void doAtTheEnd(Void actionResult) {}							
						}.execute();	
					}
				}
			}
		}		
	}
}