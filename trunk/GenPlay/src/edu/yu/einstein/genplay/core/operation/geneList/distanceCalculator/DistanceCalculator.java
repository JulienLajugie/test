/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.operation.geneList.distanceCalculator;

import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Computes the distance between closest genes from two {@link GeneList}
 * The position to be used as a reference (ie: start / middle / stop of a gene)
 * must be specified by the user.
 * @author Chirag Gorasia
 */
public class DistanceCalculator {
	private final int strandDirection;
	private int relAbs = -1;
	private final int track1Position;
	private final int track2Position;
	private final int refValue;
	private final GeneList geneList;
	private final int chromosomeindex;


	/**
	 * Creates instance of {@link DistanceCalculator}
	 * @param geneList
	 * @param chromosomeindex
	 * @param strandDirection
	 * @param track1Position
	 * @param track2Position
	 * @param refValue
	 */
	public DistanceCalculator(GeneList geneList, int chromosomeindex, int strandDirection, int track1Position, int track2Position, int refValue) {
		this.geneList = geneList;
		this.chromosomeindex = chromosomeindex;
		this.strandDirection = strandDirection;
		this.track1Position = track1Position;
		this.track2Position = track2Position;
		this.refValue = refValue;
	}


	/**
	 * Creates instance of {@link DistanceCalculator}
	 * @param geneList
	 * @param chromosomeindex
	 * @param strandDirection
	 * @param relAbs
	 * @param track1Position
	 * @param track2Position
	 * @param refValue
	 */
	public DistanceCalculator(GeneList geneList, int chromosomeindex, int strandDirection, int relAbs, int track1Position, int track2Position, int refValue) {
		this.geneList = geneList;
		this.chromosomeindex = chromosomeindex;
		this.strandDirection = strandDirection;
		this.relAbs = relAbs;
		this.track1Position = track1Position;
		this.track2Position = track2Position;
		this.refValue = refValue;
	}


	/**
	 * @return the closest distance
	 */
	public long getClosestDistance() {
		ListView<Gene> listOfGenes = geneList.get(chromosomeindex);
		long retValue = 0;
		switch(strandDirection) {
		case 0: //positive
			switch(track1Position) {
			case 0: //start for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStartStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStartMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStartStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 1:	//middle for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveMiddleStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveMiddleMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveMiddleStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 2:	//stop for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStopStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStopMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistancePositiveStopStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;
			}
			break;

		case 1: //negative
			switch(track1Position) {
			case 0: //start for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStartStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStartMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStartStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 1:	//middle for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeMiddleStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeMiddleMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeMiddleStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 2:	//stop for track 1
				switch(track2Position) {
				case 0: //start for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStopStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStopMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					//listOfGenes = geneList.get(chromosomeindex);
					retValue = getDistanceNegativeStopStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;
			}
			break;

		case 2: //both
			switch(relAbs) {
			case 0: switch(track1Position) {
			case 0: //start for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceRelativeStartStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceRelativeStartMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceRelativeStartStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 1:	//middle for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceRelativeMiddleStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceRelativeMiddleMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceRelativeMiddleStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 2:	//stop for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceRelativeStopStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceRelativeStopMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceRelativeStopStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;
			}
			break;

			case 1: switch(track1Position) {
			case 0: //start for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceAbsoluteStartStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceAbsoluteStartMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceAbsoluteStartStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 1:	//middle for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceAbsoluteMiddleStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceAbsoluteMiddleMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceAbsoluteMiddleStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;

			case 2:	//stop for track 1
				switch(track2Position) {
				case 0: //start for track 2
					getDistanceAbsoluteStopStart(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 1: //middle for track 2
					getDistanceAbsoluteStopMiddle(listOfGenes,0,listOfGenes.size()-1);
					break;

				case 2: //stop for track 2
					getDistanceAbsoluteStopStop(listOfGenes,0,listOfGenes.size()-1);
					break;
				}
				break;
			}
			break;
			}
			break;
		}
		return retValue;
	}


	private int getDistanceAbsoluteMiddleMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteMiddleStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteMiddleStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStartMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStartStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStartStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStopMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStopStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	private int getDistanceAbsoluteStopStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return inferior;
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position inferior to value if nothing found.
	 */
	private int getDistanceNegativeMiddleMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (int) (refValue - listOfGenes.get(indexStart).getMiddlePosition());
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistanceNegativeMiddleMiddle(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeMiddleMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position inferior to value if nothing found.
	 */
	private int getDistanceNegativeMiddleStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStart());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistanceNegativeMiddleStart(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeMiddleStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position inferior to value if nothing found.
	 */
	private int getDistanceNegativeMiddleStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStop());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistanceNegativeMiddleStop(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeMiddleStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStartMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (int) (refValue - listOfGenes.get(indexStart).getMiddlePosition());
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistanceNegativeStartMiddle(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStartMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStartStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStart());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistanceNegativeStartStart(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStartStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStartStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStop());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistanceNegativeStartStop(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStartStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStopMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (int) (refValue - listOfGenes.get(indexStart).getMiddlePosition());
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistanceNegativeStopMiddle(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStopMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStopStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStart());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistanceNegativeStopStart(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStopStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position inferior to value if nothing found.
	 */
	private int getDistanceNegativeStopStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (middle == 0) {
			return (refValue - listOfGenes.get(indexStart).getStop());
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistanceNegativeStopStop(listOfGenes, indexStart + middle, indexStop);
		} else {
			return getDistanceNegativeStopStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position superior to value if nothing found.
	 */
	private int getDistancePositiveMiddleMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (int) (listOfGenes.get(indexStart).getMiddlePosition() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistancePositiveMiddleMiddle(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveMiddleMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int getDistancePositiveMiddleStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistancePositiveMiddleStart(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveMiddleStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position superior to value if nothing found.
	 */
	private int getDistancePositiveMiddleStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (listOfGenes.get(indexStart).getStop() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistancePositiveMiddleStop(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveMiddleStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position superior to value if nothing found.
	 */
	private int getDistancePositiveStartMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (int) (listOfGenes.get(indexStart).getMiddlePosition() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistancePositiveStartMiddle(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStartMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int getDistancePositiveStartStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (listOfGenes.get(indexStart).getStart() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistancePositiveStartStart(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStartStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position superior to value if nothing found.
	 */
	private int getDistancePositiveStartStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (listOfGenes.get(indexStart).getStop() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistancePositiveStartStop(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStartStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position middle equals to value.
	 * Index of the first gene with a middle position superior to value if nothing found.
	 */
	private int getDistancePositiveStopMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (int) (listOfGenes.get(indexStart).getMiddlePosition() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getMiddlePosition()) {
			return getDistancePositiveStopMiddle(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStopMiddle(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value.
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int getDistancePositiveStopStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (listOfGenes.get(indexStart).getStart() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStart()) {
			return getDistancePositiveStopStart(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStopStart(listOfGenes, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.
	 * @param listOfGenes List in which the search is performed.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position Stop equals to value.
	 * Index of the first gene with a Stop position superior to value if nothing found.
	 */
	private int getDistancePositiveStopStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return (listOfGenes.get(indexStart).getStop() - refValue);
		} else if (refValue == listOfGenes.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (refValue > listOfGenes.get(indexStart + middle).getStop()) {
			return getDistancePositiveStopStop(listOfGenes, indexStart + middle + 1, indexStop);
		} else {
			return getDistancePositiveStopStop(listOfGenes, indexStart, indexStart + middle);
		}
	}


	private int getDistanceRelativeMiddleMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeMiddleStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeMiddleStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveMiddleStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeMiddleStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStartMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStartStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStartStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStartStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStartStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStopMiddle(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopMiddle(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopMiddle(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStopStart(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopStart(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopStart(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}


	private int getDistanceRelativeStopStop(ListView<Gene> listOfGenes, int indexStart, int indexStop) {
		int superior = getDistancePositiveStopStop(listOfGenes, indexStart, indexStop);
		int inferior = getDistanceNegativeStopStop(listOfGenes, indexStart, indexStop);
		if (superior < inferior) {
			return superior;
		} return (-inferior);
	}
}
