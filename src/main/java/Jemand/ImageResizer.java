package Jemand;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageResizer {
     public static final int MAX_SIZE = 5000;
     private ImageIcon image;
     private String InputName;
     private int width, height;
     private File f = func.tempFile("png");

    public ImageResizer(URL url) throws IOException {
        this(ImageIO.read(url), url.getFile());
    }
    public ImageResizer(String filename) throws IOException {
        this(ImageIO.read(func.fileOfName(filename)), filename);
    }
    public ImageResizer(BufferedImage bufferedImage, String inputName) throws IOException {
        InputName = inputName;
        if(!InputName.contains(".")) InputName = "image.png";
        ImageIO.write(bufferedImage, getFileType().toUpperCase(), f);
        image = new ImageIcon(f.getAbsolutePath());
        setWH(bufferedImage);
    }

    public Image getImage() {
        return image.getImage();
    }

    private void setWH(BufferedImage bufferedImage) {
        if(bufferedImage == null) {
            width = image.getIconWidth();
            height = image.getIconHeight();
        } else {
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
        }

        if(width < 1) width = 1;
        if(height < 1) height = 1;
    }

    public ImageResizer resizeToPixels(int pixelcount) {
        return resizeToPixels(pixelcount, 1.2);
    }

    private ImageResizer resizeToPixels(int pixelcount, double percent) {
        if(pixelcount > MAX_SIZE * MAX_SIZE) pixelcount = MAX_SIZE * MAX_SIZE;
        double ratio = (double) width / (double) height;
        height = (int) (Math.sqrt(pixelcount / ratio));
        width = pixelcount / height;
        return resize(width * percent, height * percent);
    }

    public ImageResizer resizeToMaxSize(int max_bytes) {
        if(max_bytes > 1000) {
            //resizeToPixels(width*height);
            if(getSize() > max_bytes && width * height > max_bytes) resizeToPixels(max_bytes/2, 0.99);
            while (getSize() > max_bytes) {
                resizeToPixels(width * height - 100, 0.9);
            }
        }
        return this;
    }

    public ImageResizer resize(double percent) {
        width *= percent;
        height *= percent;

        return resize(width, height);
    }

    public ImageResizer resize(int width, int height) {

        if (getWidth() == width && getHeight() == height) {
            return this;
        }

        if(width > MAX_SIZE) width = MAX_SIZE;
        else if(width < 1) width = 1;

        if(height > MAX_SIZE) height = MAX_SIZE;
        else if(height < 1) height = 1;


        if(InputName.endsWith("gif"))
            image.setImage(getBufferedImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        else
            image.setImage(getBufferedImage().getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING));


        setWH(null);

        return this;
    }

    public ImageResizer resize(double width, double height) {
        return resize((int) width, (int) height);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
        @return size of image in bytes, {@code -1L} if something went wrong.
     */
    public long getSize() {
        long output = -1L;
        try {
            f.mkdirs();
            ImageIO.write(getBufferedImage(), getFileType(), f);
            if(f.exists()) output = f.length();
        } catch (IOException e) {
            func.handle(e);
        }
        f.delete();
        return output;
    }

    public String getFileType() {
        if(InputName.contains(".")) return InputName.replace("jpeg", "jpg").substring(InputName.lastIndexOf(".") + 1, InputName.lastIndexOf(".") + 4);
        return "png";
    }

    public BufferedImage getBufferedImage(){
        Image img = image.getImage();
        if (img instanceof BufferedImage){
            return (BufferedImage) img;
        }
        //Create a buffered image with transparency
        BufferedImage bimage;
        setWH(null);
        if(getFileType().contains("png")) bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        else bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Draw the image on to te buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        //Return the buffered image
        return bimage;
    }


    public final String toString() {
        return "ImageResizer{" +
                "image=" + image +
                ", InputName='" + InputName + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    public void dispose() {
        if(f.exists()) f.delete();
        image = null;
    }
}
