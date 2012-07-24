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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.sf.jannot.tabix.TabixConfiguration;
import net.sf.jannot.tabix.TabixWriter;
import net.sf.samtools.util.BlockCompressedOutputStream;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
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
public final class PAMultiGenomeVCFToTBI extends TrackListAction {

	private static final 	long serialVersionUID = -6475180772964541278L; 			// generated ID
	private static final 	String ACTION_NAME = "VCF Convertion & Indexation";		// action name
	private static final 	String DESCRIPTION = "Convert and index a VCF file"; 	// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMultiGenomeVCFToTBI";

	private final File vcfFile;		// the vcf file
	private File bgzipFile;			// the gz file
	private File tbiFile;			// the tbi file
	private boolean loadingPassBy;


	/**
	 * Creates an instance of {@link PAMultiGenomeVCFToTBI}
	 * @param file the vcf file to compress and to index
	 */
	public PAMultiGenomeVCFToTBI(File file) {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		vcfFile = file;
		bgzipFile = null;
		tbiFile = null;
		loadingPassBy = false;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ProjectManager.getInstance().isMultiGenomeProject() || loadingPassBy) {		// if it is a multi genome project
			// Compress the VCF
			try {
				compression();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Index the VCF
			try {
				indexation();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Many data has been loaded, removed, garbage collecting free some memory
			Utils.garbageCollect();
		}
	}


	/**
	 * Compress the VCF with BGZIP
	 * @throws Exception
	 */
	private void compression () throws Exception {
		if (Utils.getExtension(vcfFile).equals("vcf")) {
			// Open the VCF input stream
			FileInputStream vcfFIS = new FileInputStream(vcfFile);
			DataInputStream vcfIN = new DataInputStream(vcfFIS);
			InputStreamReader vcfISR = new InputStreamReader(vcfIN);
			BufferedReader vcfBR = new BufferedReader(vcfISR);

			// Get the BGZIP file
			bgzipFile = new File(vcfFile.getPath() + ".gz");

			// Open the BGZIP output stream
			BlockCompressedOutputStream bgzipBCOS = new BlockCompressedOutputStream(bgzipFile);

			String newLine = "\n";
			String vcfLine;
			while ((vcfLine = vcfBR.readLine()) != null) {
				bgzipBCOS.write(vcfLine.getBytes());
				bgzipBCOS.write(newLine.getBytes());
			}

			// Close the BGZIP output stream
			bgzipBCOS.close();

			// Close the VCF input stream
			vcfBR.close();
			vcfISR.close();
			vcfIN.close();
			vcfFIS.close();
		} else {
			JOptionPane.showMessageDialog(null, "The VCF extension has not been found.\nThe file to compress must be a VCF file.\nThe file will not be compressed.", "Compression error.", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Index the compressed VCF with Tabix
	 * @throws Exception
	 */
	private void indexation () throws Exception {
		if (Utils.getExtension(bgzipFile).equals("gz")) {
			tbiFile = new File(bgzipFile.getPath() + ".tbi");
			tbiFile.createNewFile();
			TabixWriter writer = new TabixWriter(bgzipFile, TabixConfiguration.VCF_CONF);
			writer.createIndex(tbiFile);
		} else {
			JOptionPane.showMessageDialog(null, "The BGZIP extension has not been found.\nThe file will not be indexed.", "Indexing error.", JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * @param loadingPassBy the loadingPassBy to set
	 */
	public void setLoadingPassBy(boolean loadingPassBy) {
		this.loadingPassBy = loadingPassBy;
	}


	/**
	 * @return the vcfFile
	 */
	public File getVcfFile() {
		return vcfFile;
	}


	/**
	 * @return the bgzipFile
	 */
	public File getBgzipFile() {
		return bgzipFile;
	}


	/**
	 * @return the tbiFile
	 */
	public File getTbiFile() {
		return tbiFile;
	}
}