package imageTest;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

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

			InputStream s = new ByteArrayInputStream(imageString.getBytes());
			InputStream decoder = MimeUtility.decode(s, "base64");

			String tmp = "";
			int c;
			while ((c = decoder.read()) != -1)
				tmp += (char) c;

			byte[] imageInBytes = tmp.getBytes();
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(
					imageInBytes));

			BufferedImage transformed = new BufferedImage(image.getWidth(),
					image.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics g = transformed.getGraphics();
			Graphics2D graphics = (Graphics2D) g;
			graphics.rotate(Math.PI, image.getWidth() / 2,
					image.getHeight() / 2);
			graphics.drawImage(image, 0, 0, null);

			ImageIO.write(transformed, "png", new File("transformada.png"));

			OutputStream out = new FileOutputStream("image_encodeada.txt");
			OutputStream encoder = MimeUtility.encode(out, "base64");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(transformed, "PNG", baos);
			byte[] transfImageBytes = baos.toByteArray();

			encoder.write(transfImageBytes);

			// BufferedImage image = base64ToImage(imageString);
			// ImageTransformerFilter rotate = new ImageTransformerFilter();
			// rotate.apply(message, dao.getUser("tpepdc"),
			// new NullResponseFilter());
			// ImageIO.write(image, "png", new File("TallerSistemasRep.png"));

			// System.out.println("el string de image.txt es base 64?: "
			// + Base64.isBase64(imageString));
			//
			// byte[] image = Base64.decodeBase64(imageString.getBytes());

			// System.out.println(new
			// String(Base64.encodeBase64Chunked(image)));
			//
			// BufferedImage buff = ImageIO.read(new
			// ByteArrayInputStream(image));
			//
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// ImageIO.write(buff, "png", baos);
			// byte[] buf = baos.toByteArray();
			// System.out.println(new String(Base64.encodeBase64Chunked(buf)));

			// BufferedImage im = ImageIO.read(new File("image.png"));
			//			
			//
			// String toPrint = imageToBase64(im, "png");
			// BufferedImage asiQueda = base64ToImage(toPrint);
			// ImageIO.write(asiQueda, "png", new File("asiQueda.png"));
			//			
			// String ultima = imageToBase64(asiQueda, "png");
			//			
			// System.out.println("original:     " + imageString);
			// System.out.println("transformada: " + toPrint);
			// System.out.println("ultima:       " + ultima);
			// System.out.println("son iguales?: " +
			// imageString.equals(toPrint));

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

	// private static int BUFFER_SIZE = 8192;
	// public static void main(String args[]) {
	//
	// try {
	// BufferedReader reader = new BufferedReader(new FileReader(
	// "image.txt"));
	// String imageString = "";
	// String temp;
	// while ((temp = reader.readLine()) != null) {
	// imageString += temp;
	// }
	//			
	//			
	// BufferedImage image = base64ToImage(imageString);
	// // ImageTransformerFilter rotate = new ImageTransformerFilter();
	// // rotate.apply(message, dao.getUser("tpepdc"),
	// // new NullResponseFilter());
	// // ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// // ImageIO.write(image, "png", baos);
	// // ByteArrayInputStream bais = new
	// ByteArrayInputStream(baos.toByteArray());
	// // ImageIO.write(image, "png", bais);
	//
	// String toPrint = imageToBase64(image, "png");
	// System.out.println(toPrint);
	//			
	//			
	// // WORKING: imageString -> encoded -> decoded -> imageString
	// // String encoded = encode(imageString);
	// // String decoded = decode(encoded);
	// //
	// // System.out.println(decoded);
	//			
	// // NOT WORKING: imageString -> decoded -> encoded -> imageString
	// // String decoded = decode(imageString);
	// // String encoded = encode(decoded);
	// //
	// // System.out.println(encoded);
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// private static BufferedImage base64ToImage(String base64String) {
	// try {
	// byte[] imageInBytes = Base64.decodeBase64(base64String);
	// return ImageIO.read(new ByteArrayInputStream(imageInBytes));
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }
	//	
	// public static String decode(String stBase64Encoded) {
	// return toString(Base64.decodeBase64(stBase64Encoded.getBytes()));
	// }
	//
	// // Format: png,jpg etc
	// private static String imageToBase64(BufferedImage image, String format) {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// try {
	// ImageIO.write(image, format, baos);
	// baos.flush();
	// byte[] buf = baos.toByteArray();
	// return toString(Base64.encodeBase64Chunked(buf));
	// // return new String(buf);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//	
	// public static String encode(String stText) {
	// return toString(Base64.encodeBase64Chunked(stText.getBytes()));
	// }
	//
	// private static String delEnters(String str) {
	// String ret = "";
	// for (char c : str.toCharArray()) {
	// if (c != '\n') {
	// ret += c;
	// }
	// }
	// return ret;
	// }
	//
	// private static String toString(byte[] bytes) {
	// StringBuilder sb = new StringBuilder(bytes.length);
	// for (byte b : bytes) {
	// sb.append(Character.toString((char) b));
	// }
	// return sb.toString();
	// }
	//	
	// public static String encode(InputStream stream) throws IOException {
	// List<byte[]> buffers = new LinkedList<byte[]>();
	// int mark = 0;
	// try {
	// stream = new BufferedInputStream(stream);
	// byte[] buffer = new byte[BUFFER_SIZE];
	// buffers.add(buffer);
	// int avail = -1;
	// while (avail != 0) {
	// avail = stream.available();
	// if (avail > 0) {
	// int toBeRead = Math.min(avail, BUFFER_SIZE - mark);
	// stream.read(buffer, mark, toBeRead);
	// mark += toBeRead;
	// }
	// if (mark == BUFFER_SIZE) {
	// buffer = new byte[BUFFER_SIZE];
	// buffers.add(buffer);
	// mark = 0;
	// }
	// }
	// } finally {
	// stream.close();
	// }
	//
	// byte[] result = new byte[(buffers.size() - 1) * BUFFER_SIZE + mark];
	// int i = 0;
	// for (byte[] buffer2 : buffers) {
	// int len = i == buffers.size() - 1 ? mark : BUFFER_SIZE;
	// System.arraycopy(buffer2, 0, result, i * BUFFER_SIZE, len);
	// i++;
	// }
	// return new String(Base64.encodeBase64(result));
	// }

}