package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.util.List;
import java.util.Optional;

@BotCommandComponent(name = "remove", admin = true)
public class RemoveCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    protected RemoveCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        int position = (int) event.getOption("position").getAsLong();
        Guild guild = event.getGuild();

        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());
        if (optionalLiveServer.isEmpty()) {
            event.reply("The queue is empty.").queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        List<AudioTrack> trackQueue = liveServer.getQueueController().getQueue();
        if (trackQueue.isEmpty()) {
            event.reply("The queue is empty.").queue();
            return;
        }
        if (trackQueue.size() < position) {
            event.reply("There is no track at position " + position + " in the queue").queue();
            return;
        }
        AudioTrack removedTrack = trackQueue.get(position - 1);
        trackQueue.remove(removedTrack);

        event.reply("Removed " + removedTrack.getInfo().title + " from the queue!").queue();
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("remove", "Removes a specific track from the queue.")
            .addOptions(
                new OptionData(OptionType.INTEGER, "position", "The position in the queue to remove the track", true)
                    .setMinValue(1)
                    .setMaxValue(1000)
            );
    }
}
