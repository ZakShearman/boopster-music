package pink.zak.discord.music.utils.listener.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SlashCommandEventRegistry extends ListenerAdapter {
    private final Set<SlashCommandListener> listeners = new HashSet<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
            return;

        for (SlashCommandListener listener : this.listeners)
            listener.onSlashCommand(event);
    }

    public void addListener(SlashCommandListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(SlashCommandListener listener) {
        this.listeners.remove(listener);
    }
}
