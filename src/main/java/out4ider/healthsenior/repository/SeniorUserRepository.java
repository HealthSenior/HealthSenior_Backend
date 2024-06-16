package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.SeniorUser;

import java.util.Optional;

public interface SeniorUserRepository extends JpaRepository<SeniorUser,Long> {
    Optional<SeniorUser> findByOauth2Id(String oauthId);
}
