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
package edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.multiGenome.operation.convert.MGOBGZIPCompression;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGABGZIPCompression extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION = "Compress VCF as BGZIP"; 	// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 	ACTION_NAME = "Compress VCF as BGZIP";	// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Compress VCF as BGZIP";

	private MGOBGZIPCompression operation;
	private final File vcfFile;	// the vcf file
	private File bgzFile;	// the bgzip file
	private boolean success;


	/**
	 * Creates an instance of {@link MGABGZIPCompression}.
	 * @param file the file to compress
	 */
	public MGABGZIPCompression(File file) {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		vcfFile = file;
		success = false;
	}


	@Override
	protected Boolean processAction() throws Exception {
		// Notifies the action
		notifyActionStart(ACTION_NAME, 1, false);

		operation = new MGOBGZIPCompression(vcfFile);
		try {
			return operation.compute();
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
		}

		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		success = actionResult;
		bgzFile = operation.getBgzFile();

		if (!success) {
			JOptionPane.showMessageDialog(getRootPane(), "The VCF extension has not been found.\nThe file to compress must be a VCF file.\nThe file will not be compressed.", "Compression error.", JOptionPane.INFORMATION_MESSAGE);
		}

		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @return true if the action has been correctly finish, false otherwise
	 */
	public boolean hasBeenDone () {
		return success;
	}


	/**
	 * @return the BGZIP file
	 */
	public File getCompressedFile() {
		return bgzFile;
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}
