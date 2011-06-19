package imageTest;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageTest {

	public static void main(String args[]) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader("image.txt"));
			String imageString = "";
			String temp;
			while((temp = reader.readLine()) != null) {
				imageString += temp;
			}
			BufferedImage image = base64ToImage(imageString);
			// ImageTransformerFilter rotate = new ImageTransformerFilter();
			// rotate.apply(message, dao.getUser("tpepdc"),
			// new NullResponseFilter());
			ImageIO.write(image, "png", new File("image.png"));
			
			String toPrint = imageToBase64(image, "png");
			System.out.println(toPrint);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decodeBase64(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Format: png,jpg etc
	private static String imageToBase64(BufferedImage image, String format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, baos);
			byte[] buf = baos.toByteArray();
			return Base64.encodeBase64String(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
