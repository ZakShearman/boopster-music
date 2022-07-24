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

import java.util.Optional;

@BotCommandComponent(name = "loop", admin = false)
public class LoopCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    protected LoopCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());
        int repeatCount = event.getOption("count") != null ? event.getOption("count").getAsInt() : Integer.MAX_VALUE;

        if (optionalLiveServer.isEmpty() || optionalLiveServer.get().getAudioPlayer().getPlayingTrack() == null) {
            event.reply("There must be a track playing to loop.").queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        AudioTrack currentTrack = liveServer.getAudioPlayer().getPlayingTrack();

        if (currentTrack.getInfo().isStream) {
            event.reply("The current track is a stream and cannot be looped.").queue();
            return;
        }

        liveServer.getQueueController().getRepeatsRemaining().set(repeatCount);

        if (repeatCount == Integer.MAX_VALUE)
            event.reply(":white_check_mark: The current track will now repeat!").queue();
        else
            event.reply(":white_check_mark: The current track will repeat **" + repeatCount + "** times!").queue();
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("loop", "Loop the current track a specified amount of times")
            .addOptions(
                new OptionData(OptionType.INTEGER, "count", "Amount of times to repeat the track", false)
                    .setMinValue(1)
                    .setMaxValue(Integer.MAX_VALUE)
            );
    }
}
