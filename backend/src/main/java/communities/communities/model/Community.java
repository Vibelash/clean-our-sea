package communities.communities.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    
    @Column(name = "description", length = 2000)
    private String desc;

    private Integer members = 0;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "community_tags", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    public Community() {}

    public Community(String name, String desc, Integer members, List<String> tags) {
        this.name = name;
        this.desc = desc;
        this.members = members == null ? 0 : members;
        if (tags != null) this.tags = tags;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public Integer getMembers() { return members; }
    public void setMembers(Integer members) { this.members = members; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
