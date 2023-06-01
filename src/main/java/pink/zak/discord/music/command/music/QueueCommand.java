package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@BotCommandComponent(name = "queue")
public class QueueCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;
    private final MessageEmbed emptyQueueEmbed;

    protected QueueCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;

        this.emptyQueueEmbed = new EmbedBuilder()
            .setTitle("Track Queue")
            .setDescription("There are no tracks in the queue")
            .setColor(Color.MAGENTA)
            .build();
    }

    @Override
    public void onExecute(@NotNull Member sender, SlashCommandInteractionEvent event) {
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(event.getGuild().getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.replyEmbeds(this.emptyQueueEmbed).queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        if (liveServer.getQueueController().getQueue().isEmpty()) {
            event.replyEmbeds(this.emptyQueueEmbed).queue();
        } else {
            StringBuilder descriptionBuilder = new StringBuilder();
            List<AudioTrack> trackQueue = liveServer.getQueueController().getQueue();

            int i = 0;
            for (AudioTrack track : trackQueue) {
                descriptionBuilder.append("**")
                    .append(i + 1)
                    .append(".** ")
                    .append(track.getInfo().title)
                    .append("\n");
                if (i++ == 9 || i > trackQueue.size()) {
                    break;
                }
            }
            event.replyEmbeds(new EmbedBuilder()
                .setTitle("Track Queue (" + trackQueue.size() + ")")
                .setColor(Color.MAGENTA)
                .setDescription(descriptionBuilder.toString())
                .build()).queue();
        }
    }

    @Override
    public @NotNull CommandData createCommandData() {
        return Commands.slash("queue", "View the tracks in the queue.")
                .setGuildOnly(true);
    }
}
