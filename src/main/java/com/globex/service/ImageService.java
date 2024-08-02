package com.globex.service;

import com.globex.model.Image;
import com.globex.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }
    
    public Image saveImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        image.setData(file.getBytes());
        return imageRepository.save(image);
    }

    /*public Image saveImageFromPath(String filePath, String name, String type) throws IOException {
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);

        Image image = new Image();
        image.setName(name);
        image.setType(type);
        image.setData(data);

        return imageRepository.save(image);
    }*/

    public Image getImage(String name) {
        return imageRepository.findByName(name);
    }
    
    public byte[] getImageByName(String name) {
        Image image = imageRepository.findByName(name);
        return image != null ? image.getData() : null;
    }
}
