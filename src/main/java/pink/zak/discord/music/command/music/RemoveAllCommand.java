package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

import java.util.List;
import java.util.Optional;

@Component
public class RemoveAllCommand extends BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    protected RemoveAllCommand(@NotNull LiveServerRepository liveServerRepository) {
        super("removeall", true);
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.reply("There are no songs in the queue").queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        List<AudioTrack> trackQueue = liveServer.getQueueController().getQueue();
        if (trackQueue.isEmpty()) {
            event.reply("There are no songs in the queue").queue();
            return;
        }
        trackQueue.clear();
        event.reply("The queue has been cleared!").queue();
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("removeall", "removes all tracks from the queue");
    }
}
