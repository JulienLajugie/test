/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Divides the scores of each window of a {@link ScoredChromosomeWindow} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLODivideConstant implements Operation<ScoredChromosomeWindowList> {

	private final ScoredChromosomeWindowList 	scwList;	// input list
	private final double 						constant;	// constant of the division
	
	
	/**
	 * Divides the scores of each window of a {@link ScoredChromosomeWindow} by a constant
	 * @param scwList input list
	 * @param constant constant to add
	 */
	public SCWLODivideConstant(ScoredChromosomeWindowList scwList, double constant) {
		this.scwList = scwList;
		this.constant = constant;
	}
	
	
	@Override
	public ScoredChromosomeWindowList compute() throws Exception {
		if (constant == 1) {
			return scwList.deepClone();
		} else if (constant == 0) {
			throw new ArithmeticException("Division By Zero");
		}
		
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < scwList.size(); i++) {
			final List<ScoredChromosomeWindow> currentList = scwList.get(i);
			
			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {	
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = new ArrayList<ScoredChromosomeWindow>();
						// We divide each element by a constant
						for (ScoredChromosomeWindow currentWindow: currentList) {
							ScoredChromosomeWindow resultWindow = new ScoredChromosomeWindow(currentWindow);
							resultWindow.setScore(currentWindow.getScore() / constant);
							resultList.add(resultWindow);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			ScoredChromosomeWindowList resultList = new ScoredChromosomeWindowList(result);
			return resultList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getDescription() {
		return "Operation: Divide by Constant, Constant = " + constant;
	}


	@Override
	public String getProcessingDescription() {
		return "Dividing by Constant";
	}

	
	@Override
	public int getStepCount() {
		return 3;
	}
}
