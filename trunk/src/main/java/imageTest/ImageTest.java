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
			BufferedReader reader = new BufferedReader(new FileReader(
					"image.txt"));
			String imageString = "";
			String temp;
			while ((temp = reader.readLine()) != null) {
				imageString += temp;
			}
			
			
			BufferedImage image = base64ToImage(imageString);
			// ImageTransformerFilter rotate = new ImageTransformerFilter();
			// rotate.apply(message, dao.getUser("tpepdc"),
			// new NullResponseFilter());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			image = ImageIO.read(bais);

			String toPrint = imageToBase64(image, "png");
			System.out.println(toPrint);
			
			
			// WORKING: imageString -> encoded -> decoded -> imageString
//			String encoded = encode(imageString);
//			String decoded = decode(encoded);
//			
//			System.out.println(decoded);
			
			// NOT WORKING: imageString -> decoded -> encoded -> imageString
//			String decoded = decode(imageString);
//			String encoded = encode(decoded);
//			
//			System.out.println(encoded);

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
			return toString(Base64.encodeBase64Chunked(baos.toByteArray()));
			// return new String(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String delEnters(String str) {
		String ret = "";
		for (char c : str.toCharArray()) {
			if (c != '\n') {
				ret += c;
			}
		}
		return ret;
	}

	public static String encode(String stText) {
		return toString(Base64.encodeBase64Chunked(stText.getBytes()));
	}

	public static String decode(String stBase64Encoded) {
		return toString(Base64.decodeBase64(stBase64Encoded.getBytes()));
	}

	private static String toString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length);
		for (byte b : bytes) {
			sb.append(Character.toString((char) b));
		}
		return sb.toString();
	}

}
