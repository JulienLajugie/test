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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenFrame;

/**
 * This class allows users to choose a simple genome project
 * or a multi genome project.
 * @author Nicolas Fourel
 * @version 0.1
 */
class GenomeProjectTypePanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -5329054202308798840L;
	
	private JRadioButton 	simpleRadio;	// Radio button for a simple genomic project
	private JRadioButton 	multiRadio;		// Radio button for a multi genomic project
	private ButtonGroup 	genomeRadio;	// Radio group
	
	
	/**
	 * Constructor of {@link GenomeProjectTypePanel}
	 */
	protected GenomeProjectTypePanel () {
		//Size
		setSize(ProjectScreenFrame.getGenomeDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		
		//Radio buttons
		simpleRadio = new JRadioButton("Simple Genome Project");
		simpleRadio.setSelected(true);
		multiRadio = new JRadioButton("Multi Genome Project");
		
		//Color
		setBackground(ProjectScreenFrame.getGenomeColor());
		simpleRadio.setBackground(ProjectScreenFrame.getGenomeColor());
		multiRadio.setBackground(ProjectScreenFrame.getGenomeColor());
		
		//Listener
		simpleRadio.addActionListener(this);
		multiRadio.addActionListener(this);
		
		//Radio group
		genomeRadio = new ButtonGroup();
		genomeRadio.add(simpleRadio);
		genomeRadio.add(multiRadio);
		
		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (5, 77, 5, 0);
		
		//newRadio
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = gbcInsets;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		add(simpleRadio, gbc);
		
		//loadRadio
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = gbcInsets;
		add(multiRadio, gbc);
	}

	
	/**
	 * This method displays or hide the loading var files panel.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == simpleRadio) {
			NewProjectPanel.hideVarTable();
		} else {
			NewProjectPanel.showVarTable();
		}
	}
	
	
	/**
	 * This method determines if user chose a simple or a multi genome project. 
	 * @return true if user chose a simple genome project.
	 */
	protected boolean isSimpleProject () {
		if (simpleRadio.isSelected()) {
			return true;
		} else {
			return false;
		}
	}
	
}