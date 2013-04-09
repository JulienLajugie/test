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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.util.FloatLists;


/**
 * Flattens a pileup of {@link ScoredChromosomeWindow} objects.
 * The score of each "flattened window" is computed from the scores of the
 * windows overlapping the "flattened window".  The {@link ScoreOperation} for
 * the calculation of the resulting score must be specified during the instantiation
 * of this class.
 * The windows involved in the pileup needs to be added in start position order using
 * the {@link #addWindow(ScoredChromosomeWindow)} method.
 * Once all the windows have been added the {@link #flush()} method needs to be used
 * in order to receive the last "flattened windows".
 * @author Julien Lajugie
 */
public class PileupFlattener {

	/** Queue containing the windows of the pileup */
	private final List<ScoredChromosomeWindow> windowQueue;

	/** Operation to compute the score of the result value of the flattening */
	private final ScoreOperation scoreOperation;


	/**
	 * Creates an instance of {@link PileupFlattener}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of the result value of the flattening
	 */
	public PileupFlattener(ScoreOperation scoreOperation) {
		windowQueue = new ArrayList<ScoredChromosomeWindow>();
		this.scoreOperation = scoreOperation;
	}


	/**
	 * Adds a new window to the {@link PileupFlattener} object.
	 * Windows must be added in start position order.
	 * @param window a {@link ScoredChromosomeWindow} to add
	 * @return a list of {@link ScoredChromosomeWindow} resulting from the flattening
	 * between the start position of the window added in the previous call of this
	 * function and the start position of the window specified in the current call.
	 */
	public List<ScoredChromosomeWindow> addWindow(ScoredChromosomeWindow window) {
		int newWindowStart = window.getStart();
		// add the new window at the end of the queue
		windowQueue.add(window);
		// retrieve the result of the pileup flattening
		List<ScoredChromosomeWindow> flattenPileup = getFlattenedPileup(newWindowStart);
		// remove the ele
		removeProcessedElements(newWindowStart);
		return flattenPileup;
	}


	/**
	 * Computes a score for each window of the list of flattened windows.
	 * The score is based on the scores of the different windows overlapping the resulting flattened window.
	 * The score of each window is computed using the {@link ScoreOperation} set during the construction
	 * of this {@link PileupFlattener} object.
	 * @param nodes list of the nodes (start and stop positions) from the flattening of the pileup
	 * @return a list of score containing one score for each flattened window
	 */
	private List<Float> computeScores(List<Integer> nodes) {
		if ((nodes != null) && (nodes.size() > 1)) {
			// creates structure for scores of each flattened window
			List<List<Float>> scoreLists = new ArrayList<List<Float>>();
			for (int i = 0; i < (nodes.size() - 1); i++) {
				scoreLists.add(new ArrayList<Float>());
			}
			// retrieve list of scores for each flattened window
			for (int i = 0; (i + 1) < nodes.size(); i++) {
				int flattenedStart = nodes.get(i);
				int flattenedStop = nodes.get(i + 1);
				int j = 0;
				while ((j < windowQueue.size()) && (windowQueue.get(j).getStart() < flattenedStop)) {
					if (windowQueue.get(j).getStop() > flattenedStart) {
						scoreLists.get(i).add(windowQueue.get(j).getScore());
					}
					j++;
				}
			}
			// compute and return score of each flattened window
			List<Float> scores = new ArrayList<Float>();
			for (List<Float> currentScoreList: scoreLists) {
				scores.add(processScoreList(currentScoreList));
			}
			return scores;
		} else {
			return null;
		}
	}


	/**
	 * Flushes this {@link PileupFlattener} object and returns the result for the positions located after start position
	 * of the window specified at the last call of the {@link #addWindow(ScoredChromosomeWindow)} method
	 * @return the result of the flattening for the position after
	 */
	public List<ScoredChromosomeWindow> flush() {
		List<ScoredChromosomeWindow> flattenedWindows = getFlattenedPileup(Integer.MAX_VALUE);
		windowQueue.clear();
		return flattenedWindows;
	}


	/**
	 * Flattens the overlapping windows of the queue up to specified positions.
	 * @param position a position
	 * @return a list of {@link ScoredChromosomeWindow} resulting from the flattening process.
	 * The score of the windows are computing accordingly to a {@link ScoreOperation} during
	 * construction of this {@link PileupFlattener} object.
	 */
	private List<ScoredChromosomeWindow> getFlattenedPileup(int position) {
		ScoredChromosomeWindow currentWindow;
		// nodes are start and stop positions of the windows resulting from the flattening process
		List<Integer> nodes = new ArrayList<Integer>();
		while (windowQueue.iterator().hasNext() && ((currentWindow = windowQueue.iterator().next()).getStart() < position)) {
			nodes.add(currentWindow.getStart());
			if (currentWindow.getStop() < position) {
				nodes.add(currentWindow.getStop());
			}
		}
		nodes.add(position);
		// sort the nodes
		Collections.sort(nodes);
		// remove duplicate nodes
		removeDuplicateNodes(nodes);
		// compute the score values for each windows from the flattening
		List<Float> scores = computeScores(nodes);
		// generate the list of windows from the flattening process
		List<ScoredChromosomeWindow> flattenedPileup = new ArrayList<ScoredChromosomeWindow>();
		for (int i = 0; i < scores.size(); i++) {
			ScoredChromosomeWindow windowToAdd = new SimpleScoredChromosomeWindow(nodes.get(i), nodes.get(i + 1), scores.get(i));
			flattenedPileup.add(windowToAdd);
		}
		return flattenedPileup;
	}


	/**
	 * Computes a score for a window based on the scores of the different windows overlapping the resulting
	 * flattened window.  The score of the window is computed using the {@link ScoreOperation} set during the
	 * construction of this {@link PileupFlattener} object.
	 * @param currentScoreList scores of the windows overlapping the "flattened window"
	 * @return the score computed
	 */
	private Float processScoreList(List<Float> currentScoreList) {
		switch (scoreOperation) {
		case ADDITION:
			return FloatLists.sum(currentScoreList);
		case AVERAGE:
			return FloatLists.average(currentScoreList);
		case DIVISION:
			if (currentScoreList.size() == 1) {
				return currentScoreList.get(0);
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) / currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Division with more than two operands not supported");
			}
		case MAXIMUM:
			return FloatLists.maxNoZero(currentScoreList);
		case MINIMUM:
			return FloatLists.minNoZero(currentScoreList);
		case MULTIPLICATION:
			if (currentScoreList.size() == 1) {
				return currentScoreList.get(0);
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) * currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Multiplication with more than two operands not supported");
			}
		case SUBTRACTION:
			if (currentScoreList.size() == 1) {
				return currentScoreList.get(0);
			} else if (currentScoreList.size() == 2) {
				return currentScoreList.get(0) - currentScoreList.get(1);
			} else {
				throw new UnsupportedOperationException("Subtraction with more than two operands not supported");
			}
		default:
			throw new UnsupportedOperationException("Operation not supported: " + scoreOperation.name());
		}
	}


	/**
	 * Removes the duplicate values in the sorted list of nodes
	 * @param nodes sorted list of integers
	 */
	private void removeDuplicateNodes(List<Integer> nodes) {
		// nothing to do in these cases
		if ((nodes != null) && (nodes.size() > 1)) {
			int previousValue = nodes.get(0);
			int i = 1;
			while (i < nodes.size()) {
				int currentValue = nodes.get(i);
				// remove if current value is equal to previous one
				if (currentValue == previousValue) {
					nodes.remove(i);
				} else {
					previousValue = currentValue;
					i++;
				}
			}
		}
	}


	/**
	 * Removes all the windows with a stop position smaller than the specified position.
	 * Because windows are added in start position order, theses windows won't be involved
	 * in future pileups.
	 * @param position a position
	 */
	private void removeProcessedElements(int position) {
		int i = 0;
		while ((i < windowQueue.size()) && (windowQueue.get(i).getStart() <= position)) {
			if (windowQueue.get(i).getStop() < position) {
				windowQueue.remove(i);
			} else {
				i++;
			}
		}
	}
}
