/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList;

import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;

/**
 * The interface ScoredChromosomeWindowListGenerator could be implemented by a class able to create a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface ScoredChromosomeWindowListGenerator {
	
	/**
	 * @return a new {@link ScoredChromosomeWindowList}
	 */
	public ScoredChromosomeWindowList toScoredChromosomeWindowList() throws ManagerDataNotLoadedException, InvalidChromosomeException;
}
