package pink.zak.discord.music.utils.spring.registry.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
public class ListenerAdapterRegistry {
    private final ListenerAdapter[] listenerAdapters;
    private final JDA jda;

    @PostConstruct
    private void register() {
        for (ListenerAdapter listenerAdapter : this.listenerAdapters)
            this.jda.addEventListener(listenerAdapter);
    }
}
