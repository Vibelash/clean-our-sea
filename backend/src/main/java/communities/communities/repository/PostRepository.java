package communities.communities.repository;

import communities.communities.model.Post;
import communities.communities.model.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunityOrderByCreatedAtDesc(Community community, Pageable pageable);
}
