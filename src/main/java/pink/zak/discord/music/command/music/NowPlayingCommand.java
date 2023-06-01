package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.model.trackdata.TrackDataContainer;
import pink.zak.discord.music.model.trackdata.TrackDataUser;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.music.utils.types.DurationUtils;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.awt.*;
import java.time.Duration;
import java.util.Optional;

@BotCommandComponent(name = "nowplaying")
public class NowPlayingCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    @SneakyThrows
    protected NowPlayingCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(@NotNull Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty() || optionalLiveServer.get().getAudioPlayer().getPlayingTrack() == null) {
            event.reply("There is nothing currently playing.").setEphemeral(true).queue();
            return;
        }
        AudioTrack track = optionalLiveServer.get().getAudioPlayer().getPlayingTrack();
        AudioTrackInfo trackMeta = track.getInfo();
        TrackDataContainer trackData = track.getUserData(TrackDataContainer.class);
        TrackDataUser trackUser = trackData.user();
        Duration currentPosition = Duration.ofMillis(track.getPosition());
        Duration trackLength = Duration.ofMillis(track.getDuration());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(trackUser.username() + "#" + trackUser.discriminator(), null, trackUser.avatarUrl())
                .setThumbnail(trackData.imageUrl())
                .setDescription("""
                        [**%s**](%s)
                        %s
                        """.formatted(
                        trackMeta.title, trackMeta.uri,
                        this.createDurationLine(currentPosition, trackLength),
                        DurationUtils.format(currentPosition), DurationUtils.format(trackLength))
                );

        if (trackData.imageUrl() != null)
            embedBuilder.setThumbnail(trackData.imageUrl());

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    private String createDurationLine(Duration position, Duration length) {
        StringBuilder builder = new StringBuilder(":play_pause: ");
        double completion = position.getSeconds() / (double) length.getSeconds();
        int markerPosition = (int) Math.floor(completion * 20);

        for (int i = 0; i <= 20; i++) {
            if (i == markerPosition)
                builder.append(":blue_circle:");
            else
                builder.append("▬");
        }
        builder.append(" [")
                .append(DurationUtils.format(position))
                .append("/")
                .append(DurationUtils.format(length))
                .append("] :speaker:");
        return builder.toString();
    }

    @Override
    public @NotNull CommandData createCommandData() {
        return Commands.slash("nowplaying", "Gets the currently playing track")
                .setGuildOnly(true);
    }
}
