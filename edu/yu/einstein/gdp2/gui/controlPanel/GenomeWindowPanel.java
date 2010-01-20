/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.gui.event.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.GenomeWindowListener;
import yu.einstein.gdp2.gui.event.GenomeWindowModifier;
import yu.einstein.gdp2.util.ChromosomeManager;

/**
 * The GenomeWindowPanel part of the {@link ControlPanel} 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenomeWindowPanel extends JPanel implements GenomeWindowModifier {

	private static final long serialVersionUID = 8279801687428218652L;  // generated ID
	private final JTextField 						jftGenomeWindow;	// text field for the GenomeWindow
	private final JButton 							jbJump;				// button jump to position
	private final ChromosomeManager 				chrManager;			// ChromosomeManager
	private final ArrayList<GenomeWindowListener> 	listenerList;		// list of GenomeWindowListener
	private GenomeWindow 							currentGenomeWindow;// current GenomeWindow
	

	/**
	 * Creates an instance of {@link GenomeWindowPanel}
	 * @param chromosomeManager a {@link ChromosomeManager}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	public GenomeWindowPanel(ChromosomeManager chromosomeManager, GenomeWindow genomeWindow) {
		this.currentGenomeWindow = genomeWindow;
		this.chrManager = chromosomeManager;
		this.listenerList = new ArrayList<GenomeWindowListener>();
		jftGenomeWindow = new JTextField(20);
		jftGenomeWindow.setText(genomeWindow.toString());
		jftGenomeWindow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					genomeWindowChanged();
				}
			}
		});
		
		jbJump = new JButton("jump");
		jbJump.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				genomeWindowChanged();				
			}
		});

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jftGenomeWindow, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jbJump, gbc);
	}


	/**
	 * Sets the current {@link GenomeWindow}
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(currentGenomeWindow)) {
			GenomeWindow oldGenomeWindow = currentGenomeWindow;
			currentGenomeWindow = newGenomeWindow;
			// we notify the gui
			jftGenomeWindow.setText(currentGenomeWindow.toString());
			genomeWindowChanged();
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, currentGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}


	/**
	 * Called when the current {@link GenomeWindow} changes
	 */
	protected void genomeWindowChanged() {
		try {
			GenomeWindow newGenomeWindow = new GenomeWindow(jftGenomeWindow.getText(), chrManager);
			if (!newGenomeWindow.equals(currentGenomeWindow)) {
				int middlePosition = (int)newGenomeWindow.getMiddlePosition();
				if ((middlePosition < 0) || (middlePosition > newGenomeWindow.getChromosome().getLength())) {
					JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
				} else {
					setGenomeWindow(newGenomeWindow);
				}
			}			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
			jftGenomeWindow.setText(currentGenomeWindow.toString());
			e.printStackTrace();
		}		
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);		
	}	
}
