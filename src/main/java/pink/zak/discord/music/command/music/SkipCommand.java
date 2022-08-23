package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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

@BotCommandComponent(name = "skip", admin = true)
public class SkipCommand implements BotCommand {
    private final @NotNull LiveServerRepository liveServerRepository;

    protected SkipCommand(@NotNull LiveServerRepository liveServerRepository) {
        this.liveServerRepository = liveServerRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.reply("There is not a track playing").queue();
            return;
        }
        LiveServer liveServer = optionalLiveServer.get();
        AudioPlayer audioPlayer = liveServer.getAudioPlayer();
        if (audioPlayer.getPlayingTrack() == null) {
            event.reply("There is not a track playing").queue();
            return;
        }
        audioPlayer.stopTrack();
        liveServer.getQueueController().getRepeatsRemaining().set(0);
        event.reply("Skipped the current track.").queue();
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("skip", "Skips the current track");
    }
}
