package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
import pink.zak.discord.music.utils.types.DurationUtils;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.util.Optional;

@BotCommandComponent(name = "seek", admin = false)
public class SeekCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    protected SeekCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.reply("There is not a track playing.").queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        AudioPlayer audioPlayer = liveServer.getAudioPlayer();
        AudioTrack playingTrack = audioPlayer.getPlayingTrack();
        if (audioPlayer.getPlayingTrack() == null) {
            event.reply("There is not a track playing.").queue();
            return;
        }
        if (!playingTrack.isSeekable()) {
            event.reply("The currently playing track is not seekable.").queue();
            return;
        }

        int seconds = event.getOption("time").getAsInt();
        playingTrack.setPosition(seconds * 1000L); // convert to millis
        event.reply("Seeked to " + DurationUtils.formatSeconds(seconds)).queue();
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("seek", "Skip to a certain time in the playing track")
            .addOptions(
                new OptionData(OptionType.INTEGER, "time", "Time to skip to in seconds", true)
                    .setMinValue(0)
                    .setMaxValue(Integer.MAX_VALUE)
            );
    }
}
