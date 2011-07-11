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
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;


/**
 * Manage the overlapping engine
 * Provides news lists for chromosome:
 * 	- start positions
 * 	- stop positions
 * 	- scores
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class OverLappingManagement implements Serializable {
	
	private static final long serialVersionUID = 419831643761204027L;
	protected final ChromosomeManager 			chromosomeManager;		// ChromosomeManager
	private 		SCWLOptions 				sortSCW;				//use the sort option for chromosome list
	private 		List<OverLappingEngine> 	overLappingEngineList;	//overlapping engine for chromosome list
	
	/**
	 * OverLapManagement constructor
	 * 
	 * @param startList		list of start position
	 * @param stopList		list of stop position
	 * @param scoreList		list of score
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public OverLappingManagement (	ChromosomeListOfLists<Integer> startList,
								ChromosomeListOfLists<Integer> stopList,
								ChromosomeListOfLists<Double> scoreList) throws InterruptedException, ExecutionException {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.sortSCW = new SCWLOptions(startList, stopList, scoreList);
		this.sortSCW.sortAll();
	}
	
	
	////////////////////////////////////////////////	OverLapping running methods
	
	/**
	 * run method
	 * This method allow to run the overlapping engine for a specific chromosome
	 * @param chromosome	Chromosome
	 */
	public void run (Chromosome chromosome) throws InterruptedException, ExecutionException {
		this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).init(this.sortSCW.getList().get(chromosome));	//the overlapengine is ran for the chromosome list
		this.sortSCW.setNewList(chromosome, getNewStartList(chromosome), getNewStopList(chromosome), getNewScoreList(chromosome));	//the old chromosome list is replaced by the new one
	}
	
	
	////////////////////////////////////////////////	GETTERS & SETTERS

	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.sortSCW.getList(chromosome);
	}
	
	private IntArrayAsIntegerList getNewStartList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStartList();
	}
	
	private IntArrayAsIntegerList getNewStopList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewStopList();
	}
	
	private List<Double> getNewScoreList(Chromosome chromosome) {
		return this.overLappingEngineList.get(chromosomeManager.getIndex(chromosome)).getNewScoreList();
	}
	
	public void setScoreCalculationMethod (ScoreCalculationMethod scm) {
		this.overLappingEngineList = new ArrayList<OverLappingEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.overLappingEngineList.add(new OverLappingEngine(scm));
		}
	}
}