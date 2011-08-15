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
package edu.yu.einstein.genplay.gui.projectFrame.loadProject;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * This class displays the 5 last projects recorded.
 * @author Nicolas Fourel
 */
class ProjectListPanel extends JPanel {
	
	private static final long serialVersionUID = 2899780798513668868L; //generated ID
	
	private static final 	String 					OTHER_NAME = "Other";	// Name for the field uses to load an existing projects
	private					LoadProjectPanel				loadProjectPanel;			// The load project object
	private 				GridBagConstraints 		gbc;					// Constraints for GridBagLayout
	private 				Map<JRadioButton, File> projectList;			// The list of existing project
	private 				ButtonGroup 			group;					// The button group
	private 				JRadioButton 			radioOther;				// The radio button for the field uses to load an existing projects
	private 				ProjectChooserPanel 			projectChooserPanel;			// The chooser file to choose an other project
	private 				JPanel 					fakeChooser;			// A panel for graphism synchronization

	
	/**
	 * Constructor of {@link ProjectListPanel}
	 * @param loadProjectPanel the load project object
	 * @param projectPath project paths array
	 */
	protected ProjectListPanel (LoadProjectPanel loadProjectPanel, String[] projectPath) {
		this.loadProjectPanel = loadProjectPanel;
		
		//Background
		setBackground(ProjectFrame.LOAD_COLOR);
		
		//Layout
		GridBagLayout grid = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = -1;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		setLayout(grid);
		
		//Radio buttons
		buildProjectList(projectPath);
		buildButtonOther();
		
		//Project chooser
		projectChooserPanel = new ProjectChooserPanel(this);
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 10);
		add(projectChooserPanel, gbc);
		
		//Fake chooser
		fakeChooser = new JPanel();
		fakeChooser.setPreferredSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		fakeChooser.setSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		fakeChooser.setMinimumSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		fakeChooser.setMaximumSize(ProjectFrame.PROJECT_CHOOSER_DIM);
		fakeChooser.setBackground(ProjectFrame.LOAD_COLOR);
		add(fakeChooser, gbc);
	}
	
	

	/**
	 * This method check wich radio button has been selected.
	 * If the user choose "other" without choose a folder, an error box dialog appears.
	 * @return the File object to load
	 */
	public File getFileProjectToLoad () {
		for (JRadioButton radio: projectList.keySet()) {
			if (radio.isSelected()) {
				if (projectList.get(radio) != null){
					return projectList.get(radio);
				} else {
					JOptionPane.showMessageDialog(getRootPane(), "Please select a project", "Invalid project", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		return null;
	}
	
	
	/**
	 * This method associates a File object to the radio button named "other"
	 * @param file	file choosen by the user
	 */
	protected void setButtonOther (File file) {
		projectList.put(radioOther, file);
		loadProjectPanel.showProjectInformation(file.getPath());
	}
	
	
	/**
	 * This method generate automatically every radio button in order to choose one of the last 5 projects.
	 * @param projectPath	list of the 5 last project paths
	 */
	private void buildProjectList (String[] projectPath) {
		projectList = new HashMap<JRadioButton, File>();
		group = new ButtonGroup();
		for (int i=0; i<5; i++) {
			final JRadioButton radio = new JRadioButton();
			
			//File
			if (projectPath[i] != null) {
				File file = new File(projectPath[i]);
				radio.setText(file.getName());
				radio.setName(file.getPath());
				radio.setToolTipText(file.getPath());
			} else {
				radio.setText("...");
			}
			
			//Radio
			radio.setPreferredSize(ProjectFrame.LINE_DIM);
			radio.setSize(ProjectFrame.LINE_DIM);
			radio.setMinimumSize(ProjectFrame.LINE_DIM);
			radio.setMaximumSize(ProjectFrame.LINE_DIM);
			radio.setBackground(ProjectFrame.LOAD_COLOR);
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (radio.getText() != "...") {
						loadProjectPanel.showProjectInformation(radio.getName());
					} else {
						loadProjectPanel.showProjectInformation(null);
					}
					projectChooserPanel.setVisible(false);
					fakeChooser.setVisible(true);
				}
			});
			
			if (i == 0) {
				radio.setSelected(true);
				if (radio.getText() != "...") {
					loadProjectPanel.showProjectInformation(radio.getName());
				} else {
					loadProjectPanel.showProjectInformation(null);
				}
			}
			
			//Group
			group.add(radio);
			
			addRadioToPanel(radio);
			
			//List
			if (projectPath[i] != null) {
				projectList.put(radio, new File(projectPath[i]));
			} else {
				projectList.put(radio, null);
			}
		}
	}
	
	
	/**
	 * This method creates the "other" radio button.
	 * It must be treated separately from others radio button.
	 * Listeners and label are different. 
	 */
	private void buildButtonOther () {
		//Button other
		radioOther = new JRadioButton();
		radioOther.setText(OTHER_NAME);
		radioOther.setPreferredSize(ProjectFrame.LINE_DIM);
		radioOther.setSize(ProjectFrame.LINE_DIM);
		radioOther.setMinimumSize(ProjectFrame.LINE_DIM);
		radioOther.setMaximumSize(ProjectFrame.LINE_DIM);
		radioOther.setBackground(ProjectFrame.LOAD_COLOR);
		radioOther.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadProjectPanel.showProjectInformation(null);
				fakeChooser.setVisible(false);
				projectChooserPanel.setVisible(true);
			}
		});
		
		//Group
		group.add(radioOther);
		
		addRadioToPanel(radioOther);
		
		//List
		projectList.put(radioOther, null);
	}
	
	
	/**
	 * This method adds radio button to the panel.
	 * Radio button is added below the previous one. 
	 * @param radio	the radio button to add
	 */
	private void addRadioToPanel (JRadioButton radio) {
		gbc.gridy++;
		add(radio, gbc);
	}
	
}