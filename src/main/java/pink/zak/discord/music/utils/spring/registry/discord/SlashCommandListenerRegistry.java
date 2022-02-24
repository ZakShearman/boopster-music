package pink.zak.discord.music.utils.spring.registry.discord;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.utils.listener.slashcommand.SlashCommandEventRegistry;
import pink.zak.discord.music.utils.listener.slashcommand.SlashCommandListener;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
public class SlashCommandListenerRegistry {
    private final SlashCommandListener[] slashCommandListeners;
    private final SlashCommandEventRegistry slashCommandEventRegistry;

    @PostConstruct
    private void register() {
        for (SlashCommandListener slashCommandListener : this.slashCommandListeners)
            this.slashCommandEventRegistry.addListener(slashCommandListener);
    }
}
