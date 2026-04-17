package communities.communities.dto;

import java.time.Instant;

public record PostDto(Long id, Long communityId, String author, String text, Instant createdAt) {}
