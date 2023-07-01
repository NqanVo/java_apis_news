package com.javaspring.blogapi.service.impl;

import com.javaspring.blogapi.exception.CustomException;
import com.javaspring.blogapi.service.FilesInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FilesService implements FilesInterface {
    private final String uploadDir = "uploads"; // Tên thư mục uploads

    private Path folderImages() throws IOException {
        // Tạo đường dẫn tới thư mục root của ứng dụng
        String rootDir = System.getProperty("user.dir");
        // Đường dẫn đến thư mục uploads
        Path uploadPath = Paths.get(rootDir, uploadDir);
        if (!Files.exists(uploadPath)) {
            // Nếu thư mục không tồn tại, tạo mới
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }

    public void moveImageToFolder(MultipartFile file, String newNameFile) throws IOException {
        // * Đường dẫn đến thư mục uploads
        Path pathFolder = folderImages();
        // * Lưu file vào thư mục uploads
        Path filePath = pathFolder.resolve(newNameFile);
        file.transferTo(filePath.toFile());
    }

    public void deleteImageFromFolder(String oldAvatar) throws IOException {
        Path folderPath = folderImages();
        // * Xây dựng đường dẫn đầy đủ tới file ảnh cần xóa
        String imagePath = folderPath + File.separator + oldAvatar;
        // * Tạo đối tượng File từ đường dẫn ảnh
        File imageFile = new File(imagePath);
        // * Kiểm tra nếu file tồn tại và là một file hợp lệ
        if (imageFile.exists() && imageFile.isFile()) {
            // * Tiến hành xóa file
            boolean deleted = imageFile.delete();
            // * Kiểm tra kết quả xóa
            if (deleted) {
                System.out.println("Đã xóa ảnh từ thư mục lưu trữ: " + oldAvatar);
            } else {
                System.out.println("Không thể xóa ảnh từ thư mục lưu trữ: " + oldAvatar);
            }
        } else {
            System.out.println("Không tìm thấy file ảnh trong thư mục lưu trữ: " + oldAvatar);
        }
    }

    public boolean notEmpty(MultipartFile[] file) {
        if (!(file == null || file.length == 0)) return true;
        else throw new CustomException.BadRequestException("File không được bỏ trống");
    }

    public boolean isSingleFile(MultipartFile[] file) {
        if (!(file.length > 1)) return true;
        else throw new CustomException.BadRequestException("File tối đa là 1");
    }

    public boolean isImageFile(MultipartFile file) {
        if (file.getContentType() != null && file.getContentType().startsWith("image/")) return true;
        else throw new CustomException.BadRequestException("File tải lên phải là ảnh");
    }

    public boolean maxSize(MultipartFile file, double maxSize) {
        double sizeFile = (double) file.getSize() / (1024 * 1024);
        if (sizeFile < maxSize) return true;
        else throw new CustomException.BadRequestException(String.format("File tải lên phải dưới %s Mb", maxSize));
    }
}
