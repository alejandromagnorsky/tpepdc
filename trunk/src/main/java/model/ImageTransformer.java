package model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageTransformer {

	public String transform(String imageString) {
		// if (user != null && user.getSettings() != null
		// && user.getSettings().isRotate() != null
		// && user.getSettings().isRotate())

		// POP3Proxy.logger.info(c.getContentTypeHeader());

		BufferedImage image = base64ToImage(imageString);
		BufferedImage out = new BufferedImage(image.getWidth(), image
				.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics g = out.getGraphics();
		Graphics2D graphics = (Graphics2D) g;
		graphics.rotate(Math.PI, image.getWidth() / 2, image.getHeight() / 2);
		graphics.drawImage(image, 0, 0, null);

		// TODO obtener el format
		return imageToBase64(out, "");
	}

	public BufferedImage base64ToImage(String base64String) {
		try {
			byte[] imageInBytes = Base64.decodeBase64(base64String);
			return ImageIO.read(new ByteArrayInputStream(imageInBytes));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Format: png,jpg etc
	private String imageToBase64(BufferedImage image, String format) {
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

	// ORIGINAL

	// public void apply(Message message, User user, ResponseFilter chain) {
	// if (user != null && user.getSettings() != null
	// && user.getSettings().isRotate() != null
	// && user.getSettings().isRotate())
	// for (Content c : message.getContents()) {
	// if (c.getType().equals(Content.Type.IMAGE)) {
	//
	// POP3Proxy.logger.info(c.getContentTypeHeader());
	// ImageContent iContent = (ImageContent) c;
	//
	// BufferedImage image = iContent.getImage();
	// BufferedImage out = new BufferedImage(image.getWidth(),
	// image.getHeight(), BufferedImage.TYPE_INT_ARGB);
	//
	// Graphics g = out.getGraphics();
	// Graphics2D graphics = (Graphics2D) g;
	// graphics.rotate(Math.PI, image.getWidth() / 2, image
	// .getHeight() / 2);
	// graphics.drawImage(image, 0, 0, null);
	//
	// iContent.setImage(out);
	// }
	// }
	// chain.doFilter(message, user);
	// }
}
