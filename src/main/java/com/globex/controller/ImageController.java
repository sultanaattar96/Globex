package com.globex.controller;

import com.globex.model.Image;
import com.globex.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/images/{imageName}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable String imageName) {
        Image image = imageService.getImage(imageName);
        if (image != null) {
            ByteArrayResource resource = new ByteArrayResource(image.getData());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getName() + "\"")
                    .contentType(MediaType.parseMediaType(image.getType()))
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<byte[]> getImageByName(@PathVariable String name) {
        byte[] image = imageService.getImageByName(name);
        if (image != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg");
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
