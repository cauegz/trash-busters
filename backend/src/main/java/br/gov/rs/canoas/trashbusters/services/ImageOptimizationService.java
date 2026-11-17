package br.gov.rs.canoas.trashbusters.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

@Service
public class ImageOptimizationService {

    private final int maxDimension;

    public ImageOptimizationService(
            @Value("${app.image.max-dimension:1600}") int maxDimension
    ) {
        this.maxDimension = maxDimension;
    }

    public OptimizedImage optimize(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("A imagem enviada não pode estar vazia");
        }

        Path optimizedFile = null;
        try {
            optimizedFile = Files.createTempFile("trash-busters-image-", ".jpg");
            try (ImageInputStream input = ImageIO.createImageInputStream(image.getInputStream())) {
                if (input == null) {
                    throw new IllegalArgumentException("Não foi possível ler a imagem enviada");
                }

                ImageReader reader = findReader(input);
                try {
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    int sample = Math.max(1, (int) Math.ceil(
                            Math.max(width, height) / (double) maxDimension));

                    ImageReadParam readParam = reader.getDefaultReadParam();
                    readParam.setSourceSubsampling(sample, sample, 0, 0);
                    BufferedImage decoded = reader.read(0, readParam);
                    BufferedImage rgb = new BufferedImage(
                            decoded.getWidth(), decoded.getHeight(), BufferedImage.TYPE_INT_RGB);

                    Graphics2D graphics = rgb.createGraphics();
                    graphics.setColor(java.awt.Color.WHITE);
                    graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
                    graphics.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics.drawImage(decoded, 0, 0, null);
                    graphics.dispose();

                    if (!ImageIO.write(rgb, "jpg", optimizedFile.toFile())) {
                        throw new IllegalArgumentException("Formato de imagem não suportado");
                    }
                    return new OptimizedImage(optimizedFile, image.getOriginalFilename());
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException | RuntimeException exception) {
            deleteQuietly(optimizedFile);
            if (exception instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw new IllegalArgumentException("Não foi possível processar a imagem enviada", exception);
        }
    }

    public void delete(OptimizedImage image) {
        deleteQuietly(image.path());
    }

    private ImageReader findReader(ImageInputStream input) {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (!readers.hasNext()) {
            throw new IllegalArgumentException("Formato de imagem não suportado");
        }
        ImageReader reader = readers.next();
        reader.setInput(input, true, true);
        return reader;
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // O arquivo temporário será removido pelo sistema operacional.
        }
    }

    public record OptimizedImage(Path path, String originalFilename) {
    }
}
