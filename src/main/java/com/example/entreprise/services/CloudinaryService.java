package com.example.entreprise.services;

import com.cloudinary.Cloudinary;
import io.jsonwebtoken.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {
    Cloudinary cloudinary;
    public CloudinaryService(){
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "ds9erujyt");
        valuesMap.put("api_key", "226327752435374");
        valuesMap.put("api_secret", "CHtV6NiEXBelikWomF-XjBbuG4Q");
        cloudinary = new Cloudinary(valuesMap);
    }

    private File convert(MultipartFile multipartFile) throws IOException, java.io.IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }
    public Map<String, Object> upload(MultipartFile multipartFile) throws IOException, java.io.IOException {
        File file = convert(multipartFile);
        // Create a map of parameters
        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "auto"); // Automatically detect the resource type

        // Upload the file using Cloudinary's uploader
        Map<String, Object> result = cloudinary.uploader().upload(file, params);

        // Attempt to delete the temporary file
        if (!Files.deleteIfExists(file.toPath())) {
            throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
        }

        return result;
    }
    public Map<String, Object> delete(String id) throws IOException {
        try {
            // Create an empty map manually
            Map<String, Object> params = new HashMap<>();

            // Delete the resource using Cloudinary's uploader
            return cloudinary.uploader().destroy(id, params);
        } catch (Exception e) {
            // Handle and log the exception
            throw new IOException("Failed to delete resource with ID: " + id, e);
        }
    }
}