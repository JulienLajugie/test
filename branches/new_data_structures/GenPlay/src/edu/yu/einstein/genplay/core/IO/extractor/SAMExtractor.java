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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.io.IOException;

import net.sf.samtools.SAMFileHeader.SortOrder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import edu.yu.einstein.genplay.core.IO.dataReader.ChromosomeWindowReader;
import edu.yu.einstein.genplay.core.IO.dataReader.DataReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.dataReader.StrandReader;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;


/**
 * Extractor that extract data from a SAM / BAM file
 * @author Julien Lajugie
 */
public class SAMExtractor extends Extractor implements DataReader, ChromosomeWindowReader, SCWReader, StrandReader, StrandedExtractor {

	/**
	 * Default first base position of bed files. SAM files are 1-based
	 * Even though BAM files are 0-based, {@link SAMFileReader} objects
	 * returns 1 base coordinates.
	 * */
	public static final int DEFAULT_FIRST_BASE_POSITION = 1;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION; 		// position of the first base
	private StrandedExtractorOptions	strandOptions;					// options on the strand and read length / shift
	private final SAMFileReader 		samReader;						// reader that read sam / bam files (from sam.jar)
	private final SAMRecordIterator 	iterator;						// iterator in the file
	private Chromosome 					chromosome;						// chromosome of the last item read
	private Integer 					start;							// start position of the last item read
	private Integer 					stop;							// stop position of the last item read
	private Strand 						strand;							// strand of the last item read


	/**
	 * Creates an instance of {@link SAMExtractor}
	 * @param dataFile SAM / BAM file to extract
	 */
	public SAMExtractor(File dataFile) {
		super(dataFile);
		samReader = new SAMFileReader(dataFile);
		iterator = samReader.iterator();
		iterator.assertSorted(SortOrder.coordinate);
	}


	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	@Override
	public Float getScore() {
		return 1f;
	}


	@Override
	public Integer getStart() {
		return start;
	}


	@Override
	public Integer getStop() {
		return stop;
	}


	@Override
	public Strand getStrand() {
		return strand;
	}


	@Override
	public StrandedExtractorOptions getStrandedExtractorOptions() {
		return strandOptions;
	}


	/**
	 * @param samRecord
	 * @return true if the specified SAM record is valid and should be loaded. False otherwise.
	 */
	private boolean isValidRecord(SAMRecord samRecord) {
		if (samRecord.getReadPairedFlag() && samRecord.getSecondOfPairFlag()) {
			return false;
		}
		if (samRecord.getReadUnmappedFlag()) {
			return false;
		}
		if (samRecord.getReadPairedFlag() && samRecord.getMateUnmappedFlag()) {
			return false;
		}
		if (samRecord.getNotPrimaryAlignmentFlag()) {
			return false;
		}
		if (samRecord.getReadFailsVendorQualityCheckFlag()) {
			return false;
		}
		if (samRecord.getDuplicateReadFlag()) {
			return false;
		}
		return true;
	}


	/**
	 * Process the specified {@link SAMRecord} and extract its
	 * chromosome, start, stop and strand values
	 * @param samRecord
	 */
	private void processSamRecord(SAMRecord samRecord) {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		chromosome = projectChromosomes.get(samRecord.getReferenceName());
		start = samRecord.getAlignmentStart();
		stop = start + samRecord.getReadLength();
		if (samRecord.getReadPairedFlag() && samRecord.getProperPairFlag() && !samRecord.getMateUnmappedFlag() && (samRecord.getInferredInsertSize() > 0)) {
			stop += samRecord.getInferredInsertSize();
		}
		strand = samRecord.getReadNegativeStrandFlag() ? Strand.THREE : Strand.FIVE;
	}


	@Override
	public boolean readItem() throws IOException {
		try {
			SAMRecord samRecord = null;
			boolean isValidRecord = false;
			while (iterator.hasNext() && !isValidRecord) {
				samRecord = iterator.next();
				isValidRecord = isValidRecord(samRecord);
			}
			if (iterator.hasNext()) {
				processSamRecord(samRecord);
				return true;
			} else {
				return false;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return true;
		}
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		return dataFile.getName();
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;

	}


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
