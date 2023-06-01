package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.util.Optional;

@RequiredArgsConstructor
@BotCommandComponent(name = "pause")
public class PauseCommand implements BotCommand {
    private final LiveServerRepository liveServerRepository;

    @Override
    public void onExecute(@NotNull Member sender, @NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.reply("There must be a track playing to pause.").queue();
            return;
        }

        LiveServer liveServer = optionalLiveServer.get();

        if (liveServer.getAudioPlayer().getPlayingTrack() == null) {
            event.reply("There must be a track playing to pause.").queue();
            return;
        }

        AudioPlayer audioPlayer = liveServer.getAudioPlayer();
        AudioTrack currentTrack = audioPlayer.getPlayingTrack();

        if (currentTrack.getInfo().isStream) {
            event.reply("The current track is a stream and cannot be paused.").queue();
            return;
        }

        audioPlayer.setPaused(!audioPlayer.isPaused());

        if (audioPlayer.isPaused())
            event.reply(":white_check_mark: The current track is now paused.").queue();
        else
            event.reply(":white_check_mark: The current track will now play.").queue();
    }

    @Override
    public @NotNull CommandData createCommandData() {
        return Commands.slash("pause", "pauses the current track")
                .setGuildOnly(true);
    }
}
