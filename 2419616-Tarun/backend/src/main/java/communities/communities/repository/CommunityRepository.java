package communities.communities.repository;

import communities.communities.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findByNameContainingIgnoreCaseOrDescContainingIgnoreCase(String name, String desc);
}
