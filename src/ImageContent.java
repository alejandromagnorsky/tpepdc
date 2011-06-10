import javax.imageio.ImageIO;


public class ImageContent extends Content{

	private ImageIO image;	
	
	public ImageContent(String contentTypeHeader) {
		super(contentTypeHeader);
		this.setType(Type.IMAGE);
	}

	public ImageIO getImage() {
		return image;
	}

	public void setImage(ImageIO image) {
		this.image = image;
	}
	
}
