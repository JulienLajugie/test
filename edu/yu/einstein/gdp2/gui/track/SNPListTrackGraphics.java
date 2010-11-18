/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.enums.Nucleotide;
import yu.einstein.gdp2.util.ColorConverters;


/**
 *  A {@link TrackGraphics} part of a track showing SNPS
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPListTrackGraphics extends TrackGraphics<SNPList> {

	private static final long serialVersionUID = -5740813392910733205L; 				// generated ID	
	private static final DecimalFormat 	COUNT_FORMAT = new DecimalFormat("###,###,###");// format for the count
	private static final Color 			BACKGROUND_COLOR = new Color(255, 200, 165);	// color of the stripes in the background
	private static final Color			NOT_SIGNIFICANT_COLOR = Color.GRAY;				// color of a not significant base
	private static final Nucleotide[] 	LINE_BASES = 
	{Nucleotide.ADENINE, Nucleotide.CYTOSINE, Nucleotide.GUANINE, Nucleotide.THYMINE};	// bases ordered the way they are printed on the track
	private SNP 						snpUnderMouse = null;							// snp under the mouse cursor, null if none 
	

	/**
	 * Creates an instance of {@link SNPListTrackGraphics}
	 * @param displayedGenomeWindow
	 * @param data
	 */
	protected SNPListTrackGraphics(GenomeWindow displayedGenomeWindow, SNPList data) {
		super(displayedGenomeWindow, data);
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawBackground(g);
		drawVerticalLines(g);
		drawMiddleVerticalLine(g);
		drawSNP(g);
		drawNucleotide(g);
		if (snpUnderMouse != null) {
			drawSNPUnderMouse(g);
		}
		drawStripes(g);
		drawName(g);
	}


	private void drawSNPUnderMouse(Graphics g) {
		// x position of the stripe
		int xPos = genomePosToScreenPos(snpUnderMouse.getPosition());
		// width of the stripe
		int width = twoGenomePosToScreenWidth(snpUnderMouse.getPosition(), snpUnderMouse.getPosition() + 1);
		Color color = new Color(150, 150, 150, 100);
		g.setColor(color);
		g.fillRect(xPos, 0, width, getHeight());		
	}


	/**
	 * Draws the nucleotide name on the left of the track
	 * @param g {@link Graphics}
	 */
	private void drawNucleotide(Graphics g) {
		double halfFontHeight = g.getFontMetrics().getHeight() / 2d;
		double lineHeight = getHeight() / 4d;
		double halfLineHeight = getHeight() / 8d;
		int leftXPos = 5;
		int rightXPos = getWidth() - 10;
		for (int i = 0; i < 4; i++) {
			int yPos = (int) (lineHeight * i + halfLineHeight + halfFontHeight);
			Nucleotide nucleotideToPrint = LINE_BASES[i];
			g.setColor(ColorConverters.nucleotideToColor(nucleotideToPrint));
			g.drawString(nucleotideToPrint.toString(), leftXPos, yPos);
			g.drawString(nucleotideToPrint.toString(), rightXPos, yPos);			
		}
	}


	/**
	 * Draws the background of the track
	 * @param g {@link Graphics}
	 */
	private void drawBackground(Graphics g) {
		int width = getWidth();
		double lineHeight = getHeight() / 4d;
		g.setColor(BACKGROUND_COLOR);
		for (int i = 0; i < 4; i++) {
			if (i % 2 == 1) {
				g.fillRect(0, (int) (i * lineHeight), width, (int) Math.ceil(lineHeight));
			}
		}
	}


	/**
	 * Draws the SNPs
	 * @param g {@link Graphics}
	 */
	private void drawSNP(Graphics g) {
		List<SNP> snpList = data.getFittedData(genomeWindow, xFactor);
		if ((snpList != null) && (snpList.size() > 0)) {
			// loop for each SNPs
			for (int i = 0; i < snpList.size(); i++) {
				// retrieve current SNP
				SNP currentSNP = snpList.get(i);
				// format base counts as strings 
				String firstBaseString = COUNT_FORMAT.format(currentSNP.getFirstBaseCount());
				String secondBaseString = COUNT_FORMAT.format(currentSNP.getSecondBaseCount());
				// compute the largest string width to print for the current SNP
				int firstBaseWidth = g.getFontMetrics().stringWidth(firstBaseString);
				int secondBaseWidth = g.getFontMetrics().stringWidth(secondBaseString);
				int widthSNP = Math.max(firstBaseWidth, secondBaseWidth);
				// check if there is enough space to print the values for the current SNP
				drawDenseSNP(g, currentSNP);
				if (xFactor / widthSNP > 1) {
					drawDetailedSNP(g, currentSNP);
				} 
			}	
		}
	}


	/**
	 * Draws the dense representation of a specified SNP
	 * @param g {@link Graphics}
	 * @param currentSNP {@link SNP} to draw
	 */
	private void drawDenseSNP(Graphics g, SNP currentSNP) {
		// x position of the stripe
		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		// width of the stripe
		int width = twoGenomePosToScreenWidth(currentSNP.getPosition(), currentSNP.getPosition() + 1);
		// height of the stripe
		double lineHeight = getHeight() / 4d;
		// we search for the y position of the stripe for the first base
		int yPos = 0;
		switch (currentSNP.getFirstBase()) {
		case ADENINE:
			yPos = 0;
			break;
		case CYTOSINE:
			yPos = (int) lineHeight;
			break;
		case GUANINE:
			yPos = (int) (2 * lineHeight);
			break;
		case THYMINE:
			yPos = (int) (3 * lineHeight);
			break;
		}
		// we set the color of the SNP depending on the type of the first base
		Color snpColor = ColorConverters.nucleotideToColor(currentSNP.getFirstBase()); 
		g.setColor(snpColor);
		// we draw the first base
		g.fillRect(xPos, yPos, width, (int) Math.ceil(lineHeight));
		if (width >= 5) {
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yPos, width - 1, (int) Math.ceil(lineHeight) - 1);
		}
		
		// we search for the second base y position
		yPos = 0;
		switch (currentSNP.getSecondBase()) {
		case ADENINE:
			yPos = 0;
			break;
		case CYTOSINE:
			yPos = (int) lineHeight;
			break;
		case GUANINE:
			yPos = (int) (2 * lineHeight);
			break;
		case THYMINE:
			yPos = (int) (3 * lineHeight);
			break;
		}
		// we set the color and draw the second base
		if (currentSNP.isSecondBaseSignificant()) {
			snpColor = ColorConverters.nucleotideToColor(currentSNP.getSecondBase()); 
		} else {
			// if the second base is not significant we draw it in gray
			snpColor = NOT_SIGNIFICANT_COLOR; 
		}				
		g.setColor(snpColor);
		g.fillRect(xPos, yPos, width, (int) Math.ceil(lineHeight));
		if (width >= 5) {
			g.setColor(Color.BLACK);
			g.drawRect(xPos, yPos, width - 1, (int) Math.ceil(lineHeight) - 1);
		}
	}


	/**
	 * Draws the detailed representation of a specified SNP
	 * @param g {@link Graphics}
	 * @param currentSNP {@link SNP} to draw
	 */
	private void drawDetailedSNP(Graphics g, SNP currentSNP) {		
		g.setColor(Color.WHITE);
		// half height of the font
		int halfFontHeight = g.getFontMetrics().getHeight() / 2;
		// height of a line 
		int lineHeight = getHeight() / 4;		
		int halfLineHeight = getHeight() / 8;
		int xPos = genomePosToScreenPos(currentSNP.getPosition());
		int yPos = 0;
		// draw first base
		switch (currentSNP.getFirstBase()) {
		case ADENINE:
			yPos = halfLineHeight + halfFontHeight;
			break;
		case CYTOSINE:
			yPos = halfLineHeight + halfFontHeight + lineHeight;
			break;
		case GUANINE:
			yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
			break;
		case THYMINE:
			yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
			break;
		}
		String countStr = COUNT_FORMAT.format(currentSNP.getFirstBaseCount());
		g.drawString(countStr, xPos, yPos);

		// draw second base
		switch (currentSNP.getSecondBase()) {
		case ADENINE:
			yPos = halfLineHeight + halfFontHeight;
			break;
		case CYTOSINE:
			yPos = halfLineHeight + halfFontHeight + lineHeight;
			break;
		case GUANINE:
			yPos = halfLineHeight + halfFontHeight + 2 * lineHeight;
			break;
		case THYMINE:
			yPos = halfLineHeight + halfFontHeight + 3 * lineHeight;
			break;
		}
		countStr = COUNT_FORMAT.format(currentSNP.getSecondBaseCount());
		g.drawString(countStr, xPos, yPos);
	}
	
	
	/**
	 * Resets the tooltip and the highlighted base when the mouse exits the track
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		if (snpUnderMouse != null) {
			snpUnderMouse = null;
			setToolTipText(null);
			repaint();
		}
	}
	
	
	/**
	 * Sets the mouse cursor, the tooltip and the SNP with the mouse over when the mouse move
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		//long baseToPrintCount = genomeWindow.getSize();
		SNP oldSnpUnderMouse = snpUnderMouse;
		snpUnderMouse = null;		
		if (!getScrollMode()) {
			// if the zoom is too out we can't print the bases and so there is none under the mouse
			//if (baseToPrintCount <= getWidth()) {
				// retrieve the position of the mouse
				Point mousePosition = e.getPoint();
				// retrieve the list of the printed nucleotides
				List<SNP> printedSNPs = data.getFittedData(genomeWindow, xFactor);
				// do nothing if there is no genes
				if (printedSNPs != null) {
					int i = 0;
					while ((i < printedSNPs.size()) && (snpUnderMouse == null)) {
						SNP currentSNP = printedSNPs.get(i);
						if ((mousePosition.x >= genomePosToScreenPos(currentSNP.getPosition())) &&
								(mousePosition.x <= genomePosToScreenPos(currentSNP.getPosition() + 1))) {
							// we found a gene under the mouse
							snpUnderMouse = currentSNP;
						}
						i++;
					}					
					// we repaint the track only if the gene under the mouse changed
					if (((oldSnpUnderMouse == null) && (snpUnderMouse != null)) 
							|| ((oldSnpUnderMouse != null) && (!oldSnpUnderMouse.equals(snpUnderMouse)))) {
						repaint();
					}				
				//}
			}
			if (snpUnderMouse != null) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setToolTipText(snpUnderMouse.getFirstBase() + " = " + snpUnderMouse.getFirstBaseCount() +
						", " + snpUnderMouse.getSecondBase() + " = " + snpUnderMouse.getSecondBaseCount());
			} else {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				setToolTipText(null);
			}
		}
	}
}