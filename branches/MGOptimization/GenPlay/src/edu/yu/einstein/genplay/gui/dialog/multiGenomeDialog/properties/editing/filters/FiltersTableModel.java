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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.table.EditingTableModel;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FiltersTableModel extends EditingTableModel<FiltersData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;
	

	/**
	 * Constructor of {@link FiltersTableModel}
	 */
	protected FiltersTableModel () {
		super(new String[]{"File", "ID", "Filter", "Track"});
	}


	@Override
	public Object getValueAt(int row, int col) {
		if (col == buttonColumnIndex) {
			return buttons.get(row);
		}
		FiltersData filtersData = data.get(row);
		switch (col) {
		case FiltersData.VCF_FILE_INDEX:
			return filtersData.getReaderForDisplay();
		case FiltersData.ID_INDEX:
			return filtersData.getIDForDisplay();
		case FiltersData.FILTER_INDEX:
			return filtersData.getFilterForDisplay();
		case FiltersData.TRACK_INDEX:
			return filtersData.getTrackListForDisplay();
		default:
			return new Object();
		}
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case FiltersData.VCF_FILE_INDEX:
			return String.class;
		case FiltersData.ID_INDEX:
			return String.class;
		case FiltersData.FILTER_INDEX:
			return String.class;
		case FiltersData.TRACK_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}


	/**
	 * @return the data
	 */
	protected List<FiltersData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<FiltersData> data) {
		this.data = data;
		buttons = new ArrayList<JButton>();
		for (int row = 0; row <data.size(); row++) {
			buttons.add(getNewButton());
			fireTableCellUpdated(row, FiltersData.VCF_FILE_INDEX);
			fireTableCellUpdated(row, FiltersData.ID_INDEX);
			fireTableCellUpdated(row, FiltersData.FILTER_INDEX);
			fireTableCellUpdated(row, FiltersData.TRACK_INDEX);
			fireTableCellUpdated(row, buttonColumnIndex);
		}
	}

}