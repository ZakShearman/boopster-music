package pink.zak.discord.music.config;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties("discord")
@ConstructorBinding
public record DiscordConfig(@NotNull String token) {

    @Bean
    @SneakyThrows
    public JDA jda() {
        return JDABuilder.createDefault(this.token)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .build()
            .awaitReady();
    }
}
