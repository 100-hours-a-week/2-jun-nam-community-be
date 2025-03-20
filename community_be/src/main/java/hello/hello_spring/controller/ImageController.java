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
        @Value("${file.user.upload-dir}")
        private String userUploadDir;

        @Value("${file.post.upload-dir}")
        private String postUploadDir;

        private final Path userUploadPath = Paths.get("uploads/users");
        private final Path postUploadPath = Paths.get("uploads/posts");

        @PostMapping("/users")
        public ResponseEntity<String> uploadUserImage(@RequestParam("file") MultipartFile file){
            try{
                if(file == null || file.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
                }

                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if(fileName == null || fileName.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 유효하지 않습니다.");
                }

                Path uploadPath = Paths.get("uploads/users");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. 파일 저장
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/api/images/users/" + fileName;
                return ResponseEntity.ok(fileUrl);
            }
            catch(IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
            }
        }

        @PostMapping("/posts")
        public ResponseEntity<String> uploadPostImage(@RequestParam("file") MultipartFile file){
            try{
                if(file == null || file.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
                }

                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if(fileName == null || fileName.isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 유효하지 않습니다.");
                }

                Path uploadPath = Paths.get("uploads/posts");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. 파일 저장
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/api/images/posts/" + fileName;
                return ResponseEntity.ok(fileUrl);
            }
            catch(IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
            }
        }



        @GetMapping("/users/{filename}")
        public ResponseEntity<Resource> getUserImage(@PathVariable String filename) {
            try {
                Path filePath = userUploadPath.resolve(filename).normalize();
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

        @GetMapping("/posts/{filename}")
        public ResponseEntity<Resource> getPostImage(@PathVariable String filename) {
            try {
                Path filePath = postUploadPath.resolve(filename).normalize();
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

        @DeleteMapping("/users/{fileName}")
        public ResponseEntity<String> deleteUserImage(@PathVariable String fileName) {
            try {
                if (fileName == null || fileName.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 유효하지 않습니다.");
                }

                Path filePath = Paths.get("uploads/users").resolve(fileName);

                if (!Files.exists(filePath)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없습니다.");
                }

                Files.delete(filePath);
                return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 삭제 실패: " + e.getMessage());
            }
        }

        @DeleteMapping("/posts/{fileName}")
        public ResponseEntity<String> deletePostImage(@PathVariable String fileName) {
            try {
                if (fileName == null || fileName.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일명이 유효하지 않습니다.");
                }

                Path filePath = Paths.get("uploads/posts").resolve(fileName);

                if (!Files.exists(filePath)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 찾을 수 없습니다.");
                }

                Files.delete(filePath);
                return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 삭제 실패: " + e.getMessage());
            }
        }
    }
