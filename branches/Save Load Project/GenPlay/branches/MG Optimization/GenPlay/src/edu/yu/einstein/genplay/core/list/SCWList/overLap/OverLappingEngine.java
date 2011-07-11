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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.util.DoubleLists;


/**
 * Manage the overlapping region for a chromosome
 * Provides news lists of:
 * 	- start positions
 * 	- stop positions
 * 	- scores
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
final class OverLappingEngine implements Serializable {
	
	private static final long serialVersionUID = 7462066006408418433L;
	private List<ScoredChromosomeWindow> 	list;					//original list of value
	private IntArrayAsIntegerList 			newStartList;			//new list of start positions
	private IntArrayAsIntegerList 			newStopList;			//new list of stop positions
	private List<Double> 					newScoresList;			//new list of scores
	private List<OverLappingNode> 			currentListOfNode;		//current list of nodes
	private List<Integer> 					currentListOfPosition;	//current list of positions
	private List<Double> 					currentListOfScore;		//current list of scores
	private ScoreCalculationMethod			scm;
	
	/**
	 * OverLapEngine constructor
	 * The OverLapManagement class controls this class
	 */
	protected OverLappingEngine (ScoreCalculationMethod scm) {
		this.scm = scm;
	}
	
	/**
	 * init method
	 * This method initializes and generates the new list of:
	 * 	- start positions
	 * 	- stop positions
	 * 	- scores
	 * 
	 * @param list	a list of scored chromosome window
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected void init (List<ScoredChromosomeWindow> list) throws InterruptedException, ExecutionException {
		this.list = list;
		this.newStartList = new IntArrayAsIntegerList();
		this.newStopList = new IntArrayAsIntegerList();
		this.newScoresList = new ArrayList<Double>();
		generateList();
	}
	
	
	////////////////////////////////////////////////	Generate list
	
	/**
	 * generateList method
	 * This method generates all the new lists:
	 * 	- start
	 * 	- stop
	 * 	- score
	 */
	private void generateList () {
		int i = 0;
		while (i < this.list.size()) {	//on the chromosome size
			if ((i+1) < this.list.size()) {	//if the next position exists
				if (getOriginalStart(i+1) >= getOriginalStop(i)) {	// if the next start is higher or equal to the current stop
					addInStart(getOriginalStart(i));	//we add the current start position
					addInStop(getOriginalStop(i));	//we add the current stop position
					this.newScoresList.add(getOriginalScore(i));	//we add the current score
					i++;	//the current position can be incremented
				} else {	//the position is involved on a overlapping
					i = overLappingManagement(i);	//this method manage the overlapping situation and return the next index after this region
				}
			} else {
				addInStart(getOriginalStart(i));	//we add the current start position
				addInStop(getOriginalStop(i));	//we add the current stop position
				this.newScoresList.add(getOriginalScore(i));	//we add the current score
				i++;	//the current position can be incremented
			}
		}
	}
	
	/**
	 * overLappingManagement method
	 * This method manage the overlapping for a specific index
	 * 
	 * @param index	index of the overlapping region beginning
	 * @return		the next index after the overlapping region ending
	 */
	private int overLappingManagement(int index) {
		//Lists initialization
		this.currentListOfNode = new ArrayList<OverLappingNode>();
		this.currentListOfPosition = new ArrayList<Integer>();
		this.currentListOfScore = new ArrayList<Double>();
		//Lists generation
		generatePositionsAndNodesLists(index);	//generates the lists of the nodes and positions contained on the overlapping region
		generateScoreList();	//generates the associated list of score
		//New lists updates
		while (this.currentListOfPosition.size() > 1) {	//for all position
			addInStart(this.currentListOfPosition.get(0));	//the start is the first index of the list
			addInStop(this.currentListOfPosition.get(1));	//the stop is the second index of the list
			this.currentListOfPosition.remove(0);	//the first index is deleted to progress on the list
		}
		for (Double score: this.currentListOfScore) {	//all scores are added in the new list
			this.newScoresList.add(score);
		}
		return (index + (this.currentListOfNode.size()/2));	//return the next index after the overlapping region
	}
	
	/**
	 * generatePositionsAndNodesLists method
	 * This method generates the list of nodes and the list of positions contained in the overlapping regions.
	 * These lists are sorted at the end.
	 * 
	 * @param index index of the overlapping region beginning
	 */
	private void generatePositionsAndNodesLists (int index) {
		int nextIndex = index + 1;
		addNode(new OverLappingNode(true, this.list.get(index)));	//the first node is the current position, added as a start node
		addNode(new OverLappingNode(false, this.list.get(index)));	//added as a stop node
		addPosition(this.getOriginalStart(index));	//added as a start position
		addPosition(this.getOriginalStop(index));	//added as a stop position
		boolean valid = true;
		if (nextIndex < this.list.size()) {	//if the next index is valid
			while (valid) {
				if (getOriginalStop(index) > getOriginalStart(nextIndex)) {	//if the current stop is higher than the next start, nodes and position are added
					addNode(new OverLappingNode(true, this.list.get(nextIndex)));	//added as a start node
					addNode(new OverLappingNode(false, this.list.get(nextIndex)));	//added as a stop node
					addPosition(this.getOriginalStart(nextIndex));	//added as a start position
					addPosition(this.getOriginalStop(nextIndex));	//added as a stop position
					if (getOriginalStop(nextIndex) > getOriginalStop(index)) {	//if the next stop is higher than the current stop
						index = nextIndex;	//the next position become the current position
					}
				}
				if ((nextIndex + 1) < this.list.size()) {	//if the next + 1 index is valid
					nextIndex++;	//process can continue
				} else {	//else
					valid = false;	//exit
				}
			}
		}
		Collections.sort(this.currentListOfNode);	//the nodes list is sorted 
		Collections.sort(this.currentListOfPosition);	//the position list is sorted 
	}
	
	/**
	 * generateScoreList method
	 * This method generates the current score list
	 */
	private void generateScoreList () {
		LinkedList<ScoredChromosomeWindow> linkedList = new LinkedList<ScoredChromosomeWindow>();
		Integer currentPos = this.currentListOfNode.get(0).getValue();	//the current position is the position of the first node
		for (OverLappingNode node: this.currentListOfNode) {	//for all nodes
			if (node.getValue() != currentPos) {	//if the value of the node is different than the value of the current position
				this.currentListOfScore.add(getScore(linkedList));	//the score of the current position is calculated
				currentPos = node.getValue();	//the value of the node become the current position
			}
			if (node.isStart()) {	//if the node is a start node
				linkedList.add(node.getScw());	//the scored chromosome window is added to the linked list of score
			} else {	//if the node is a stop node
				linkedList.remove(node.getScw());	//the scored chromosome window is removed to the linked list of score
			}
		}
	}
	
	/**
	 * getScore method
	 * This method calculates the score for the list of scored chromosome window
	 * 
	 * @param linkedList	list of scored chromosome window
	 * @return				the calculation result of the linked list
	 */
	private double getScore (LinkedList<ScoredChromosomeWindow> linkedList) {
		double score = 0.0;
		List<Double> list = new ArrayList<Double>();
		for (ScoredChromosomeWindow scw: linkedList) {
			list.add(scw.getScore());
		}
		switch (this.scm) {
			case AVERAGE:
				score = DoubleLists.average(list);
				break;
			case MAXIMUM:
				score = DoubleLists.maxNoZero(list);
				break;
			case SUM:
				score = DoubleLists.sum(list);
				break;
			default:
				break;
		}
		return score;
	}
	
	
	////////////////////////////////////////////////	List management methods (add & get)
	
	private int getOriginalStart (int index) {
		return this.list.get(index).getStart();
	}
	
	private int getOriginalStop (int index) {
		return this.list.get(index).getStop();
	}
	
	private double getOriginalScore (int index) {
		return this.list.get(index).getScore();
	}
	
	private void addInStart (int value) {
		if (!this.newStartList.contains(value)) {
			this.newStartList.add(value);
		}
	}
	
	private void addInStop (int value) {
		if (!this.newStopList.contains(value)) {
			this.newStopList.add(value);
		}
	}
	
	private void addPosition (Integer value) {
		if (!this.currentListOfPosition.contains(value)) {
			this.currentListOfPosition.add(value);
		}
	}
	
	private void addNode (OverLappingNode node) {
		if (!this.currentListOfNode.contains(node)) {
			this.currentListOfNode.add(node);
		}
	}
	
	
	////////////////////////////////////////////////	GETTERS

	protected IntArrayAsIntegerList getNewStartList() {
		return this.newStartList;
	}
	
	protected IntArrayAsIntegerList getNewStopList() {
		return this.newStopList;
	}
	
	protected List<Double> getNewScoreList() {
		return this.newScoresList;
	}
	
	protected List<ScoredChromosomeWindow> getNewList () {
		List<ScoredChromosomeWindow> list = new ArrayList<ScoredChromosomeWindow>();
		for (int i = 0; i < this.newStartList.size(); i++) {
			list.add(new ScoredChromosomeWindow(this.newStartList.get(i),
												this.newStopList.get(i),
												this.newScoresList.get(i)));
		}
		return list;
	}
	
}
