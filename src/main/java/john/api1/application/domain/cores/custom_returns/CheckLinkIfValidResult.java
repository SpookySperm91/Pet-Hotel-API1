package john.api1.application.domain.cores.custom_returns;

import john.api1.application.components.enums.SendStatus;

public record CheckLinkIfValidResult(SendStatus status, String username) {
}
