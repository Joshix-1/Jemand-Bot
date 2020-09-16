package Jemand;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.util.DiscordRegexPattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class Memes {
    public static final String DONALD1 = "Donald1";
    public static final String KALENDER = "Kalender";
    public static final String DRAKE1 = "Drake1";
    public static final String LISA_PRESENTATION = "Lisa1";
    public static final String WINNIE_2 = "Winnie1";
    public static final String WINNIE_3 = "Winnie2";
    public static final String WORSE_THAN_HITLER = "Wth";
    public static final String KALENDER2 = "Kalender2";
    public static final String ZITAT_ATA = "AtaZitatBild";

    private static final Font[] fonts = {
            getFontByName("Regular"),
            getFontByName("Bold"),
            getFontByName("Impact"),
            getFontByName("Calligraphy"),
            getFontByName("CondensedLight")}; //maybe CondensedExtraLight


    private static Font emoji = getFontByName("Emoji");

    private int[][] coordinates; //{{x, x2, y, y2}, {x, x2, y, y2}}
    private BufferedImage template;
    private BufferedImage[] images;

    Memes(String template, URL... image) throws IOException {
        this(template, convertURLs(image));
    }

    public Memes(String template, String... text) throws IOException {
        this(template, convertStrings(text, getCoordinates(template)));
    }

    Memes(String template, BufferedImage... images) throws IOException {
        this.images = images;
        coordinates = getCoordinates(template);
        this.template = ImageIO.read(func.fileOfName("templates/" + template + ".png")); //gets the files in the templates folder the function basically replaces "/" with "\\" when on windows
    }

    static int[][] getCoordinates(String template) {
        int[][] coordinates = null;
        switch (template) {
            //coordinates = new int[][]{{0, 100, 0, 100, Bg-Color-RGB, Text-COlor.RGB, FontIndex}, {0, 100, 100, 200, Bg-Color-RGB, Text-COlor.RGB}};
            case DONALD1:
                coordinates = new int[][]{
                        {36, 233, 85, 250, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 0}
                };
                break;
            case KALENDER:
                coordinates = new int[][]{
                        {7, 502, 7, 276, Color.WHITE.getRGB(), new Color(203, 38, 44).getRGB(), 1},
                        {27, 482, 300, 365, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 0},
                        {75,435,456,526, Color.WHITE.getRGB(), new Color(27, 144, 254).getRGB(), 0}
                };
                break;
            case DRAKE1:
                coordinates = new int[][]{
                        {600, 1200 - 5, 0, 600, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 2},
                        {600, 1200 - 5, 600, 1200, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 2}
                };
                break;
            case LISA_PRESENTATION:
                coordinates = new int[][]{
                        {339, 1300, 0, 516, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 0}
                };
                break;
            case WINNIE_2:
                coordinates = new int[][]{
                        {346, 800 - 5, 0, 294, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 2},
                        {346, 800 - 5, 297, 582, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 3}
                };
                break;
            case WINNIE_3:
                coordinates = new int[][]{
                        {298, 666 - 5, 0, 235, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 2},
                        {298, 666 - 5, 238, 469, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 2},
                        {298, 666 - 5, 472, 704, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 3}
                };
                break;
            case WORSE_THAN_HITLER:
                coordinates = new int[][] {
                        {47, 184, 32, 191, Color.black.getRGB(), Color.WHITE.getRGB(), 0}
                };
                break;
            case KALENDER2:
                coordinates = new int[][]{
                        {16 + 12, 384 - 12, 55, 305, Color.WHITE.getRGB(), new Color(225, 122, 17).getRGB(), 1},
                        {16 + 12, 384 - 12, 305 + 15, 305 + 55, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 4},
                        {16 + 12 ,384 - 12, 396 + 10, 457-10, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 0}
                };
                break;
            case ZITAT_ATA:
                coordinates = new int[][] {
                        {20, 1191- 20, 20, 1111, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 1},
                        {420, 1191- 30,1234, 1684, Color.WHITE.getRGB(), Color.BLACK.getRGB(), 1},
                };
                break;
        }
        return coordinates;
    }

    public Optional<BufferedImage> getFinalMeme() {
        if (coordinates.length > images.length) return Optional.empty();

        BufferedImage copy = new BufferedImage(template.getWidth(), template.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                copy.setRGB(x,y, template.getRGB(x, y));
            }
        }

        Graphics2D g = copy.createGraphics();
        for (int i = 0; i < coordinates.length; i++) {
            int w = coordinates[i][1] - coordinates[i][0];
            int h = coordinates[i][3] - coordinates[i][2];
            ImageResizer ir = null;
            try {
                ir = new ImageResizer(images[i], "image.png");
                images[i] = ir.resize(w, h).getBufferedImage();
                ir.dispose();
            } catch (IOException ignored) { }

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h;y++) {
                    if(coordinates[i][4] != images[i].getRGB(x, y)) {
                        g.setColor(new Color(images[i].getRGB(x, y)));
                        g.drawLine(x + coordinates[i][0], y + coordinates[i][2], x + coordinates[i][0], y + coordinates[i][2]);
                        //g.fillRect(x + coordinates[i][0], y + coordinates[i][2], 1, 1);
                    }
                }
            }
        }
        g.dispose();
        return Optional.of(copy);
    }

    static BufferedImage[] convertStrings(String[] text, int[][] coordinates) {
        int pixels_between_lines = 4;

        BufferedImage[] images = new BufferedImage[coordinates.length];

        for (int i = 0; i < text.length && i < coordinates.length; i++) {
            String withoutWhite = func.WHITE_SPACE.matcher(text[i]).replaceAll(""); //= Pattern.compile("\\s+");
            if(func.isURL(withoutWhite)) { //function that checks if a String is a URL
                try {
                    images[i] = ImageIO.read(new URL(withoutWhite));
                } catch (IOException ignored) {
                    images[i] = null;
                }
            }
            if(images[i] == null) {
                text[i] = toUnicode(text[i]);

                int w = coordinates[i][1] - coordinates[i][0];
                int h = coordinates[i][3] - coordinates[i][2];

                images[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g = images[i].createGraphics();

                g.setColor(new Color(coordinates[i][4]));
                g.fillRect(0, 0, images[i].getWidth(), images[i].getHeight());

                String[] ltxt = func.WHITE_SPACE.split(text[i]); //= Pattern.compile("\\s+");
                Font f = fonts[coordinates[i][6]];
                int fontSize;
                int actualRows = 1;
                for (fontSize = 6; fontSize < h; fontSize++) {
                    g.setFont(f.deriveFont((float) fontSize));
                    int max_rows = (h - fontSize/2) / (fontSize + pixels_between_lines);

                    StringBuilder sb = new StringBuilder();

                    boolean b1 = false;
                    int index = 0;
                    int j = 0;
                    while (index < ltxt.length && j < max_rows) {
                        if (stringWidth(g, ltxt[index]) > w - 6) {
                            b1 = true;
                            break;
                        } else if (stringWidth(g, sb.append(" ").append(ltxt[index]).toString()) < w)
                            index++;
                        else {
                            j++;
                            sb = new StringBuilder();
                        }
                    }

                    if (j > max_rows - 1 || b1) break;
                    else actualRows = j;
                }

                f = f.deriveFont((float) fontSize - 1f);
                Font emoji = Memes.emoji.deriveFont((float) fontSize);

                StringBuilder sb = new StringBuilder();
                g.setColor(new Color(coordinates[i][5]));
                g.setFont(f);
                int y = ((fontSize + h - (actualRows * (pixels_between_lines  + fontSize)))/2);

                int index = 0;
                while (index < ltxt.length) {
                    int strw = stringWidth(g, sb + " " + ltxt[index]);
                    if (strw < w) {
                        sb.append(" ").append(ltxt[index]);
                        index++;
                        if (index == ltxt.length) {
                            drawString(g, sb.toString(), emoji, 3 + (w - strw) / 2, y);
                        }
                    } else {
                        strw = stringWidth(g, sb.toString());
                        drawString(g, sb.toString(), emoji, 3 + (w - strw) / 2, y);
                        y += fontSize + pixels_between_lines;
                        sb = new StringBuilder();
                    }
                }
                g.dispose();
            }
        }
        return images;
    }

    static String toUnicode(String str) {
        return EmojiParser.parseToUnicode(str);
    }

    static Graphics2D drawString(Graphics2D g, String str, Font fallback, int posX, int posY) {
        char[] c =  str.toCharArray();

        Font f = g.getFont();
        fallback = fallback.deriveFont((float) f.getSize());

        List<String> emojis = EmojiParser.extractEmojis(str);

        StringBuilder sb = new StringBuilder(str.length());

        boolean b1;
        for (int i = 0; i < str.length(); i++) {
            if (!f.canDisplay(c[i])) {
                if(Memes.fonts[1].canDisplay(c[i]) && sb.length() == 0) {
                    g.setFont(Memes.fonts[1].deriveFont((float) f.getSize()));
                    g.drawGlyphVector(g.getFont().createGlyphVector(g.getFontRenderContext(), String.valueOf(c[i])), posX, posY);
                    posX += g.getFontMetrics().charWidth(c[i]);
                    g.setFont(f);
                    b1 = false;
                } else {
                    sb.append(c[i]);
                    //if(i > 0 && Fitzpatrick.fitzpatrickFromUnicode(String.valueOf(c[i-1]) + c[i]) != null) b1 = true;
                    //else
                    //    if(emojis.contains(sb.toString())) {
                    //    b1 = (i >= str.length() - 2) || Fitzpatrick.fitzpatrickFromUnicode(c[i + 1] + String.valueOf(c[i + 2])) == null;
                    //}
                    b1 = emojis.contains(sb.toString());
                }
            } else b1 = sb.length() > 0;

            if(b1) {
                g.drawGlyphVector(fallback.createGlyphVector(g.getFontRenderContext(), sb.toString()), posX, posY);
                g.setFont(fallback);
                posX += g.getFontMetrics().stringWidth(sb.toString());
                g.setFont(f);
                sb = new StringBuilder(str.length()-i);
            }
            if(f.canDisplay(c[i])) {
                g.drawGlyphVector(f.createGlyphVector(g.getFontRenderContext(), String.valueOf(c[i])), posX, posY);
                posX += g.getFontMetrics().charWidth(c[i]);
            }
        }
        if(sb.length() > 0)
            g.drawGlyphVector(fallback.createGlyphVector(g.getFontRenderContext(), sb.toString()), posX, posY);
        return g;
    }

    static int stringWidth(Graphics2D g,  String str) {
        int w = 0;

        char[] c = str.toCharArray();
        Font f = g.getFont();
        Font e = emoji.deriveFont((float) f.getSize());

        for (int i = 0; i < c.length; i++) {
            g.setFont(f);
            if(!f.canDisplay(c[i]))
                g.setFont(e);

            w += g.getFontMetrics().charWidth(c[i]);
        }
        g.setFont(f);
        return w;
    }

    static BufferedImage[] convertURLs(URL... urls) throws IOException {
        BufferedImage[] images = new BufferedImage[urls.length];
        for (int i = 0; i < urls.length; i++)
            images[i] = ImageIO.read(urls[i]);
        return images;
    }

    static Font getFontByName(String name) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, func.fileOfName("templates/" + name + ".ttf")); //gets the files in the templates folder the function basically replaces "/" with "\\" when on windows
        } catch(IOException | FontFormatException e) {
            return new Font("Arial", Font.PLAIN,5);
        }
    }
}
