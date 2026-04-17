package communities.communities.dto;

import java.time.Instant;
import java.util.List;

public record CommunityDto(Long id, String name, String desc, Integer members, List<String> tags, Instant createdAt) {}
