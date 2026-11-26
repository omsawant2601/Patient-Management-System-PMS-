package com.cliniccare.backend.patient;

import com.cliniccare.backend.common.BadRequestException;
import com.cliniccare.backend.common.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Component
public class PatientImageStorage {
    private static final long MAX_PIXELS=20_000_000L;
    private final Path root;
    private final long maxFileSize;

    public PatientImageStorage(
            @Value("${cliniccare.patient-images.upload-dir:${user.home}/.cliniccare/patient-images}") String uploadDir,
            @Value("${cliniccare.patient-images.max-file-size-bytes:5242880}") long maxFileSize) {
        this.root=Path.of(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize=maxFileSize;
    }

    public String store(MultipartFile file) {
        if(file==null||file.isEmpty())throw new BadRequestException("Select an image to upload");
        if(file.getSize()>maxFileSize)throw new BadRequestException("Each image must be 5 MB or smaller");
        ImageFormat format=validate(file);
        String key=UUID.randomUUID()+format.extension();
        Path target=resolve(key);
        try {
            Files.createDirectories(root);
            try(InputStream input=file.getInputStream()) {
                Files.copy(input,target);
            }
            return key;
        } catch(IOException ex) {
            throw new IllegalStateException("Could not store patient image",ex);
        }
    }

    public StoredImage open(String key) {
        Path path=resolve(key);
        if(!Files.isRegularFile(path))throw new NotFoundException("Patient image not found");
        try {
            return new StoredImage(new FileSystemResource(path),mediaType(key),Files.size(path),key);
        } catch(IOException ex) {
            throw new IllegalStateException("Could not read patient image",ex);
        }
    }

    public void delete(String key) {
        if(key==null)return;
        try {
            Files.deleteIfExists(resolve(key));
        } catch(IOException ex) {
            throw new IllegalStateException("Could not remove patient image",ex);
        }
    }

    private ImageFormat validate(MultipartFile file) {
        try(InputStream input=file.getInputStream();
            ImageInputStream imageInput=ImageIO.createImageInputStream(input)) {
            if(imageInput==null)throw new BadRequestException("Upload a valid JPEG or PNG image");
            Iterator<ImageReader> readers=ImageIO.getImageReaders(imageInput);
            if(!readers.hasNext())throw new BadRequestException("Upload a valid JPEG or PNG image");
            ImageReader reader=readers.next();
            try {
                reader.setInput(imageInput,true,true);
                String format=reader.getFormatName().toLowerCase(Locale.ROOT);
                String extension;
                if("jpeg".equals(format)||"jpg".equals(format))extension=".jpg";
                else if("png".equals(format))extension=".png";
                else throw new BadRequestException("Only JPEG and PNG images are supported");
                int width=reader.getWidth(0),height=reader.getHeight(0);
                if(width<1||height<1||width>8000||height>8000||(long)width*height>MAX_PIXELS)
                    throw new BadRequestException("Image dimensions must not exceed 20 megapixels");
                if(reader.read(0)==null)throw new BadRequestException("Upload a valid JPEG or PNG image");
                return new ImageFormat(extension);
            } finally {
                reader.dispose();
            }
        } catch(BadRequestException ex) {
            throw ex;
        } catch(IOException ex) {
            throw new BadRequestException("Upload a valid JPEG or PNG image");
        }
    }

    private Path resolve(String key) {
        if(key==null||!key.matches("[0-9a-fA-F-]{36}\\.(jpg|png)"))
            throw new BadRequestException("Invalid patient image key");
        Path path=root.resolve(key).normalize();
        if(!path.startsWith(root))throw new BadRequestException("Invalid patient image key");
        return path;
    }

    private String mediaType(String key) {
        return key.endsWith(".png")?"image/png":"image/jpeg";
    }

    private record ImageFormat(String extension) {}
    public record StoredImage(Resource resource,String mediaType,long size,String fileName) {}
}
