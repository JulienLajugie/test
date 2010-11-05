/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList;

import java.util.Comparator;

import yu.einstein.gdp2.core.Gene;

/**
 * A class to sort genes by their stop positions
 * @author Chirag
 * @version 0.1
 */
public class GeneListStopPositionComparator implements Comparator<Gene>{

	@Override
	public int compare(Gene first, Gene second) {
		if (first.getStop() > second.getStop()) {
			return 1;
		} else if (first.getStop() < second.getStop()) {
			return -1;
		} else {
			if (first.getStart() > second.getStart()) {
				return 1;
			} else if (first.getStart() < second.getStart()) {
				return -1;
			} else {
				return 0;
			}
		}		
	}
}
