import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class drawBMP_edit extends JPanel {
	private BufferedImage image;
	private String path;
	public drawBMP_edit(BufferedImage image, String path) {
		this.image = image;
		this.path = path;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, this);

			try{
		        ImageIO.write(image, "jpg", new File(path.replace(".bmp", ".jpg")));
			}catch(IOException e){}
		}
	}
}