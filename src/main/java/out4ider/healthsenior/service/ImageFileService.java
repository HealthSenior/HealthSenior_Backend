package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.ImageFile;
import out4ider.healthsenior.repository.ImageFileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final String FOLDER_PATH = "C:\\images\\";
    private final ImageFileRepository imageFileRepository;

    public void uploadImageToFolder(Article article, MultipartFile file) throws IOException {
        String filePath = FOLDER_PATH +"article"+article.getId()+"\\";
        ImageFile imageFile = ImageFile.builder()
                .filePath(filePath)
                .fileName(file.getOriginalFilename())
                .article(article)
                .build();
        Files.createDirectories(Path.of(filePath));
        article.addImage(imageFile);
        imageFileRepository.save(imageFile);
        file.transferTo(new File(filePath+file.getOriginalFilename()));
    }

    public List<byte[]> downloadImageFromFolder(Article article) throws IOException {
        String filePath = FOLDER_PATH +"article"+article.getId()+"\\";
        List<byte[]> result = new ArrayList<>();
        for(ImageFile imageFile : article.getImageFiles()){
            result.add(Files.readAllBytes(Path.of(filePath+imageFile.getFileName())));
        }
        return result;
    }
}
