/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.nucleotideList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * This class provides the representation of a sequence from a .2bit file as described 
 * in the help file of the UCSC Genome Browser: http://genome.ucsc.edu/FAQ/FAQformat.html#format7
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequence extends AbstractList<Nucleotide> implements Serializable, List<Nucleotide>, Stoppable {

	private static final long serialVersionUID = 4155123051619828951L;	// generated ID
	private transient RandomAccessFile 	raf;			// 2bit random access file  
	private String	filePath;					// path of the 2bit file (used for the serialization)
	private int 	headerSize;					// the size in byte of the header of the sequence
	private String 	name;						// the sequence name  
	private int 	offset;						// the offset of the sequence data relative to the start of the file
	private int 	dnaSize;					// number of bases of DNA in the sequence
	private int[] 	nBlockStarts;				// the starting position for each block of Ns
	private int[] 	nBlockSizes;				// the length of each block of Ns
	private int[] 	maskBlockStarts;			// the starting position for each masked block
	private int[] 	maskBlockSizes;				// the length of each masked block
	private boolean	needToBeStopped = false; 	// true if the execution need to be stopped
	
	
	/**
	 * Default constructor. Creates an instance of {@link TwoBitSequence}
	 */
	public TwoBitSequence() {
		super();
	}
	
	
	/**
	 * Extract the information about a sequence from a {@link TwoBitSequence}
	 * @param raf {@link RandomAccessFile}
	 * @param offset offset of the sequence in the file
	 * @param name name of the sequence
	 * @param reverseBytes true if the byte order in the input file need to be reversed
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void extract(String filePath, RandomAccessFile raf, int offset, String name, boolean reverseBytes) throws IOException, InterruptedException {
		this.filePath = filePath;
		this.raf = raf;
		this.name = name;
		this.offset = offset;
		raf.seek(offset);
		if (reverseBytes) {
			dnaSize = Integer.reverseBytes(raf.readInt());
		} else {
			dnaSize = raf.readInt();
		}
		int nBlockCount = 0;
		if (reverseBytes) {
			nBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			nBlockCount = raf.readInt();
		}
		nBlockStarts = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockStarts[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockStarts[i] = raf.readInt();
			}
		}
		nBlockSizes = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockSizes[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockSizes[i] = raf.readInt();
			}			
		}
		
		int maskBlockCount = 0;
		if (reverseBytes) {
			maskBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			maskBlockCount = raf.readInt();
		}
		maskBlockStarts = new int[maskBlockCount];
		for (int i = 0; i < maskBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				maskBlockStarts[i] = Integer.reverseBytes(raf.readInt());
			} else {
				maskBlockStarts[i] = raf.readInt();
			}
		}
		maskBlockSizes = new int[maskBlockCount];
		for (int i = 0; i < maskBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				maskBlockSizes[i] = Integer.reverseBytes(raf.readInt());
			} else {
				maskBlockSizes[i] = raf.readInt();
			}
		}
		headerSize = 8 * (nBlockCount + maskBlockCount + 2);
	}


	/**
	 * @return the headerSize of the sequence
	 */
	public final int getHeaderSize() {
		return headerSize;
	}


	/**
	 * @return the name of the sequence
	 */
	public final String getName() {
		return name;
	}


	/**
	 * @return the offset of the sequence
	 */
	public final int getOffset() {
		return offset;
	}


	/**
	 * @return the dnaSize of the sequence
	 */
	public final int getDnaSize() {
		return dnaSize;
	}


	/**
	 * @return the nBlockStarts of the sequence
	 */
	public final int[] getnBlockStarts() {
		return nBlockStarts;
	}


	/**
	 * @return the nBlockSizes of the sequence
	 */
	public final int[] getnBlockSizes() {
		return nBlockSizes;
	}


	/**
	 * @return the maskBlockStarts of the sequence
	 */
	public final int[] getMaskBlockStarts() {
		return maskBlockStarts;
	}


	/**
	 * @return the maskBlockSizes of the sequence
	 */
	public final int[] getMaskBlockSizes() {
		return maskBlockSizes;
	}
	
	
	/**
	 * Returns the {@link Nucleotide} at the specified position
	 */
	@Override
	public Nucleotide get(int position) {
		if ((position < 0) || (position > dnaSize)) { 
			return null;
		}
		int i = 0;
		while ((i < nBlockStarts.length) && (nBlockStarts[i] <= position)) {
			if (position < nBlockStarts[i] + nBlockSizes[i]) {
				return Nucleotide.ANY;	
			}
			i++;
		}
		// integer in the file containing the position we look for
		int offsetPosition = (int)(position / 4);
		// position of the nucleotide inside the integer
		int offsetInsideByte = 3 - (position % 4);
		try {
			raf.seek(offsetPosition + offset + headerSize);
			// rotate the result until the two bits we want are on the far right 
			// and then apply a 0x0003 filter
			int result2Bit= Integer.rotateRight(raf.readByte(), offsetInsideByte * 2) & 0x3;
			return Nucleotide.get((byte)result2Bit);
		} catch (IOException e) {
			return null;
		}		
	}


	/**
	 * Returns the number of nucleotides
	 */
	@Override
	public int size() {
		return dnaSize;
	}
	
	
	/**
	 * Methods used for the unserialization of the object.
	 * Since the random access file can't be serialized we try to recreate it if the file path is still the same
	 * See javadocs for more information
	 * @return the unserialized object
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		try {
			raf = new RandomAccessFile(new File(filePath), "r");
			return this;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Stops the extraction of the data
	 */
	@Override
	public void stop() {
		needToBeStopped = true;
	}
}
