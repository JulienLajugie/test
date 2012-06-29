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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsOffsetList;
import edu.yu.einstein.genplay.core.manager.project.MultiGenomeProject;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileStatistic;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFSampleStatistic;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderElementRecord;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.IndelVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.action.project.multiGenome.PAMultiGenomeSynchronizing;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class manages the whole synchronization processes for indels and structural variants.
 * There are two main method (all the others are private):
 * - insertVariantposition: add all concerned variants from the VCF files to the data structure.
 * - performPositionSynchronization: perform the synchronization properly speaking.
 * 
 * This class is totally managed in the full synchronization process by the class {@link PAMultiGenomeSynchronizing}.
 * 
 * A synchronization process means updating genome(s) offset according to the variation.
 * A deletion involves a synchronization process on the genome it occurs.
 * An insertion involves a synchronization process on all genomes but the one it occurs.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSynchronizer implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 7123540095215677101L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private MultiGenomeProject multiGenomeProject;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(multiGenomeProject);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		multiGenomeProject = (MultiGenomeProject) in.readObject();
	}


	/**
	 * Constructor of {@link MGSynchronizer}
	 * @param multiGenomeProject the multi genome instance
	 */
	public MGSynchronizer (MultiGenomeProject multiGenomeProject) {
		this.multiGenomeProject = multiGenomeProject;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	Variant insertion section


	/**
	 * Reads the VCF files in order to store the position where a variation happens.
	 * Does not take into account SNP and variation equal to the reference (0/0)
	 * @throws IOException
	 */
	public void insertVariantposition () throws IOException {
		List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();	// get the chromosome list
		List<VCFFile> VCFFileList = multiGenomeProject.getAllVCFFiles();													// get all vcf readers

		List<AlleleType> alleleTypeList = new ArrayList<AlleleType>();
		alleleTypeList.add(AlleleType.ALLELE01);
		alleleTypeList.add(AlleleType.ALLELE02);
		
		for (Chromosome chromosome: chromosomeList) {																// loop on every chromosome
			for (VCFFile vcfFile: VCFFileList) {																	// loop on every vcf reader
				VCFFileStatistic statistic = vcfFile.getStatistics();
				List<String> genomeNames = getRequiredGenomeNamesFromAReader(vcfFile);								// get the genome names list
				VCFHeader fileHeader = vcfFile.getHeader();
				List<String> genomeRawNames = fileHeader.getRawGenomesNames();
				List<String> columnNames = getColumnNamesForQuery(genomeRawNames);									// get the list of column to perform the query on the current vcf
				List<Map<String, Object>> result = vcfFile.getReader().query(chromosome.getName(), 0, chromosome.getLength(), columnNames);	// perform the query on the current vcf: chromosome name, from 0 to its length

				int[] chromosomeBounds = getBounds(result.size());
				int lineNumber = 0;
				for (Map<String, Object> VCFLine: result) {															// loop on every line of the result of the query
					lineNumber++;
					int referencePosition = Integer.parseInt(VCFLine.get(VCFColumnName.POS.toString()).toString());	// get the reference genome position (POS field)
					String reference = VCFLine.get(VCFColumnName.REF.toString()).toString();						// get the reference value (REF field)
					String alternative = VCFLine.get(VCFColumnName.ALT.toString()).toString();						// get the alternative values and split them into an array (comma separated)
					String[] alternatives = Utils.split(alternative, ',');
					String info = VCFLine.get(VCFColumnName.INFO.toString()).toString();							// get the information of the variation (INFO field). Needs it if the variation is SV coded.
					float score;
					try {
						score = Float.parseFloat(VCFLine.get("QUAL").toString());
					} catch (Exception e) {
						score = 0;
					}

					int[] variantLengths = getVariantLengths(reference, alternatives, info);
					VariantType[] variantTypes = getVariantTypes(variantLengths);

					updateFileStatistics(statistic, variantTypes, alternatives);

					if (isInBounds(chromosomeBounds, lineNumber)) {
						updateHeaderInformation(fileHeader, VCFLine, genomeRawNames);
					}

					for (String genomeName: genomeNames) {															// loop on every genome raw name
						String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
						//String genotype = VCFLine.get(genomeRawName).toString().trim().split(":")[0];				// get the related format value, split it (colon separated) into an array, get the first value: the genotype 
						String genotype = Utils.split(VCFLine.get(genomeRawName).toString().trim(), ':')[0];				// get the related format value, split it (colon separated) into an array, get the first value: the genotype
						if (genotype.length() == 3) {																// the genotype must have 3 characters (eg: 0/0 0/1 1/0 1/1 0|0 0|1 1|0 1|1)
							int firstAlleleIndex = getAlleleIndex(VCFLine.get(genomeRawName).toString().charAt(0));
							int secondAlleleindex = getAlleleIndex(VCFLine.get(genomeRawName).toString().charAt(2));
							updateGenotypeSampleStatistics(statistic.getSampleStatistics(genomeName), variantTypes, firstAlleleIndex, secondAlleleindex);

							for (AlleleType alleleType: alleleTypeList) {
								int currentAlleleIndex = -1;
								if (alleleType == AlleleType.ALLELE01) {													// if we are processing the paternal allele
									currentAlleleIndex = firstAlleleIndex;													// we look at the first character
								} else {																					// if not, it means we are looking to the maternal allele
									currentAlleleIndex = secondAlleleindex;													// and we look at the third character
								}

								if (currentAlleleIndex >= 0) {																// if we have a variation
									int alleleLength = variantLengths[currentAlleleIndex];									// we retrieve its length
									VariantType variantType = variantTypes[currentAlleleIndex];								// get the type of variant according to the length of the variation
									vcfFile.addVariantType(genomeName, variantType);										// notice the reader of the variant type
									updateVariationSampleStatistics(statistic.getSampleStatistics(genomeName), variantType, alternatives[currentAlleleIndex]);
									if (variantType != VariantType.SNPS) {													// if it is not a SNP
										List<MGOffset> offsetList;
										MGGenomeForDisplay genomeForDisplay = multiGenomeProject.getMultiGenomeForDisplay().getGenomeInformation(genomeName);
										MGAlleleForDisplay alleleForDisplay;
										if (alleleType == AlleleType.ALLELE01) {											// if we are processing the paternal allele
											offsetList = multiGenomeProject.getMultiGenome().getGenomeInformation(genomeName).getAlleleA().getOffsetList().get(chromosome);	// we get the list of offset of the allele A
											alleleForDisplay = genomeForDisplay.getAlleleA();
										} else {																			// if not, it means we are looking to the maternal allele
											offsetList = multiGenomeProject.getMultiGenome().getGenomeInformation(genomeName).getAlleleB().getOffsetList().get(chromosome);	// we get the list of offset of the allele B
											alleleForDisplay = genomeForDisplay.getAlleleB();
										}
										offsetList.add(new MGOffset(referencePosition, alleleLength));						// we insert the new offset

										if (variantType == VariantType.INSERTION) {											// if we are processing an insertion
											multiGenomeProject.getMultiGenome().getReferenceGenome().getAllele().getOffsetList().get(chromosome).add(new MGOffset(referencePosition, alleleLength));				// we insert it into the reference genome allele
											MGVariantListForDisplay variantListForDisplay = alleleForDisplay.getVariantList(chromosome, VariantType.INSERTION); // we also insert it to the display data structure
											VariantInterface variant = new IndelVariant(variantListForDisplay, referencePosition, alleleLength, score, 0);
											variantListForDisplay.getVariantList().add(variant);
										} else {																			// if it is not an insertion it is a deletion
											MGVariantListForDisplay variantListForDisplay = alleleForDisplay.getVariantList(chromosome, VariantType.DELETION); // we only insert it to the display data structure
											VariantInterface variant = new IndelVariant(variantListForDisplay, referencePosition, alleleLength, score, 0);
											variantListForDisplay.getVariantList().add(variant);
										}
									}
								}
							}
						} else {
							System.err.println("FORMAT field for the genome " + genomeRawName + " (" + chromosome.getName() + ") at the position " + referencePosition + " of the reference is invalid: GT = " + genotype + " (length: " + genotype.length() + ")");
						}
					}
				}
			}
		}
		for (VCFFile vcfFile: VCFFileList) {
			vcfFile.getStatistics().processStatistics();
		}
	}
	
	
	/**
	 * Some function are time expensive and could be applied only on some lines.
	 * Which ones? The beginning/ending of a set of results can have non representative results.
	 * That function will define a minimum and a maximum bound to delimit a sample of lines to use.
	 * It will take the X percent of lines that are in the middle of the set.
	 * @param length	length of a set of result
	 * @return			an array of bounds (0: min; 1: max)
	 */
	private int[] getBounds (int length) {
		int delimiter = (int) (length * 0.1) / 2;
		
		int[] result = new int[2];
		int middle = length / 2;
		
		result[0] = middle - delimiter;
		result[1] = middle + delimiter;
		
		if (result[0] < 0) {
			result[0] = 0;
		}
		
		if (result[1] > length) {
			result[1] = length;
		}
		
		return result;
	}
	
	
	/**
	 * @param bounds	the bounds
	 * @param line	the line number
	 * @return true if the line number is in the bounds, false if out
	 */
	private boolean isInBounds (int[] bounds, int line) {
		if (line < bounds[0] || line > bounds[1]) {
			return false;
		}
		return true;
	}
	

	/**
	 * Creates the list of the column names necessary for a query.
	 * Four fields are static: POS, REF, ALT and INFO for the position synchronization.
	 * Name of the genomes (raw name) must be added dynamically according to the content of the VCF file and the project requirements.
	 * @param genomeNames list of genome raw names to add to the static list
	 * @return the list of column names necessary for a query
	 */
	private List<String> getColumnNamesForQuery (List<String> genomeRawNames) {
		List<String> columnNames = new ArrayList<String>();
		columnNames.add(VCFColumnName.POS.toString());
		columnNames.add(VCFColumnName.REF.toString());
		columnNames.add(VCFColumnName.ALT.toString());
		columnNames.add(VCFColumnName.QUAL.toString());
		columnNames.add(VCFColumnName.INFO.toString());
		columnNames.add(VCFColumnName.FORMAT.toString());
		for (String genomeName: genomeRawNames) {
			columnNames.add(genomeName);
		}
		return columnNames;
	}


	/**
	 * Retrieves the name of the genomes required in the project that are present in a VCF file.
	 * A project does not necessary require all genomes present in a VCF file.
	 * @param vcfFile	VCF file
	 * @return			the list of genome names required for the project and present in the VCF file
	 */
	private List<String> getRequiredGenomeNamesFromAReader (VCFFile vcfFile) {
		List<String> requiredGenomeNames = new ArrayList<String>();
		List<String> allGenomeNames = multiGenomeProject.getGenomeNames();
		List<String> readerGenomeNames = vcfFile.getHeader().getGenomeNames();

		for (String readerGenomeName: readerGenomeNames) {
			if (allGenomeNames.contains(readerGenomeName)) {
				requiredGenomeNames.add(readerGenomeName);
			}
		}

		return requiredGenomeNames;
	}


	/**
	 * Transforms a character into its allele index.
	 * The char 1 will refer to the first alternative located at the index 0 of any arrays.
	 * The char 0 and '.' will return -1 and don't refer to any alternatives.
	 * @param alleleChar the character
	 * @return the associated code
	 */
	public int getAlleleIndex (char alleleChar) {
		int alleleIndex = -1;
		if (alleleChar != '.' && alleleChar != '0') {
			try {
				alleleIndex = Integer.parseInt(alleleChar + "") - 1;
			} catch (Exception e) {}
		}
		return alleleIndex;
	}



	/**
	 * Retrieves the length of all defined alternatives
	 * If an alternative is SV coded, the info field is required
	 * @param reference		the REF field
	 * @param alternatives	the parsed ALT field
	 * @param info			the INFO field
	 * @return				an array of integer as lengths
	 */
	public int[] getVariantLengths(String reference, String[] alternatives, String info) {
		int[] lengths = new int[alternatives.length];

		for (int i = 0; i < alternatives.length; i++) {
			lengths[i] = retrieveVariantLength(reference, alternatives[i], info);
		}

		return lengths;
	}


	/**
	 * Defines the variant type according to several lengths
	 * @param length 	array of length
	 * @return			an array of variant types
	 */
	public VariantType[] getVariantTypes (int[] length) {
		VariantType[] variantTypes = new VariantType[length.length];

		for (int i = 0; i < length.length; i++) {
			variantTypes[i] = getVariantType(length[i]);
		}

		return variantTypes;
	}


	/**
	 * Retrieves the length of a variation using the reference and the alternative.
	 * If the alternative is a structural variant, the length is given by the SVLEN INFO attributes 
	 * @param reference		REF field
	 * @param alternative	ALT field
	 * @param info			INFO field
	 * @return	the length of the variation
	 */
	private int retrieveVariantLength (String reference, String alternative, String info) {
		int length = 0;

		if (isStructuralVariant(alternative)) {
			String lengthPattern = "SVLEN=";
			int lengthPatternIndex = info.indexOf(lengthPattern) + lengthPattern.length();
			int nextCommaIndex = info.indexOf(";", lengthPatternIndex);
			if (nextCommaIndex == -1) {
				length = Integer.parseInt(info.substring(lengthPatternIndex));
			} else {
				length = Integer.parseInt(info.substring(lengthPatternIndex, nextCommaIndex));
			}
		} else {
			length = alternative.length() - reference.length();
		}

		return length;
	}


	/**
	 * Tests the length of a variation to find its type out.
	 * @param variationLength 	length of the variation
	 * @return					the variation type {@link VariantType}
	 */
	private VariantType getVariantType (int variationLength) {
		if (variationLength < 0) {
			return VariantType.DELETION;
		} else if (variationLength > 0) {
			return VariantType.INSERTION;
		} else if (variationLength == 0) {
			return VariantType.SNPS;
		} else {
			return null;
		}
	}


	/**
	 * Updates statistics related to the file
	 * @param statistic		file statistics
	 * @param variantTypes	variant type array
	 * @param alternatives	alternatives array
	 */
	private void updateFileStatistics (VCFFileStatistic statistic, VariantType[] variantTypes, String[] alternatives) {
		statistic.incrementNumberOfLines();
		for (int i = 0; i < variantTypes.length; i++) {
			if (variantTypes[i] == VariantType.SNPS) {
				statistic.incrementNumberOfSNPs();
			} else if (variantTypes[i] == VariantType.INSERTION) {
				if (isStructuralVariant(alternatives[i])) {
					statistic.incrementNumberOfLongInsertions();
				} else {
					statistic.incrementNumberOfShortInsertions();
				}
			} else if (variantTypes[i] == VariantType.DELETION) {
				if (isStructuralVariant(alternatives[i])) {
					statistic.incrementNumberOfLongDeletions();
				} else {
					statistic.incrementNumberOfShortDeletions();
				}
			}
		}
	}


	/**
	 * 
	 * @param statistic				sample statistics
	 * @param variantTypes			array of variant types
	 * @param firstAlleleNumber		number of the first allele
	 * @param secondAlleleNumber	number of the second allele
	 */
	private void updateGenotypeSampleStatistics (VCFSampleStatistic statistic, VariantType[] variantTypes, int firstAlleleIndex, int secondAlleleIndex) {
		boolean homozygote = isVariantHomozygote(firstAlleleIndex, secondAlleleIndex);
		boolean heterozygote = isVariantHeterozygote(firstAlleleIndex, secondAlleleIndex);

		for (VariantType variantType: variantTypes) {
			if (homozygote) {
				if (variantType == VariantType.SNPS) {
					statistic.incrementNumberOfHomozygoteSNPs();
				} else if (variantType == VariantType.INSERTION) {
					statistic.incrementNumberOfHomozygoteInsertions();
				} else if (variantType == VariantType.DELETION) {
					statistic.incrementNumberOfHomozygoteDeletions();
				}
			} else if (heterozygote) {
				if (variantType == VariantType.SNPS) {
					statistic.incrementNumberOfHeterozygoteSNPs();
				} else if (variantType == VariantType.INSERTION) {
					statistic.incrementNumberOfHeterozygoteInsertions();
				} else if (variantType == VariantType.DELETION) {
					statistic.incrementNumberOfHeterozygoteDeletions();
				}
			}
		}
	}


	/**
	 * Defines if a variant is homozygote according to its genotype.
	 * @param firstAlleleNumber		number related to the "first" allele
	 * @param secondAlleleNumber	number related to the "second" allele
	 * @return	true if the variant is homozygote, false otherwise
	 */
	private boolean isVariantHomozygote (int firstAlleleNumber, int secondAlleleNumber) {
		if (firstAlleleNumber == secondAlleleNumber && firstAlleleNumber >= 0) {
			return true;
		}
		return false;
	}


	/**
	 * Defines if a variant is heterozygote according to its genotype.
	 * @param firstAlleleNumber		number related to the "first" allele
	 * @param secondAlleleNumber	number related to the "second" allele
	 * @return	true if the variant is heterozygote, false otherwise
	 */
	private boolean isVariantHeterozygote (int firstAlleleNumber, int secondAlleleNumber) {
		if (firstAlleleNumber != secondAlleleNumber) {
			if (firstAlleleNumber >= 0 || secondAlleleNumber >= 0) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Updates statistics related to the sample
	 * @param statistic		sample statistics
	 * @param variantType	variant type
	 * @param alternative	alternative
	 */
	private void updateVariationSampleStatistics (VCFSampleStatistic statistic, VariantType variantType, String alternative) {
		if (variantType == VariantType.SNPS) {
			statistic.incrementNumberOfSNPs();
		} else if (variantType == VariantType.INSERTION) {
			if (isStructuralVariant(alternative)) {
				statistic.incrementNumberOfLongInsertions();
			} else {
				statistic.incrementNumberOfShortInsertions();
			}
		} else if (variantType == VariantType.DELETION) {
			if (isStructuralVariant(alternative)) {
				statistic.incrementNumberOfLongDeletions();
			} else {
				statistic.incrementNumberOfShortDeletions();
			}
		}
	}


	/**
	 * @param alternative ALT field (or part of it)
	 * @return true if the given alternative is coded as an SV
	 */
	private boolean isStructuralVariant (String alternative) {
		if (alternative.charAt(0) == '<') {
			return true;
		}
		return false;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Update common information section
	
	/**
	 * Updates the header information.
	 * It consists in storing the first values found of the IDs from INFO and FORMAT fields.
	 */
	private void updateHeaderInformation (VCFHeader fileHeader, Map<String, Object> VCFLine, List<String> genomeNames) {
		updateHeaderINFOInformation(fileHeader, VCFLine);
		updateHeaderFORMATInformation(fileHeader, VCFLine, genomeNames);
	}
	
	
	/**
	 * Updates the INFO header information.
	 * It consists in storing the first values found of the IDs from the INFO field.
	 */
	private void updateHeaderINFOInformation (VCFHeader header, Map<String, Object> VCFLine) {
		String info = (String) VCFLine.get(VCFColumnName.INFO.toString());
		String[] fields = Utils.split(info, ';');

		for (int i = 0; i < fields.length; i++) {
			//String[] pair = fields[i].split("=");
			String[] pair = Utils.split(fields[i], '=');
			if (pair.length == 2) {
				String id = pair[0];
				Object value = pair[1];
				VCFHeaderAdvancedType infoHeader = header.getInfoHeaderFromID(id);
				if (infoHeader.getType() == String.class) {
					((VCFHeaderElementRecord)infoHeader).addElement(value);
				}
			}
		}
	}
	
	
	/**
	 * Updates the FORMAT header information.
	 * It consists in storing the first values found of the IDs from the FORMAT field.
	 */
	private void updateHeaderFORMATInformation (VCFHeader header, Map<String, Object> VCFLine, List<String> genomeNames) {
		String format = (String) VCFLine.get(VCFColumnName.FORMAT.toString());
		String[] fields = Utils.split(format, ':');

		for (int i = 0; i < fields.length; i++) {
			VCFHeaderAdvancedType formatHeader = header.getFormatHeaderFromID(fields[i]);
			if (formatHeader.getId().equals("GT") || formatHeader.getType() == String.class) {
				for (String genome: genomeNames) {
					//String[] values = ((String) VCFLine.get(genome)).split(":");
					String[] values = Utils.split(((String) VCFLine.get(genome)), ':');
					if (i < values.length) {
						((VCFHeaderElementRecord)formatHeader).addElement(values[i]);
					}
				}
			}
		}
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Synchronization section


	/**
	 * Performs the synchronization for every genome of the project.
	 * The process is separated on 3 levels:
	 * - genomes
	 * - alleles
	 * - chromosomes
	 * (Makes the reading easier)
	 */
	public void performPositionSynchronization () {
		synchronizeToGenomesLevel();
		multiGenomeProject.getMultiGenome().getReferenceGenome().synchronizePosition();
	}


	/**
	 * This method manages the position synchronization for every genome.
	 * It handles the genomes loop in order to process the synchronization for both alleles of each of them.
	 */
	private void synchronizeToGenomesLevel () {
		for (String genomeName: multiGenomeProject.getGenomeNames()) {																// scan on every genome
			MGGenome genomeInformation = multiGenomeProject.getMultiGenome().getGenomeInformation(genomeName);						// current genome information 

			ChromosomeListOfLists<MGOffset> chromosomeAlleleAOffsetList = synchronizeToAlleleLevel(genomeInformation.getAlleleA().getOffsetList());		// get the synchronized chromosome list of list for the allele A
			ChromosomeListOfLists<MGOffset> chromosomeAlleleBOffsetList = synchronizeToAlleleLevel(genomeInformation.getAlleleB().getOffsetList());		// get the synchronized chromosome list of list for the allele B

			genomeInformation.getAlleleA().setOffsetList(chromosomeAlleleAOffsetList);						// set the current chromosome list of list of the allele A with the synchronized one
			genomeInformation.getAlleleB().setOffsetList(chromosomeAlleleBOffsetList);						// set the current chromosome list of list of the allele B with the synchronized one
		}
	}


	/**
	 * This method manages the synchronization for the chromosome list of list of an allele.
	 * It manages the chromosome loop in order to process the synchronization for every chromosome of this list.
	 * @param chromosomeAlleleOffsetList chromosome list of list of an allele
	 * @return a synchronized chromosome list of list
	 */
	private ChromosomeListOfLists<MGOffset> synchronizeToAlleleLevel (ChromosomeListOfLists<MGOffset> chromosomeAlleleOffsetList) {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();						// get the number of chromosome

		ChromosomeListOfLists<MGOffset> chromosomeReferenceListOfList = multiGenomeProject.getMultiGenome().getReferenceGenome().getAllele().getOffsetList();	// get the chromosome list of list of the reference genome
		ChromosomeListOfLists<MGOffset> list = new ChromosomeArrayListOfLists<MGOffset>();												// instantiate a new chromosome list of list (to insert the synchronized list of offset)

		for (int i = 0; i < chromosomeListSize; i++) {																					// scan on the number of chromosome (loop on ever chromosome)
			List<MGOffset> referenceOffsetList = chromosomeReferenceListOfList.get(i);													// get the list of offset from the reference genome for the current chromosome
			List<MGOffset> alleleOffsetList = chromosomeAlleleOffsetList.get(i);														// get the list of offset from the current genome for the current chromosome
			List<MGOffset> alleleOffsetListTmp = synchronizeToChromosomeLevel(referenceOffsetList, alleleOffsetList);					// get the synchronized offset list
			list.add(alleleOffsetListTmp);																								// add the synchronized offset list to the chromosome list of list
		}
		return list;																													// return the chromosome list of list recently created
	}


	/**
	 * This method performs the synchronization of a list of offset using the offset list from the reference genome.
	 * @param referenceOffsetList	offset list from the reference genome
	 * @param alleleOffsetList		offset list to synchronize
	 * @return a synchronized list of offset
	 */
	private List<MGOffset> synchronizeToChromosomeLevel (List<MGOffset> referenceOffsetList, List<MGOffset> alleleOffsetList) {
		List<MGOffset> list = new IntArrayAsOffsetList();

		int referenceOffsetIndex = 0;				// index of the current offset from the reference genome
		int alleleOffsetIndex = 0;					// index of the current offset from the genome

		int lastRefPosition = 0;					// last genome reference position of a variation
		int length = 0;								// total length of all insertion between two deletion

		boolean valid = true;

		while (valid) {
			MGOffset currentReferenceOffset = getOffset(referenceOffsetList, referenceOffsetIndex);										// get the current reference offset
			MGOffset currentAlleleOffset = getOffset(alleleOffsetList, alleleOffsetIndex);												// get the current offset from the current genome
			MGOffset newOffset = null;																									// declare a new offset

			if (currentReferenceOffset != null && currentAlleleOffset != null) {														// if both offset exist

				if (currentAlleleOffset.getPosition() == currentReferenceOffset.getPosition()) {										// if both position are similar
					if (currentReferenceOffset.getValue() > currentAlleleOffset.getValue()) {											// if the value from the reference offset is higher than the one from the current allele, it means that an other genome of the project contains a bigger insertion.
						newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, currentAlleleOffset.getValue());	// get the new offset
						lastRefPosition = currentReferenceOffset.getPosition() + 1;														// the last reference position is the current reference position + 1 
						length = 0;																										// reset the length counter
					} else if (currentReferenceOffset.getValue() == currentAlleleOffset.getValue()) {									// if both value (length) are equal, there is no new offset
						length += currentReferenceOffset.getValue();																	// but the length must be taken into account
					}
					referenceOffsetIndex++;																								// increase the current reference offset
					alleleOffsetIndex++;																								// increase the current genome offset
				} else if(currentAlleleOffset.getPosition() < currentReferenceOffset.getPosition()) {
					if (currentAlleleOffset.getValue() < 0) {																			// if the current offset is related to a deletion
						newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, 0);	// get the new offset
						lastRefPosition = currentAlleleOffset.getPosition() + Math.abs(currentAlleleOffset.getValue()) + 1;
						length = 0;																										// reset the length counter
					} else if (currentAlleleOffset.getValue() > 0) {																	// if the current offset is related to an insertion
						length += currentAlleleOffset.getValue();																		// the length must be taken into account
					}
					alleleOffsetIndex++;																								// increase the current genome offset only
				} else if(currentReferenceOffset.getPosition() < currentAlleleOffset.getPosition()) {
					newOffset = getNewOffset(list, currentReferenceOffset, lastRefPosition, length, 0);									// get the new offset
					lastRefPosition = currentReferenceOffset.getPosition() + 1;
					length = 0;																											// reset the length counter
					referenceOffsetIndex++;																								// increase the current reference offset only
				}

			} else if (currentReferenceOffset == null && currentAlleleOffset != null) {													// if only the offset from the current genome exists

				if (currentAlleleOffset.getValue() < 0) {																				// if the current offset is related to a deletion
					newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, 0);									// get the new offset
					lastRefPosition = currentAlleleOffset.getPosition() + Math.abs(currentAlleleOffset.getValue()) + 1;
					length = 0;																											// reset the length counter
				} else if (currentAlleleOffset.getValue() > 0) {																		// if the current offset is related to an insertion
					length += currentAlleleOffset.getValue();																			// the length must be taken into account
				}
				alleleOffsetIndex++;																									// increase the current genome offset

			} else if (currentReferenceOffset != null && currentAlleleOffset == null) {													// if only the current offset from the reference exists

				newOffset = getNewOffset(list, currentReferenceOffset, lastRefPosition, length, 0);										// get the new offset
				lastRefPosition = currentReferenceOffset.getPosition() + 1;
				length = 0;																												// reset the length counter
				referenceOffsetIndex++;																									// increase the current reference offset only

			} else {
				valid = false;
			}

			if (newOffset != null) {
				list.add(newOffset);
			}
		}

		return list;
	}


	/**
	 * Creates an offset
	 * @param offsetList		the full list of offsets
	 * @param currentOffset		the offset being processed
	 * @param lastRefPosition	the last reference genome position
	 * @param additionalLength	the total length of all insertion between two deletion
	 * @param additionalValue	only in the case where two or more insertion happen at the same position and the one of the actual offset is not the longest one
	 * @return					a new offset corresponding to the current process
	 */
	private MGOffset getNewOffset (List<MGOffset> offsetList, MGOffset currentOffset, int lastRefPosition, int additionalLength, int additionalValue) {
		MGOffset offset = null;
		int newGenomePosition = 0;
		int newOffsetValue = 0;

		if (offsetList.size() == 0) {
			newGenomePosition = currentOffset.getPosition();
		} else {
			newGenomePosition = (currentOffset.getPosition() - lastRefPosition) + getOffset(offsetList).getPosition() + additionalLength;
			newOffsetValue = getOffset(offsetList).getValue();
		}
		newOffsetValue += Math.abs(currentOffset.getValue());
		newGenomePosition += additionalValue + 1;

		offset = new MGOffset(newGenomePosition, newOffsetValue);

		return offset;
	}


	/**
	 * Look for an offset in an offset list according to an index.
	 * Checks if the index is valid and return the offset.
	 * @param offsetList 	the list of offset
	 * @param index			the index of the offset in the list
	 * @return				the offset if the index is valid, null otherwise
	 */
	private MGOffset getOffset (List<MGOffset> offsetList) {
		if (offsetList.size() > 0) {
			return offsetList.get(offsetList.size() - 1);
		}
		return null;
	}


	/**
	 * Look for an offset in an offset list according to an index.
	 * Checks if the index is valid and return the offset.
	 * @param offsetList 	the list of offset
	 * @param index			the index of the offset in the list
	 * @return				the offset if the index is valid, null otherwise
	 */
	private MGOffset getOffset (List<MGOffset> offsetList, int index) {
		if (index < offsetList.size()) {
			return offsetList.get(index);
		}
		return null;
	}

}