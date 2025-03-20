package hello.hello_spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

    @RestController
    @RequestMapping("/api/images")
    public class ImageController {
        @Value("${file.upload-dir}")
        private String uploadDir;
        private final Path uploadPath = Paths.get("uploads");

        @PostMapping
        public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
            try{
                if(file == null || file.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
                }

                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if(fileName == null || fileName.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 유효하지 않습니다.");
                }

                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. 파일 저장
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/api/images/" + fileName;
                return ResponseEntity.ok(fileUrl);
            }
            catch(IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
            }
        }

        @GetMapping("/{filename}")
        public ResponseEntity<Resource> getImage(@PathVariable String filename) {
            try {
                Path filePath = uploadPath.resolve(filename).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok(resource);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } catch (MalformedURLException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
