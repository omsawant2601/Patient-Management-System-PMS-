package com.cliniccare.backend;

import com.cliniccare.backend.common.BadRequestException;
import com.cliniccare.backend.common.NotFoundException;
import com.cliniccare.backend.patient.PatientImageStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PatientImageStorageTests {
    @TempDir
    Path storageDirectory;

    @Test
    void storesOnlyValidatedImageBytesOutsideTheWebRoot() throws Exception {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB),"png",bytes);
        MockMultipartFile upload=new MockMultipartFile("photo","profile.png","image/png",bytes.toByteArray());
        PatientImageStorage storage=new PatientImageStorage(storageDirectory.toString(),1024*1024);

        String key=storage.store(upload);

        assertTrue(storage.open(key).fileName().endsWith(".png"));
        assertEquals("image/png",storage.open(key).mediaType());
        storage.delete(key);
        assertThrows(NotFoundException.class,()->storage.open(key));
    }

    @Test
    void rejectsNonImageFilesAndOversizedImages() {
        PatientImageStorage storage=new PatientImageStorage(storageDirectory.toString(),4);
        MockMultipartFile textFile=new MockMultipartFile("photo","notes.txt","text/plain","not an image".getBytes());
        MockMultipartFile largeFile=new MockMultipartFile("photo","large.png","image/png",new byte[5]);

        assertThrows(BadRequestException.class,()->storage.store(textFile));
        assertThrows(BadRequestException.class,()->storage.store(largeFile));
    }
}
