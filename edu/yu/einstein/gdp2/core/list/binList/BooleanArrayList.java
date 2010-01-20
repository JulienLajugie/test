/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;


/**
 * An array of booleans encapsulated in order to implement the {@link List} interface with Double parameter
 * It means that the methods get and set work with Double objects
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BooleanArrayList extends AbstractList<Double> implements Serializable, List<Double> {

	private static final long serialVersionUID = -5280328695672981245L;	// generated ID
	private boolean[] 	data;	// boolean data array
	
	
	/**
	 * Creates an instance of {@link DataArrayList}
	 * @param size size of the array
	 */
	public BooleanArrayList(int size) {
		data = new boolean[size];
	}
	

	public void sort() {
		throw (new UnsupportedOperationException("Invalid operation, can't sort a boolean array"));
	};

	
	@Override
	public Double get(int index) {
		return (data[index] ? 1d : 0d);
	}

	
	/**
 	 * @return null in order to accelerate the operation
	 */
	@Override
	public Double set(int index, Double element) {
		data[index] = (element != 0);
		return null;
	}

	
	@Override
	public int size() {
		return data.length;
	}
}
