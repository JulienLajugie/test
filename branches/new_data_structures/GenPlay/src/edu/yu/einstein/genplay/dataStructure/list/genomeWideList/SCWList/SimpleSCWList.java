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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOComputeStats;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOCountNonNullLength;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Class implementing the {@link ScoredChromosomeWindowList} interface using an {@link ArrayList} based data structure
 * @author Julien Lajugie
 * @version 0.1
 */
public class SimpleSCWList implements ScoredChromosomeWindowList {

	/** Generated serial ID */
	private static final long serialVersionUID = 9159412940141151387L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/**
	 * @param scwListType a {@link SCWListType}
	 * @return the number of steps needed to create the list.
	 */
	public static int getCreationStepCount(SCWListType scwListType) {
		switch (scwListType) {
		case GENERIC:
			return 5;
		case MASK:
			return 2;
		default:
			return 0;
		}
	}

	/** {@link GenomicDataArrayList} containing the ScoredChromosomeWindows */
	private final List<ListView<ScoredChromosomeWindow>> data;

	/** Type of the list */
	private final SCWListType scwListType;

	/** Smallest value of the list */
	private double minimum;

	/** Greatest value of the list */
	private double maximum;

	/** Average of the list */
	private double average;

	/** Standard deviation of the list */
	private double standardDeviation;

	/** Sum of the scores of all windows */
	private double scoreSum;

	/** Count of none-null bins in the BinList */
	private long nonNullLength;


	/**
	 * Creates an instance of {@link SimpleGeneList}
	 * @param data {@link ScoredChromosomeWindow} list organized by chromosome
	 * @param scwListType type of the list (as a {@link SCWListType} element)
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public SimpleSCWList(List<ListView<ScoredChromosomeWindow>> data, SCWListType scwListType) throws InterruptedException, ExecutionException {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.data = new ArrayList<ListView<ScoredChromosomeWindow>>(projectChromosome.size());
		for (int i = 0; i < data.size(); i++){
			data.add(data.get(i));
		}
		this.scwListType = scwListType;
		computeStatistics();
	}


	/**
	 * Computes some statistic values for this list
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void computeStatistics() throws InterruptedException, ExecutionException {
		if (scwListType == SCWListType.MASK) {
			maximum = 1d;
			minimum = 1d;
			average = 1d;
			standardDeviation = 0d;
			nonNullLength = new SCWLOCountNonNullLength(this, null).compute();
			scoreSum = nonNullLength;
		} else {
			SCWLOComputeStats operation = new SCWLOComputeStats(this);
			operation.compute();
			maximum = operation.getMaximum();
			minimum = operation.getMinimum();
			average = operation.getAverage();
			standardDeviation = operation.getStandardDeviation();
			nonNullLength = operation.getNonNullLength();
			scoreSum = operation.getScoreSum();
		}
	}


	@Override
	public ListView<ScoredChromosomeWindow> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public ScoredChromosomeWindow get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return get(chromosome).get(index);
	}


	@Override
	public ListView<ScoredChromosomeWindow> get(int elementIndex) {
		return 	data.get(elementIndex);
	}


	@Override
	public ScoredChromosomeWindow get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public double getAverage() {
		return average;
	}


	@Override
	public double getMaximum() {
		return maximum;
	}


	@Override
	public double getMinimum() {
		return minimum;
	}


	@Override
	public long getNonNullLength() {
		return nonNullLength;
	}


	@Override
	public double getScore(Chromosome chromosome, int position) {
		ListView<ScoredChromosomeWindow> currentList = get(chromosome);
		int indexWindow = Collections.binarySearch(currentList, new SimpleChromosomeWindow(position, position), new ChromosomeWindowStartComparator());
		if (indexWindow < 0) {
			// retrieve the window right before the insert point
			indexWindow = -indexWindow - 2;
			if (indexWindow < 0) {
				return 0;
			}
		}
		// check if the window contains the stop position
		if (currentList.get(indexWindow).getStop() >= position) {
			return currentList.get(indexWindow).getScore();
		}
		return 0;
	}


	@Override
	public double getScoreSum() {
		return scoreSum;
	}


	@Override
	public SCWListType getSCWListType() {
		return scwListType;
	}


	@Override
	public double getStandardDeviation() {
		return standardDeviation;
	}


	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}


	@Override
	public Iterator<ListView<ScoredChromosomeWindow>> iterator() {
		return data.iterator();
	}


	/**
	 * Method used for unserialization. Computes the statistics of the list after unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (GenomicListView<ScoredChromosomeWindow>) in.readObject();
		scwListType = (SCWListType) in.readObject();
		maximum = in.readDouble();
		minimum = in.readDouble();
		average = in.readDouble();
		standardDeviation = in.readDouble();
		nonNullLength = in.readLong();
		scoreSum = in.readDouble();
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return get(chromosome).size();
	}


	@Override
	public int size(int chromosomeIndex) {
		return get(chromosomeIndex).size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
		out.writeObject(scwListType);
		out.writeDouble(maximum);
		out.writeDouble(minimum);
		out.writeDouble(average);
		out.writeDouble(standardDeviation);
		out.writeLong(nonNullLength);
		out.writeDouble(scoreSum);
	}
}
