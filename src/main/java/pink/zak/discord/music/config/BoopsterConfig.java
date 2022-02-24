package pink.zak.discord.music.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("boopster")
@ConstructorBinding
public record BoopsterConfig(Long guildId) {
}
