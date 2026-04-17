package communities.communities.service;

import communities.communities.dto.CreateCommunityRequest;
import communities.communities.dto.CreatePostRequest;
import communities.communities.model.Community;
import communities.communities.model.Post;
import communities.communities.repository.CommunityRepository;
import communities.communities.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final PostRepository postRepository;

    public CommunityService(CommunityRepository communityRepository, PostRepository postRepository){
        this.communityRepository = communityRepository;
        this.postRepository = postRepository;
    }

    public List<Community> listAll(){
        return communityRepository.findAll();
    }

    public List<Community> search(String q){
        if(q == null || q.isBlank()) return listAll();
        return communityRepository.findByNameContainingIgnoreCaseOrDescContainingIgnoreCase(q,q);
    }

    public Optional<Community> get(Long id){
        return communityRepository.findById(id);
    }

    public Community create(CreateCommunityRequest req){
        Community c = new Community();
        c.setName(req.name);
        c.setDesc(req.description);
        if(req.tags != null) c.setTags(req.tags);
        if(req.category != null && (req.tags == null || req.tags.isEmpty())) c.getTags().add(req.category);
        c.setMembers(1);
        return communityRepository.save(c);
    }

    public Optional<Community> update(Long id, CreateCommunityRequest req){
        return communityRepository.findById(id).map(existing -> {
            if(req.name != null) existing.setName(req.name);
            if(req.description != null) existing.setDesc(req.description);
            if(req.tags != null) existing.setTags(req.tags);
            return communityRepository.save(existing);
        });
    }

    public void delete(Long id){ communityRepository.deleteById(id); }

    public Optional<Community> join(Long id){
        return communityRepository.findById(id).map(c -> {
            c.setMembers((c.getMembers() == null ? 0 : c.getMembers()) + 1);
            return communityRepository.save(c);
        });
    }

    public List<Post> listPosts(Long communityId, int page, int size){
        Optional<Community> c = communityRepository.findById(communityId);
        if(c.isEmpty()) return List.of();
        return postRepository.findByCommunityOrderByCreatedAtDesc(c.get(), PageRequest.of(page,size));
    }

    public Optional<Post> createPost(Long communityId, CreatePostRequest req){
        return communityRepository.findById(communityId).map(c -> {
            Post p = new Post(c, req.author == null ? "Anonymous" : req.author, req.text);
            return postRepository.save(p);
        });
    }
}
