package pink.zak.discord.music.utils.spring.registry.discord;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.utils.command.discord.DiscordCommandBackend;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

import javax.annotation.PostConstruct;


@Component
@AllArgsConstructor
public class BotCommandRegistry {
    private final BotCommand[] botCommands;
    private final DiscordCommandBackend discordBackend;

    @PostConstruct
    private void register() {
        for (BotCommand botCommand : this.botCommands)
            this.discordBackend.registerCommand(botCommand);

        this.discordBackend.init();
    }
}
