package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.LikeUserRelation;
import out4ider.healthsenior.repository.LikeUserRelationRepository;

@Service
@RequiredArgsConstructor
public class LikeUserRelationService {
    private final LikeUserRelationRepository likeUserRelationRepository;

    public void saveLikeUserRelation(LikeUserRelation likeUserRelation) {
        likeUserRelationRepository.save(likeUserRelation);
    }

    public void deleteLikeUserRelation(Long id) {
        likeUserRelationRepository.deleteById(id);
    }
}
