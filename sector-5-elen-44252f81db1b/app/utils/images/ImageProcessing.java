package utils.images;

import play.libs.Files;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * ImageProcessing.
 *
 * @author Pierre Adam
 * @since 20.07.19
 */
public class ImageProcessing {

    /**
     * Crop and resize input stream.
     *
     * @param file      the file
     * @param selection the selection
     * @param maxSize   the max size
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult cropAndResize(final Files.TemporaryFile file,
                                            final Rectangle selection,
                                            final Dimension maxSize,
                                            final boolean keepRatio) {
        try {
            return cropAndResize(ImageIO.read(file.path().toFile()), selection, maxSize, keepRatio);
        } catch (final IOException ignore) {
            return null;
        }
    }

    /**
     * Crop and resize input stream.
     *
     * @param file      the file
     * @param selection the selection
     * @param maxSize   the max size
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult cropAndResize(final File file,
                                            final Rectangle selection,
                                            final Dimension maxSize,
                                            final boolean keepRatio) {
        try {
            return cropAndResize(ImageIO.read(file), selection, maxSize, keepRatio);
        } catch (final IOException ignore) {
            return null;
        }
    }

    /**
     * Crop and resize input stream.
     *
     * @param image     the image
     * @param selection the selection
     * @param maxSize   the max size
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult cropAndResize(final BufferedImage image,
                                            final Rectangle selection,
                                            final Dimension maxSize,
                                            final boolean keepRatio) {
        ImageResult result = crop(image, selection);
        if (result == null) {
            return null;
        }
        try {
            result = resize(ImageIO.read(result.getDataAsInputStream()), maxSize, keepRatio);
        } catch (final IOException e) {
            return null;
        }
        return result;
    }

    /**
     * Resize input stream.
     *
     * @param file      the file
     * @param boundary  the boundary
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult resize(final Files.TemporaryFile file,
                                     final Dimension boundary,
                                     final Boolean keepRatio) {
        try {
            return resize(ImageIO.read(file.path().toFile()), boundary, keepRatio);
        } catch (final IOException ignore) {
            return null;
        }
    }

    /**
     * Resize input stream.
     *
     * @param file      the file
     * @param boundary  the boundary
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult resize(final File file,
                                     final Dimension boundary,
                                     final Boolean keepRatio) {
        try {
            return resize(ImageIO.read(file), boundary, keepRatio);
        } catch (final IOException ignore) {
            return null;
        }
    }

    /**
     * Resize input stream.
     *
     * @param image     the image
     * @param boundary  the boundary
     * @param keepRatio the keep ratio
     * @return the input stream
     */
    public static ImageResult resize(final BufferedImage image,
                                     final Dimension boundary,
                                     final Boolean keepRatio) {
        final String formatName;
        final String mimeType;
        final int type;
        Dimension newSize = boundary;

        if (keepRatio) {
            newSize = ImageProcessing.getScaledDimension(
                new Dimension(
                    image.getWidth(),
                    image.getHeight()
                ),
                boundary
            );
        }

        if (image.isAlphaPremultiplied()) {
            type = BufferedImage.TYPE_INT_ARGB;
            formatName = "png";
            mimeType = "image/png";
        } else {
            type = BufferedImage.TYPE_INT_RGB;
            formatName = "jpg";
            mimeType = "image/jpeg";
        }

        final BufferedImage resizedImage = new BufferedImage(newSize.width, newSize.height, type);
        final Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, newSize.width, newSize.height, null);
        g2d.dispose();
        g2d.setComposite(AlphaComposite.Src);

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, formatName, os);
            return new ImageResult(os.toByteArray(), mimeType);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Crop input stream.
     *
     * @param file      the file
     * @param selection the selection
     * @return the input stream
     */
    public static ImageResult crop(final File file, final Rectangle selection) {
        try {
            return crop(ImageIO.read(file), selection);
        } catch (final IOException ignore) {
            return null;
        }
    }

    /**
     * Crop input stream.
     *
     * @param image     the image
     * @param selection the selection
     * @return the input stream
     */
    public static ImageResult crop(final BufferedImage image, final Rectangle selection) {
        BufferedImage croppedImage;
        final String formatName;
        final String mimeType;
        if (image.isAlphaPremultiplied()) {
            formatName = "png";
            mimeType = "image/png";
        } else {
            formatName = "jpg";
            mimeType = "image/jpeg";
        }

        try {
            croppedImage = image.getSubimage(selection.x, selection.y, selection.width, selection.height);
        } catch (final Exception e) {
            croppedImage = image;
        }

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(croppedImage, formatName, os);
            return new ImageResult(os.toByteArray(), mimeType);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Gets scaled dimension.
     *
     * @param original the original
     * @param boundary the boundary
     * @return the scaled dimension
     */
    public static Dimension getScaledDimension(final Dimension original, final Dimension boundary) {
        int newWidth = original.width;
        int newHeight = original.height;

        // First check if we need to scale width
        if (original.width > boundary.width) {
            //scale width to fit
            newWidth = boundary.width;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * original.height) / original.width;
        }

        // Then check if we need to scale even with the new height
        if (newHeight > boundary.height) {

            // Scale height to fit instead
            newHeight = boundary.height;

            // Scale width to maintain aspect ratio
            newWidth = (newHeight * original.width) / original.height;
        }

        return new Dimension(newWidth, newHeight);
    }

    /**
     * As byte array byte [ ].
     *
     * @param stream the stream
     * @return the byte [ ]
     */
    public static byte[] asByteArray(final InputStream stream) {
        try {
            final byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            return buffer;
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * The type Image result.
     */
    public static class ImageResult {
        /**
         * The Data.
         */
        final byte[] data;

        /**
         * The MimeType.
         */
        final String mimeType;

        /**
         * Instantiates a new Image result.
         *
         * @param data     the data
         * @param mimeType the mimeType
         */
        public ImageResult(final byte[] data, final String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }

        /**
         * Get data as byte array byte [ ].
         *
         * @return the byte [ ]
         */
        public byte[] getDataAsByteArray() {
            return this.data;
        }

        /**
         * Gets data as input stream.
         *
         * @return the data as input stream
         */
        public InputStream getDataAsInputStream() {
            return new ByteArrayInputStream(this.data);
        }

        /**
         * Gets mimetype.
         *
         * @return the mimetype
         */
        public String getMimeType() {
            return this.mimeType;
        }
    }
}
