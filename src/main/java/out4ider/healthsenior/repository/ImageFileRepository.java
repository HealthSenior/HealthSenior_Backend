package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
