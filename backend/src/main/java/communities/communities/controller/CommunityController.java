package communities.communities.controller;

import communities.communities.dto.*;
import communities.communities.model.Community;
import communities.communities.service.CommunityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/communities")
@CrossOrigin(origins = "*")
public class CommunityController {
    private final CommunityService service;

    public CommunityController(CommunityService service){ this.service = service; }

    @GetMapping
    public List<CommunityDto> list(@RequestParam(required = false) String q){
        return service.search(q).stream().map(c -> new CommunityDto(c.getId(), c.getName(), c.getDesc(), c.getMembers(), c.getTags(), c.getCreatedAt())).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> get(@PathVariable Long id){
        return service.get(id).map(c -> ResponseEntity.ok(new CommunityDto(c.getId(), c.getName(), c.getDesc(), c.getMembers(), c.getTags(), c.getCreatedAt()))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommunityDto> create(@RequestBody CreateCommunityRequest req){
        Community created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommunityDto(created.getId(), created.getName(), created.getDesc(), created.getMembers(), created.getTags(), created.getCreatedAt()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDto> update(@PathVariable Long id, @RequestBody CreateCommunityRequest req){
        return service.update(id, req).map(c -> ResponseEntity.ok(new CommunityDto(c.getId(), c.getName(), c.getDesc(), c.getMembers(), c.getTags(), c.getCreatedAt()))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<CommunityDto> join(@PathVariable Long id){
        return service.join(id).map(c -> ResponseEntity.ok(new CommunityDto(c.getId(), c.getName(), c.getDesc(), c.getMembers(), c.getTags(), c.getCreatedAt()))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/posts")
    public List<PostDto> listPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return service.listPosts(id,page,size).stream().map(p -> new PostDto(p.getId(), p.getCommunity().getId(), p.getAuthor(), p.getText(), p.getCreatedAt())).collect(Collectors.toList());
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<PostDto> createPost(@PathVariable Long id, @RequestBody CreatePostRequest req){
        return service.createPost(id, req).map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new PostDto(p.getId(), p.getCommunity().getId(), p.getAuthor(), p.getText(), p.getCreatedAt()))).orElse(ResponseEntity.notFound().build());
    }
}
