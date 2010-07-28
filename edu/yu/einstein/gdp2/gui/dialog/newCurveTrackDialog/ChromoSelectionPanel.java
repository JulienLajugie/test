/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;


/**
 * Panel of a {@link NewCurveTrackDialog} for the chromosome selection input
 * @author Julien Lajugie
 * @version 0.1
 */
class ChromoSelectionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8940594564630580785L; // generated ID
	private final ChromosomeManager cm; 					// chromosome manager
	private final JList 			jlSelectedChromo;		// list showing the selected chromosomes
	private final JScrollPane 		jcpSelectedChromo;		// scroll pane containing the list
	private final JButton 			jbModifySelection;		// button to modify the selection
	private static boolean[]		defaultSelection = null;// default selected chromosome

	/**
	 * Creates an instance of {@link ChromoSelectionPanel}
	 */
	ChromoSelectionPanel() {
		super();
		jlSelectedChromo = new JList(new DefaultListModel());
		cm = ChromosomeManager.getInstance();
		for (int i = 0; i < cm.size(); i++) {
			if ((defaultSelection == null) || (defaultSelection[i])) {
				((DefaultListModel) jlSelectedChromo.getModel()).addElement(cm.get(i));
			}
		}
		jcpSelectedChromo = new JScrollPane(jlSelectedChromo);
		jbModifySelection = new JButton("Modify Selection");
		jbModifySelection.addActionListener(this);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(jcpSelectedChromo);
		add(jbModifySelection);
		setBorder(BorderFactory.createTitledBorder("Selected Chromosomes"));
	}


	/**
	 * Shows a windows where the user can select the chromosomes 
	 * when the "modify selection" button is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean[] newSelectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), getSelectedChromosomes());
		if (newSelectedChromo != null) {
			if (noChromoSelected(newSelectedChromo)) {
				JOptionPane.showMessageDialog(getRootPane(), "You must select at least one chromosome", "Nothing Selected", JOptionPane.WARNING_MESSAGE);
			} else {
				DefaultListModel lm = (DefaultListModel) jlSelectedChromo.getModel();
				lm.removeAllElements();
				for (int i = 0; i < cm.size(); i++) {
					if (newSelectedChromo[i]) {
						lm.addElement(cm.get(i));
					}
				}
			}
		}
	}

	/**
	 * @return the chromosomes selected
	 */
	boolean[] getSelectedChromosomes() {
		boolean[] selectedChromo = new boolean[cm.size()];
		for (int i = 0; i < selectedChromo.length; i++) {
			selectedChromo[i] = isSelected(cm.get(i));
		}
		return selectedChromo;
	}


	/**
	 * @param chromo a chromosome
	 * @return true if the specified chromosome had been selected. Otherwise return false
	 */
	private boolean isSelected(Chromosome chromo) {
		ListModel lm = jlSelectedChromo.getModel();
		for (int i = 0; i < lm.getSize(); i++) {
			if (chromo.equals(lm.getElementAt(i))) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @param chromoArray an array of boolean representing the selection state of chromosomes
	 * @return true if a no chromosomes is selected. False otherwise
	 */
	private boolean noChromoSelected(boolean[] chromoArray) {
		for (boolean chromo: chromoArray) {
			if (chromo) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Saves the selected chromsomes as default
	 */
	void saveDefault() {
		defaultSelection = getSelectedChromosomes();
	}
}
