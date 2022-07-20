package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.model.trackdata.TrackDataContainer;
import pink.zak.discord.music.model.trackdata.TrackDataUser;
import pink.zak.discord.music.service.AudioService;
import pink.zak.discord.music.service.SpotifyService;
import pink.zak.discord.music.utils.types.DurationUtils;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@BotCommandComponent(name = "play", admin = false)
public class PlayCommand implements BotCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    private final @NotNull AudioPlayerManager audioPlayerManager;
    private final @NotNull AudioService audioService;
    private final @NotNull SpotifyService spotifyService;

    protected PlayCommand(@NotNull AudioPlayerManager audioPlayerManager, @NotNull AudioService audioService, @NotNull SpotifyService spotifyService) {
        this.audioPlayerManager = audioPlayerManager;
        this.audioService = audioService;
        this.spotifyService = spotifyService;
    }

    @Override
    public void onExecute(Member sender, @NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        String musicRequest = event.getOption("name").getAsString();

        LiveServer liveServer = this.audioService.getLiveServer(guild);

        Optional<Track> optionalSpotifyTrack = this.getSpotifyTrackId(musicRequest);
        if (optionalSpotifyTrack.isPresent()) {
            Track track = optionalSpotifyTrack.get();
            String searchTerm = "ytsearch:" + (track.getArtists().length > 0 ? track.getArtists()[0].getName() + " - " : "") + track.getName();
            Image image = this.spotifyService.getLargestImage(track.getAlbum().getImages());
            this.audioPlayerManager.loadItemOrdered(guild, searchTerm, new ResultHandler(this.audioPlayerManager, liveServer, event, true, image.getUrl()));
        } else {
            this.audioPlayerManager.loadItemOrdered(guild, musicRequest, new ResultHandler(this.audioPlayerManager, liveServer, event, false));
        }
    }

    private @NotNull Optional<Track> getSpotifyTrackId(@NotNull String input) {
        if (!input.startsWith("https://open.spotify.com/track/")) // 31 chars long link (before the URL)
            return Optional.empty();

        String inputEnd = input.substring(31);
        String trackId = inputEnd.length() > 22 ? inputEnd.substring(0, 22) : inputEnd; // Spotify often puts ?si=xxxxxx at the end, this accounts for that
        if (trackId.length() != 22)
            return Optional.empty();

        return Optional.of(this.spotifyService.getSpotifyTrack(trackId));
    }

    private record ResultHandler(@NotNull AudioPlayerManager audioPlayerManager,
                                 @NotNull LiveServer liveServer,
                                 @NotNull SlashCommandInteractionEvent event,
                                 boolean search,
                                 @Nullable String imageUrl) implements AudioLoadResultHandler {

        private ResultHandler(@NotNull AudioPlayerManager audioPlayerManager, @NotNull LiveServer liveServer, @NotNull SlashCommandInteractionEvent event, boolean search) {
            this(audioPlayerManager, liveServer, event, search, null);
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            Guild guild = this.event.getGuild();
            Member requester = this.event.getMember();
            if (!requester.getVoiceState().inAudioChannel()) {
                this.event.reply(":x: You are not in an audio channel.").queue();
                return;
            }
            if (!guild.getSelfMember().getVoiceState().inAudioChannel())
                guild.getAudioManager().openAudioConnection(requester.getVoiceState().getChannel());

            track.setUserData(this.createTrackDataContainer(requester, track));

            AudioPlayer audioPlayer = this.liveServer.getAudioPlayer();
            List<AudioTrack> trackQueue = this.liveServer.getQueueController().getQueue();
            if (trackQueue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
                audioPlayer.playTrack(track);
                this.event.replyEmbeds(this.createPlayingEmbed(track)).queue();
            } else {
                trackQueue.add(track);
                this.event.replyEmbeds(this.createQueuedEmbed(trackQueue.size(), track)).queue();
            }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            Member requester = this.event.getMember();

            if (playlist.getTracks().size() == 1 || this.search) {
                this.trackLoaded(playlist.getTracks().get(0));
            } else {
                if (!requester.getVoiceState().inAudioChannel()) {
                    this.event.reply(":x: You are not in an audio channel.").queue();
                    return;
                }

                Guild guild = this.event.getGuild();
                if (!guild.getSelfMember().getVoiceState().inAudioChannel())
                    guild.getAudioManager().openAudioConnection(requester.getVoiceState().getChannel());

                AudioPlayer audioPlayer = this.liveServer.getAudioPlayer();
                List<AudioTrack> trackQueue = this.liveServer.getQueueController().getQueue();
                List<AudioTrack> playlistTracks = playlist.getTracks();
                if (trackQueue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
                    AudioTrack audioTrack = playlistTracks.get(0);
                    audioPlayer.playTrack(audioTrack);

                    playlistTracks.remove(0);
                    // todo this message and the one below - this should be a playlist specific message.
                    this.event.replyEmbeds(this.createPlayingEmbed(audioTrack)).queue();
                } else {
                    this.event.reply("TODO: Loaded a playlist with " + playlistTracks.size() + " tracks and added them to queue :)").queue();
                }
                trackQueue.addAll(playlistTracks);
            }
        }

        @Override
        public void noMatches() {
            if (this.search)
                this.event.reply(":x: Could not find a match").queue();
            else
                this.audioPlayerManager.loadItemOrdered(this.event.getGuild(), "ytsearch:" + this.event.getOption("name").getAsString(), new ResultHandler(this.audioPlayerManager, this.liveServer, this.event, true));
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            LOGGER.error("Failed something: ", exception);
        }

        private TrackDataContainer createTrackDataContainer(Member user, AudioTrack track) {
            String imageUrl = null;
            if (this.imageUrl != null)
                imageUrl = this.imageUrl;
            else if (track instanceof YoutubeAudioTrack)
                imageUrl = "https://i.ytimg.com/vi/" + track.getInfo().identifier + "/maxresdefault.jpg";
            return new TrackDataContainer(imageUrl, TrackDataUser.fromMember(user));
        }

        private MessageEmbed createPlayingEmbed(AudioTrack track) {
            AudioTrackInfo meta = track.getInfo();
            Duration duration = Duration.ofMillis(meta.length);

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Now Playing " + meta.title)
                    .setDescription(String.format("""      
                            Length: `%s`""", DurationUtils.format(duration)))
                    .setColor(Color.GREEN);

            if (this.imageUrl != null)
                builder.setThumbnail(this.imageUrl);
            else if (track instanceof YoutubeAudioTrack)
                builder.setThumbnail("https://i.ytimg.com/vi/" + meta.identifier + "/maxresdefault.jpg");

            return builder.build();
        }

        private MessageEmbed createQueuedEmbed(int position, AudioTrack track) {
            AudioTrackInfo meta = track.getInfo();
            Duration duration = Duration.ofMillis(meta.length);

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Added to Queue " + meta.title)
                    .setDescription(String.format("""
                            Length: `%s`
                            Position: #%s""", DurationUtils.format(duration), position))
                    .setColor(Color.ORANGE);

            if (this.imageUrl != null)
                builder.setThumbnail(this.imageUrl);
            else if (track instanceof YoutubeAudioTrack)
                builder.setThumbnail("https://i.ytimg.com/vi/" + meta.identifier + "/maxresdefault.jpg");

            return builder.build();
        }
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("play", "play a track")
                .addOption(OptionType.STRING, "name", "Name or URL of a youtube track", true);
    }
}
