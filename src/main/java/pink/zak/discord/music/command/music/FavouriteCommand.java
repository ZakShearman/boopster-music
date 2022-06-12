package pink.zak.discord.music.command.music;

import com.google.api.services.youtube.model.VideoSnippet;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.model.MusicUser;
import pink.zak.discord.music.model.PersistentTrack;
import pink.zak.discord.music.repository.MusicUserRepository;
import pink.zak.discord.music.repository.PersistentTrackRepository;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.music.service.YouTubeService;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.util.Optional;

@RequiredArgsConstructor
@BotCommandComponent(name = "favourite", admin = false)
public class FavouriteCommand implements BotCommand {
    private final YouTubeService youTubeService;
    private final PersistentTrackRepository persistentTrackRepository;
    private final LiveServerRepository liveServerRepository;
    private final MusicUserRepository userRepository;

    @Override
    public void onExecute(@NotNull Member member, @NotNull SlashCommandInteractionEvent event) {
        PersistentTrack persistentTrack;
        if (event.getOption("track") != null) {
            // get track by option
            String trackInput = event.getOption("track", OptionMapping::getAsString);

            if (!trackInput.contains("/watch?v=")) {
                event.reply("You can only favourite a YouTube track.").setEphemeral(true).queue();
                return;
            }

            String trackId = trackInput.split("/watch?v=")[1];
            Optional<PersistentTrack> optionalPersistentTrack = this.persistentTrackRepository.findById(trackId);

            if (optionalPersistentTrack.isEmpty()) {
                VideoSnippet snippet = this.youTubeService.getSnippet(trackId);
                if (snippet == null) {
                    event.reply("Could not find a YouTube track with that ID").setEphemeral(true).queue();
                    return;
                }
                persistentTrack = this.persistentTrackRepository.save(new PersistentTrack(trackId, snippet.getTitle()));
            } else {
                persistentTrack = optionalPersistentTrack.get();
            }
        } else {
            // current track
            Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(event.getGuild().getIdLong());
            if (optionalLiveServer.isEmpty()) {
                event.reply("There is not currently a track playing.").setEphemeral(true).queue();
                return;
            }
            AudioTrack currentTrack = optionalLiveServer.get().getAudioPlayer().getPlayingTrack();
            if (currentTrack == null) {
                event.reply("There is not currently a track playing.").setEphemeral(true).queue();
                return;
            }

            if (!currentTrack.getSourceManager().getSourceName().equals("youtube")) {
                event.reply("The current playing track is not from YouTube. Only YouTube tracks can be favourited.").setEphemeral(true).queue();
                return;
            }

            String trackId = currentTrack.getIdentifier();
            Optional<PersistentTrack> optionalPersistentTrack = this.persistentTrackRepository.findById(trackId);

            persistentTrack = optionalPersistentTrack.orElseGet(() -> {
                VideoSnippet snippet = this.youTubeService.getSnippet(trackId);
                return new PersistentTrack(trackId, snippet.getTitle());
            });

            event.reply("Successfully favourited current song :)").setEphemeral(true).queue();
        }

        long userId = event.getUser().getIdLong();
        MusicUser user = this.userRepository.findById(userId).orElseGet(() -> this.userRepository.save(new MusicUser(userId)));
        user.getFavouriteTracks().add(persistentTrack);
        this.userRepository.save(user);
    }

    @Override
    public @NotNull CommandData createCommandData() {
        return Commands.slash("favourite", "Favourite a song - use no option for current track")
                .addOption(OptionType.STRING, "track", "URL of the track to favourite - don't provide this if you want the current track :)");
    }
}
