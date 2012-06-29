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

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.EditingTable;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FiltersTable extends EditingTable<FiltersData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3302281291755118456L;


	/**
	 * Constructor of {@link FiltersTable}
	 */
	public FiltersTable () {
		FiltersTableModel model = new FiltersTableModel();
		setModel(model);
	}


	@Override
	protected void setData(List<FiltersData> data) {
		List<FiltersData> newData = new ArrayList<FiltersData>();
		for (int i = 0; i < data.size(); i++) {
			FiltersData rowData = new FiltersData();
			rowData.setVCFFilter(data.get(i).getVCFFilter());
			rowData.setTrackList(data.get(i).getTrackList());
			newData.add(rowData);
		}
		((FiltersTableModel)getModel()).setData(newData);
	}


	@Override
	protected void updateColumnSize () {
		int columnNumber = ((FiltersTableModel)getModel()).getColumnCount();
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		String[] columnNames = ((FiltersTableModel)getModel()).getColumnNames();

		// Scan columns
		for (int i = 0; i < columnNumber; i++) {
			int currentWidth = fm.stringWidth(columnNames[i].toString()) + 10;

			for (FiltersData filtersData: getData()) {
				int width;
				switch (i) {
				case FiltersData.VCF_FILE_INDEX:
					width = fm.stringWidth(filtersData.getReaderForDisplay()) + 10;
					break;
				case FiltersData.ID_INDEX:
					width = fm.stringWidth(filtersData.getIDForDisplay()) + 10;
					break;
				case FiltersData.FILTER_INDEX:
					width = fm.stringWidth(filtersData.getFilterForDisplay()) + 10;
					break;
				case FiltersData.TRACK_INDEX:
					width = fm.stringWidth(filtersData.getTrackListForDisplay().toString()) + 10;
					break;
				default:
					width = 0;
					break;
				}

				// Sets column width
				if (width > currentWidth) {
					currentWidth = width;
				}
			}

			getColumnModel().getColumn(i).setPreferredWidth(currentWidth);
		}
	}

}
