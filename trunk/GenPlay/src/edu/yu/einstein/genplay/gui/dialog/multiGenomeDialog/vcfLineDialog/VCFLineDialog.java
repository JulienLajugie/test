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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class shows variant stripe information.
 * It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLineDialog extends JDialog implements MouseListener, ActionListener {

	private static final long serialVersionUID = -4932470485711131874L;

	private final static int WIDTH = 700;
	private final static int HEIGHT = 70;

	private final FontMetrics 	fm;			// the dialog font metrics
	private List<String> 		columns;	// list of the columns for the table
	private JTable				table;		// the table containing the vcf line
	private final JScrollPane 	pane;		// the scroll pane containing the table
	private final JPopupMenu 	menu;		// the popup menu object
	private final JMenuItem 	copyItem;	// the item of the popup menu to copy the line to the clipboard


	/**
	 * Constructor of {@link VCFLineDialog}
	 */
	public VCFLineDialog () {
		super(MainFrame.getInstance());

		// Gets the font metrics
		fm = getFontMetrics(getFont());

		// Initialize the table
		table = new JTable();
		table.addMouseListener(this);

		// Initialize the scroll pane
		pane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);

		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(pane, BorderLayout.CENTER);
		//add(pane);

		// Initialize the popup menu
		menu = new JPopupMenu();
		copyItem = new JMenuItem("Copy to clipboard");
		copyItem.addActionListener(this);
		menu.add(copyItem);

		// Sets dialog parameters
		setIconImages(Images.getApplicationImages());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Full VCF Line");
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(copyItem)) {
			Clipboard clipboard = Utils.getClipboard();
			clipboard.setContents(getSelection(), null);
		}
	}


	/**
	 * @param columnName
	 * @param data
	 * @return the max length between the column name and the data
	 */
	private int getMax (String columnName, Object data) {
		return Math.max(fm.stringWidth(columnName), fm.stringWidth(data.toString()));
	}


	/**
	 * Formats the table data and return the transferable string
	 * @return the string for the clipboard
	 */
	private StringSelection getSelection () {
		String line = "";
		for (int i = 0; i < table.getModel().getColumnCount(); i++) {
			line += table.getValueAt(0, i);
			if (i < (table.getModel().getColumnCount() - 1)) {
				line += "\t";
			}
		}
		return new StringSelection(line);
	}


	/**
	 * Initializes column header list.
	 */
	private void initColumnList (VCFLine line) {
		columns = new ArrayList<String>();
		columns.add(VCFColumnName.CHROM.toString());
		columns.add(VCFColumnName.POS.toString());
		columns.add(VCFColumnName.ID.toString());
		columns.add(VCFColumnName.REF.toString());
		columns.add(VCFColumnName.ALT.toString());
		columns.add(VCFColumnName.QUAL.toString());
		columns.add(VCFColumnName.FILTER.toString());
		columns.add(VCFColumnName.INFO.toString());
		columns.add(VCFColumnName.FORMAT.toString());
		List<String> genomeName = line.getGenomeIndexer().getGenomeRawNames();
		for (String name: genomeName) {
			columns.add(name);
		}
	}


	/**
	 * Creates the table containing the details
	 * @param information
	 */
	private void initTable (VCFLine line) {
		// Creates the data object
		Object[][] data = new Object[1][columns.size()];
		data[0][0] = line.getCHROM();
		data[0][1] = line.getPOS();
		data[0][2] = line.getID();
		data[0][3] = line.getREF();
		data[0][4] = line.getALT();
		data[0][5] = line.getQuality();
		data[0][6] = line.getFILTER();
		data[0][7] = line.getINFO();
		data[0][8] = line.getFORMAT();
		for (int i = 9; i < columns.size(); i++) {
			data[0][i] = line.getField(i);
		}

		// Creates the table
		VCFLineTableModel model = new VCFLineTableModel(columns.toArray(), data);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.addMouseListener(this);

		// Sets the column widths
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columns.size(); i++) {
			int width = (int)(getMax(columns.get(i), data[0][i]) * 1.5);
			columnModel.getColumn(i).setPreferredWidth(width);
			columnModel.getColumn(i).setResizable(false);
		}
	}

	/**
	 * Shows the popup menu if the event if valid
	 * @param e
	 */
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}


	@Override
	public void mousePressed(MouseEvent arg0) {
		maybeShowPopup(arg0);
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		maybeShowPopup(arg0);
	}


	/**
	 * Shows the dialog
	 * @param line information about the position
	 */
	public void show (Component parentComponent, VCFLine line) {
		// Initializes the table
		initColumnList(line);
		initTable(line);
		table.revalidate();
		pane.setViewportView(table);
		pane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		pack();

		// Show the dialog
		setLocationRelativeTo(parentComponent);
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setVisible(true);

	}

}
