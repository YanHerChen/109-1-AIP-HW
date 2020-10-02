import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.JPanel;

public class drawHistogram extends JPanel {

	protected static final int MIN_BAR_WIDTH = 4;
	private int[][] colorBins;

	public drawHistogram(int[][] colorBins) {
		this.colorBins = colorBins;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int xOffset = 5;
		int yOffset = 5;
		int width = getWidth() - 1 - (xOffset * 2);
		int height = getHeight() - 1 - (yOffset * 2);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawRect(xOffset, yOffset, width, height);
		int barWidth = 2;

		// initail
		int[] grayLevel = new int[256];
		for (int i = 0; i < grayLevel.length; i++) {
			grayLevel[i] = 0;
		}

		// RGB平均
		int gray = 0;
		for (int j = 0; j < colorBins[0].length; j++) {
			gray = (colorBins[0][j] + colorBins[1][j] + colorBins[2][j]) / 3;
			grayLevel[gray]++;
		}

		// maxValue
		int maxValue = 0;
		for (int i = 0; i < grayLevel.length; i++) {
			if (maxValue < grayLevel[i]) {
				maxValue = grayLevel[i];
			}
		}

		int scaling = maxValue / height + 1;
		int xPos = xOffset;
		for (int i = 0; i < grayLevel.length; i++) {
			int barHeight = grayLevel[i];
			int yPos = height + yOffset - (barHeight / scaling);

			Rectangle2D bar = new Rectangle2D.Float(xPos, yPos, barWidth, barHeight);
			g2d.fill(bar);
			g2d.setColor(Color.DARK_GRAY);
			g2d.draw(bar);
			xPos += barWidth;
		}
		g2d.dispose();
	}
}