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
package edu.yu.einstein.genplay.gui.old.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASave;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAAverageScore;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLACountAllGenes;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLACountNonNullGenes;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLADistanceCalculator;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAExtractExons;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAExtractInterval;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAFilter;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAFilterStrand;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAGeneRenamer;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAScoreExons;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAScoreRepartitionAroundStart;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLASearchGene;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLASumScore;
import edu.yu.einstein.genplay.gui.old.action.geneListTrack.GLAUniqueScore;
import edu.yu.einstein.genplay.gui.old.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



/**
 * A popup menu for a {@link GeneListTrack}
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class GeneListTrackMenu extends TrackMenu {

	private static final long serialVersionUID = -7024046901324869134L; // generated ID

	private final JMenu		jmOperation;			// category operation

	private final JMenuItem	jmiAverageScore;		// average score menu
	private final JMenuItem	jmiCountAllGene;			// count gene menu
	private final JMenuItem	jmiCountNonNullGene;			// count gene menu
	private final JMenuItem	jmiDistanceCalculator;	// distance Calculator menu
	private final JMenuItem jmiExtractExons;		// extract exons menu
	private final JMenuItem jmiExtractInterval;		// extract interval menu
	private final JMenuItem jmiFilterScore;			// filter score menu
	private final JMenuItem jmiFilterStrand;		// filter strand menu
	private final JMenuItem jmiRenameGenes;			// rename genes menu
	private final JMenuItem jmiSaveGeneTrack;		// save the gene track
	private final JMenuItem jmiScoreExons;			// save the exons of the genelist
	private final JMenuItem jmiScoreRepartition;	// show the score repartition around the start of the genes
	private final JMenuItem jmiSearchGene;			// search gene menu
	private final JMenuItem	jmiSumScore;			// sum score menu
	private final JMenuItem jmiUniqueScore;			// unique score menu

	private final VersionedTrackMenuItems	versionedTrackMenuItems;	// versioned track menu items


	/**
	 * Creates an instance of a {@link GeneListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public GeneListTrackMenu(TrackList tl) {
		super(tl);

		jmOperation = new JMenu("Operation");

		jmiAverageScore = new JMenuItem(actionMap.get(GLAAverageScore.ACTION_KEY));
		jmiCountAllGene = new JMenuItem(actionMap.get(GLACountAllGenes.ACTION_KEY));
		jmiCountNonNullGene = new JMenuItem(actionMap.get(GLACountNonNullGenes.ACTION_KEY));
		jmiDistanceCalculator = new JMenuItem(actionMap.get(GLADistanceCalculator.ACTION_KEY));
		jmiExtractExons = new JMenuItem(actionMap.get(GLAExtractExons.ACTION_KEY));
		jmiExtractInterval = new JMenuItem(actionMap.get(GLAExtractInterval.ACTION_KEY));
		jmiFilterScore = new JMenuItem(actionMap.get(GLAFilter.ACTION_KEY));
		jmiFilterStrand = new JMenuItem(actionMap.get(GLAFilterStrand.ACTION_KEY));
		jmiRenameGenes = new JMenuItem(actionMap.get(GLAGeneRenamer.ACTION_KEY));
		jmiSaveGeneTrack = new JMenuItem(actionMap.get(ATASave.ACTION_KEY));
		jmiScoreExons = new JMenuItem(actionMap.get(GLAScoreExons.ACTION_KEY));
		jmiScoreRepartition = new JMenuItem(actionMap.get(GLAScoreRepartitionAroundStart.ACTION_KEY));
		jmiSearchGene = new JMenuItem(actionMap.get(GLASearchGene.ACTION_KEY));
		jmiSumScore = new JMenuItem(actionMap.get(GLASumScore.ACTION_KEY));
		versionedTrackMenuItems = new VersionedTrackMenuItems(this, trackList);
		jmiUniqueScore = new JMenuItem(actionMap.get(GLAUniqueScore.ACTION_KEY));

		jmOperation.add(jmiSumScore);
		jmOperation.add(jmiAverageScore);
		jmOperation.add(jmiCountAllGene);
		jmOperation.add(jmiCountNonNullGene);
		jmOperation.addSeparator();
		jmOperation.add(jmiSearchGene);
		jmOperation.add(jmiExtractInterval);
		jmOperation.add(jmiExtractExons);
		jmOperation.add(jmiUniqueScore);
		jmOperation.add(jmiScoreExons);
		jmOperation.addSeparator();
		jmOperation.add(jmiFilterScore);
		jmOperation.add(jmiFilterStrand);
		jmOperation.addSeparator();
		jmOperation.add(jmiRenameGenes);
		jmOperation.add(jmiDistanceCalculator);
		jmOperation.add(jmiScoreRepartition);

		add(jmOperation, 0);
		add(new Separator(), 1);

		add(jmiSaveGeneTrack, 11);

		addSeparator();
		versionedTrackMenuItems.addItemsToMenu();
	}
}