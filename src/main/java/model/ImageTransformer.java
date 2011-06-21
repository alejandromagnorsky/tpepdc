package model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

public class ImageTransformer {
	
	public static Logger logger = Logger.getLogger("logger");

	public String transform(String imageStringInput, String imageType) {

		try {
			
			BufferedReader reader = new BufferedReader(new StringReader(
					imageStringInput));
			String imageString = "";
			String temp;
			while ((temp = reader.readLine()) != null) {
				imageString += temp;
			}

			InputStream s = new ByteArrayInputStream(imageString.getBytes());
			InputStream decoder = new BASE64DecoderStream(s);

//			String tmp = "";
//			int c;
//			while ((c = decoder.read()) != -1)
//				tmp += (char) c;
//
//			byte[] imageInBytes = tmp.getBytes("iso-8859-1");
//			BufferedImage image = ImageIO.read(new ByteArrayInputStream(
//					imageInBytes));
			BufferedImage image = ImageIO.read(decoder);

			BufferedImage transformed = new BufferedImage(image.getWidth(),
					image.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics g = transformed.getGraphics();
			Graphics2D graphics = (Graphics2D) g;
			graphics.rotate(Math.PI, image.getWidth() / 2,
					image.getHeight() / 2);
			graphics.drawImage(image, 0, 0, null);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStream encoder = new BASE64EncoderStream(out);

			ImageIO.write(transformed, imageType, encoder);

			byte[] buf = out.toByteArray();
			return new String(buf);
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Error transforming image, returning original one.");
		}
		return imageStringInput;
	}

}
