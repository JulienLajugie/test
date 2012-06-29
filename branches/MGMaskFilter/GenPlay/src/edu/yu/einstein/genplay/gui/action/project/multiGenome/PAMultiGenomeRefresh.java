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
package edu.yu.einstein.genplay.gui.action.project.multiGenome;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Updates the tracks after important changes as:
 * - validating the multi genome properties dialog
 * - changing chromosome
 * 
 * The updates consist in managing in the following order:
 * - the SNP (if required)
 * - the filters (if required)
 * - the track display
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAMultiGenomeRefresh extends TrackListAction {

	private static final 	long serialVersionUID = -6475180772964541278L; 			// generated ID
	private static final 	String ACTION_NAME = "Multi Genome Properties";			// action name
	private static final 	String DESCRIPTION = "Shows the project properties"; 	// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key
	private List<VCFFilter> previousFilterList;

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMultiGenomeProperties";


	/**
	 * Creates an instance of {@link PAMultiGenomeRefresh}
	 */
	public PAMultiGenomeRefresh() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		previousFilterList = new ArrayList<VCFFilter>();
	}


	/**
	 * Shows the Multi Genome Project Properties dialog
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {		// if it is a multi genome project
			UpdateThread thread = new UpdateThread();					// if it is approved (OK button), we create the thread running the content of a DoAtTheEnd-like method
			thread.start();												// we start the thread
		}
	}


	/**
	 * @return the previousFilterList
	 */
	public List<VCFFilter> getPreviousFilterList() {
		return previousFilterList;
	}


	/**
	 * @param previousFilterList the previousFilterList to set
	 */
	public void setPreviousFilterList(List<VCFFilter> previousFilterList) {
		this.previousFilterList = previousFilterList;
	}



	/////////////////////////////////////////////////////////////////////// DoAtTheEndThread class

	/**
	 * Thread running the content of the DoAtTheEnd-like method.
	 * That method is running two other actions {@link PAMultiGenomeSNP} and {@link PAMultiGenomeFilters}
	 * supposed to be ran one by one, consecutively. One waiting for the other to stop in order to start.
	 * These two actions are ran into separated thread.
	 * The {@link UpdateThread} handle these waiting breaks but must not stop the main thread of the application.
	 * That is why the content of the DoAtTheEnd-like method is executed into a new thread.
	 * These thread synchronization is handled with {@link CountDownLatch} methods.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class UpdateThread extends Thread {

		@Override
		public void run() {

			// Create and start the SNP thread
			SNPThread snpThread = new SNPThread();
			if (snpThread.hasToBeStarted()) {
				CountDownLatch SNPLatch = new CountDownLatch(1);		// one for the SNP action
				snpThread.setLatch(SNPLatch);
				snpThread.start();

				// The current thread is waiting for the SNP thread to finish
				try {
					SNPLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Create and start the filter thread
			FilterThread filterThread = new FilterThread();
			filterThread.setPreviousFilterList(previousFilterList);
			if (filterThread.hasToBeStarted()) {
				CountDownLatch filterLatch = new CountDownLatch(1);
				filterThread.setLatch(filterLatch);
				filterThread.start();

				// The current thread is waiting for the filter thread to finish
				try {
					filterLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Update tracks
			TrackThread trackThread = new TrackThread();
			CountDownLatch trackLatch = new CountDownLatch(1);		// one for the track update action
			trackThread.setLatch(trackLatch);
			trackThread.start();

			// The current thread is waiting for the filter thread to finish
			try {
				trackLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Many data has been loaded, removed, garbage collecting free some memory
			Utils.garbageCollect();
		}
	}



	/////////////////////////////////////////////////////////////////////// SNPThread class

	/**
	 * The SNP thread class.
	 * This class starts the SNP action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class SNPThread extends Thread {

		PAMultiGenomeSNP multiGenomeSNP;


		public SNPThread () {
			multiGenomeSNP = new PAMultiGenomeSNP();
		}

		@Override
		public void run() {
			multiGenomeSNP.actionPerformed(null);
		}

		/**
		 * @return true if the thread has to be started, false if no need
		 */
		public boolean hasToBeStarted () {
			return multiGenomeSNP.hasToBeProcessed();
		}

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			multiGenomeSNP.setLatch(latch);
		}
	}



	/////////////////////////////////////////////////////////////////////// FilterThread class

	/**
	 * The filter thread class.
	 * This class starts the filter action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class FilterThread extends Thread {

		PAMultiGenomeFilters multigenomeFilters;


		public FilterThread () {
			multigenomeFilters = new PAMultiGenomeFilters();
		}

		@Override
		public void run() {
			multigenomeFilters.actionPerformed(null);
			previousFilterList = null;
		}

		/**
		 * @return true if the thread has to be started, false if no need
		 */
		public boolean hasToBeStarted () {
			return multigenomeFilters.hasToBeProcessed();
		}

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			multigenomeFilters.setLatch(latch);
		}

		/**
		 * @param previousFilterList the previousFilterList to set
		 */
		public void setPreviousFilterList(List<VCFFilter> previousFilterList) {
			multigenomeFilters.setPreviousFilterList(previousFilterList);
		}
	}



	/////////////////////////////////////////////////////////////////////// TrackThread class

	/**
	 * The track update thread class.
	 * This class starts the Track Update action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class TrackThread extends Thread {

		CountDownLatch latch = null;	// CountDownLatch object for synchronizing threads

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			PAMultiGenomeTrackDisplayRefresh updateTrackThread = new PAMultiGenomeTrackDisplayRefresh();
			updateTrackThread.setLatch(latch);
			updateTrackThread.actionPerformed(null);
		}
	}

}