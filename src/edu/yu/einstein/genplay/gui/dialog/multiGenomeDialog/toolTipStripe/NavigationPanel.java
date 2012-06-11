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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NavigationPanel extends JPanel{

	
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 793779650948801264L;
	
	private static final int WIDTH = 230;	// width of the panel
	private static final int HEIGHT = 30;	// height of the panel
	
	private ToolTipStripeDialog origin;		// tooltipstripe object to aware it of any changes.
	private JButton details;				// button to show the full line
	private JButton previous;				// the previous button (move backward)
	private JButton next;					// the next button (move forward)
	
	
	/**
	 * Constructor of {@link NavigationPanel}
	 */
	protected NavigationPanel (ToolTipStripeDialog origin) {
		this.origin = origin;
		
		Dimension paneDim = new Dimension(WIDTH, HEIGHT);
		setSize(paneDim);
		setMinimumSize(paneDim);
		setMaximumSize(paneDim);
		setPreferredSize(paneDim);

		Insets inset = new Insets(0, 0, 0, 0);
		
		details = new JButton("Details");
		details.setToolTipText("See the whole VCF line.");
		details.setMargin(inset);
		details.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getOrigin().showVCFLine();
			}
		});

		next = new JButton(getIcon(Images.getNextImage()));
		next.setContentAreaFilled(false);
		next.setToolTipText("Next variant on the track");
		next.setMargin(inset);
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToNextVariant();
				next.setEnabled(enable);
			}
		});

		previous = new JButton(getIcon(Images.getPreviousImage()));
		previous.setContentAreaFilled(false);
		previous.setToolTipText("Previous variant on the track");
		previous.setMargin(inset);
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToPreviousVariant();
				previous.setEnabled(enable);
			}
		});

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		gbc.insets = inset;
		gbc.weighty = 1;
		gbc.gridy = 0;

		// Add the "previous" button
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 0;
		add(previous, gbc);
		
		// Add the "details" button
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx++;
		gbc.weightx = 1;
		add(details, gbc);
		
		// Add the "next" button
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx++;
		gbc.weightx = 0;
		add(next, gbc);
	}

	
	/**
	 * @return the {@link ToolTipStripeDialog} object that requested the {@link NavigationPanel}
	 */
	private ToolTipStripeDialog getOrigin () {
		return origin;
	}
	
	
	/**
	 * Creates a square icon using the given path 
	 * @param path	icon path
	 * @param side	size of the side
	 * @return		the icon
	 */
	private ImageIcon getIcon (Image image) {
		Image newImg = image.getScaledInstance(WIDTH / 4, HEIGHT, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(newImg);
		return icon;
	}
	
	
	/**
	 * @return the height of the panel
	 */
	protected static int getPanelHeight () {
		return NavigationPanel.HEIGHT;
	}
	
	
	/**
	 * Enable the detail button
	 * @param activate true if the button has to be enabled, false otherwise
	 */
	public void setEnableDetail (boolean activate) {
		details.setEnabled(activate);
	}

}
