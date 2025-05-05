package john.api1.application.dto.mapper;

import john.api1.application.domain.models.AdminDomain;

public record AdminDTO(
        String id,
        String username,
        String email,
        boolean active
) {

    public static AdminDTO map(AdminDomain domain) {
        return new AdminDTO(domain.getId(), domain.getUsername(), domain.getEmail(), domain.isActive());
    }
}
