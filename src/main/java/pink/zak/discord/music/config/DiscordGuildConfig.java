package pink.zak.discord.music.config;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pink.zak.discord.utils.spring.config.JdaConfiguration;

@Configuration
@AllArgsConstructor
public class DiscordGuildConfig {
    private final JdaConfiguration jdaConfig;
    private final JDA jda;

    @Bean
    public Guild guild() {
        return this.jda.getGuildById(this.jdaConfig.getGuildId());
    }
}
