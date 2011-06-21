package model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

public class ImageTransformer {

	public static Logger logger = Logger.getLogger("logger");

	public String transform(String imageStringInput, String imageType) {

		try {

			InputStream s = new ByteArrayInputStream(imageStringInput.getBytes());
			InputStream decoder = MimeUtility.decode(s, "base64");

			BufferedImage image = ImageIO.read(decoder);

			BufferedImage transformed = new BufferedImage(image.getWidth(),
					image.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics g = transformed.getGraphics();
			Graphics2D graphics = (Graphics2D) g;
			graphics.rotate(Math.PI, image.getWidth() / 2,
					image.getHeight() / 2);
			graphics.drawImage(image, 0, 0, null);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStream encoder = MimeUtility.encode(out, "base64");

			ImageIO.write(transformed, imageType, encoder);

			byte[] buf = out.toByteArray();
			return new String(buf);
		} catch (Exception e) {
			logger.fatal("Error transforming image in format "+ imageType +", returning original one.");
		}
		return imageStringInput;
	}

}
