package filter;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Message;

import proxy.Content;
import proxy.ImageContent;
import proxy.POP3Proxy;



public class ImageTransformerFilter implements Filter {

	public void apply(Message message) {
		for (Content c : message.getContents()) {
			if (c.getType().equals(Content.Type.IMAGE)) {

				POP3Proxy.logger.info(c.getContentTypeHeader());
				ImageContent iContent = (ImageContent) c;

				BufferedImage image = iContent.getImage();
				BufferedImage out = new BufferedImage(image.getWidth(),
						image.getHeight(), BufferedImage.TYPE_INT_ARGB);

				Graphics g = out.getGraphics();
				Graphics2D graphics = (Graphics2D) g;
				graphics.rotate(Math.PI, image.getWidth() / 2,
						image.getHeight() / 2);
				graphics.drawImage(image, 0, 0, null);

				iContent.setImage(out);
			}
		}
	}
}
