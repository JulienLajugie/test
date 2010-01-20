/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * A track containing a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListTrack extends CurveTrack {

	private static final long serialVersionUID = -2203140318940911180L; // generated ID
	private final ScoredChromosomeWindowList data;		// ScoredChromosomeWindowList used to create the track

	
	/**
	 * Creates an instance of {@link SCWListTrack}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data {@link ScoredChromosomeWindowList} showed in the track
	 */
	public SCWListTrack(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, int trackNumber,  ScoredChromosomeWindowList data) {
		this.data = data;
		initComponent(zoomManager, displayedGenomeWindow, trackNumber);
	}
	

	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.Track#copy()
	 */
	@Override
	public Track copy() {
		Track copiedTrack = new SCWListTrack(trackGraphics.getZoomManager(), trackGraphics.genomeWindow, trackHandle.getTrackNumber(), data);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;		
	}

	
	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.Track#createTrackGraphics(yu.einstein.gdp2.util.ZoomManager, yu.einstein.gdp2.core.GenomeWindow)
	 */
	@Override
	protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		trackGraphics = new SCWListTrackGraphics(zoomManager, displayedGenomeWindow, data);
	}
	
	
	/**
	 * @return the data
	 */
	public ScoredChromosomeWindowList getData() {
		return data;
	}
}
