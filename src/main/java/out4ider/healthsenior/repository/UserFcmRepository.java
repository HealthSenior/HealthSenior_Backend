package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.UserFcmToken;

public interface UserFcmRepository extends JpaRepository<UserFcmToken,String> {
}
