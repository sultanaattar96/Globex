package com.globex;

import com.globex.service.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GlobexApplication /*implements CommandLineRunner*/ {

    @Autowired
    private ImageService imageService;

    public static void main(String[] args) {
        SpringApplication.run(GlobexApplication.class, args);
    }

    /*@Override
    public void run(String... args) throws Exception {
    	try {
            String rootDirectory = "D:/img/";
            Files.walk(Paths.get(rootDirectory))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        String fileName = filePath.getFileName().toString();
                        String type = Files.probeContentType(filePath);
                        imageService.saveImageFromPath(filePath.toString(), fileName, type);
                        System.out.println("Image " + fileName + " has been saved successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
