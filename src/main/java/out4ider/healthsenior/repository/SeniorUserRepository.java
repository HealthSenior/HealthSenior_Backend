package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.SeniorUser;

public interface SeniorUserRepository extends JpaRepository<SeniorUser,Long> {
}
