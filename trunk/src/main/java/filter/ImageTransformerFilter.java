package filter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Content;
import model.ImageContent;
import model.Message;
import model.User;
import proxy.POP3Proxy;

public class ImageTransformerFilter extends ResponseFilter {

	public void apply(Message message, User user, ResponseFilter chain) {
		if (user != null && user.getSettings() != null
				&& user.getSettings().isRotate() != null 
				&& user.getSettings().isRotate())
			for (Content c : message.getContents()) {
				if (c.getType().equals(Content.Type.IMAGE)) {

					POP3Proxy.logger.info(c.getContentTypeHeader());
					ImageContent iContent = (ImageContent) c;

					BufferedImage image = iContent.getImage();
					BufferedImage out = new BufferedImage(image.getWidth(),
							image.getHeight(), BufferedImage.TYPE_INT_ARGB);

					Graphics g = out.getGraphics();
					Graphics2D graphics = (Graphics2D) g;
					graphics.rotate(Math.PI, image.getWidth() / 2, image
							.getHeight() / 2);
					graphics.drawImage(image, 0, 0, null);

					iContent.setImage(out);
				}
			}
		chain.doFilter(message, user);
	}
}
