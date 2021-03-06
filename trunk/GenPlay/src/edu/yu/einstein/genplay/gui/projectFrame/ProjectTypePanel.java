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
package edu.yu.einstein.genplay.gui.projectFrame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * This class gives radio button in order to choose between creating a new project or
 * loading an existing project.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
class ProjectTypePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 5891323545514431816L; //generated ID

	private JRadioButton 			newRadio;			// The radio button to choose a new project
	private JRadioButton 			loadRadio;			// The radio button to choose to load a project
	private ButtonGroup 			projectRadio;		// The button group


	/**
	 * Constructor of {@link ProjectTypePanel}
	 * @param projectScreenFrame the project screen object
	 */
	protected ProjectTypePanel() {
		super();
		init();
	}


	/**
	 * This method runs the screen project manager object method
	 * according to the selected radio button.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == newRadio) {
			ProjectFrame.getInstance().toNewScreenProject();
		} else if (arg0.getSource() == loadRadio) {
			ProjectFrame.getInstance().toLoadScreenProject();
		}
	}


	/**
	 * @return the loadRadio
	 */
	public JRadioButton getLoadRadio() {
		return loadRadio;
	}


	/**
	 * @return the newRadio
	 */
	public JRadioButton getNewRadio() {
		return newRadio;
	}


	/**
	 * Main method of the class.
	 * It initializes the {@link ProjectTypePanel} panel.
	 */
	private void init() {
		//Radio buttons
		newRadio = new JRadioButton("New project");
		newRadio.setSelected(true);
		loadRadio = new JRadioButton("Load an existing project");

		setOpaque(false);
		newRadio.setOpaque(false);
		loadRadio.setOpaque(false);

		//Listener
		newRadio.addActionListener(this);
		loadRadio.addActionListener(this);

		//Radio group
		projectRadio = new ButtonGroup();
		projectRadio.add(newRadio);
		projectRadio.add(loadRadio);

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (10, 0, 10, 10);

		//newRadio
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = gbcInsets;
		gbc.anchor = GridBagConstraints.CENTER;
		add(newRadio, gbc);

		//loadRadio
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbcInsets = new Insets (10, 50, 10, 0);
		gbc.insets = gbcInsets;
		add(loadRadio, gbc);
	}
}
