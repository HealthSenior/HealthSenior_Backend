package out4ider.healthsenior.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NewArticleDto {
    private String title;
    private String content;
    private List<MultipartFile> images;
}
