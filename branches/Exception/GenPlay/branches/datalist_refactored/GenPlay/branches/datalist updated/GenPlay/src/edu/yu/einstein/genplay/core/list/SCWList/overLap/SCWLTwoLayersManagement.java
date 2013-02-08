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
package edu.yu.einstein.genplay.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoLayersMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class manages theses operations on two layers:
 * 	- addition
 * 	- subtraction
 * 	- multiplication
 * 	- division
 * 
 * @author Nicolas
 * @version 0.1
 */
public class SCWLTwoLayersManagement implements Serializable, Stoppable {

	private static final long serialVersionUID = -4066526880193456101L;
	protected 	final 	ProjectChromosome 				projectChromosome;	//ChromosomeManager
	private 	final 	List<ChromosomeListOfLists<?>> 			scwList;		//list containing originals lists
	private 	final 	List<SCWLTwoLayersEngine>			twoLayersEngineList;

	/**
	 * SCWLTwoLayers constructor
	 * 
	 * @param list1	first layer
	 * @param list2	second layer
	 * @param scm		operation
	 */
	public SCWLTwoLayersManagement (	ChromosomeListOfLists<?> list1,
			ChromosomeListOfLists<?> list2,
			ScoreCalculationTwoLayersMethod scm) {
		this.projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.scwList = new ArrayList<ChromosomeListOfLists<?>>();
		this.scwList.add(list1);
		this.scwList.add(list2);
		this.twoLayersEngineList = new ArrayList<SCWLTwoLayersEngine>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			this.twoLayersEngineList.add(new SCWLTwoLayersEngine(scm));
		}
	}

	/**
	 * Runs the two layers overlap process
	 * @param chromosome the chromosome
	 */
	public void run(Chromosome chromosome) {
		this.twoLayersEngineList.get(projectChromosome.getIndex(chromosome)).init(scwList.get(0), scwList.get(1), chromosome);
	}

	/**
	 * @param chromosome the chromosome
	 * @return the new list of scored chromosome window
	 */
	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.twoLayersEngineList.get(projectChromosome.getIndex(chromosome)).getList();
	}

	@Override
	public void stop() {
		if (twoLayersEngineList != null) {
			for (SCWLTwoLayersEngine currentEngine: twoLayersEngineList) {
				currentEngine.stop();
			}
		}
	}

}