package pink.zak.discord.music.service;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.ServerRepository;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;

import java.nio.Buffer;
import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
public class AudioService {
    private final AudioPlayerManager audioPlayerManager;
    private final LiveServerRepository liveServerRepository;

    private final HistoricTrackService historicTrackService;
    private final ServerRepository serverRepository;

    @Bean
    public static AudioPlayerManager audioPlayerManager() {
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

        AudioConfiguration configuration = audioPlayerManager.getConfiguration();
        configuration.setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);

        return audioPlayerManager;
    }

    public @NotNull LiveServer getLiveServer(@NotNull Guild guild) {
        return this.liveServerRepository.findById(guild.getIdLong()).orElseGet(() -> {
            AudioPlayer player = this.audioPlayerManager.createPlayer();

            Integer volume = this.serverRepository.findVolumeById(guild.getIdLong());
            player.setVolume(volume == null ? 25 : volume);

            LiveServer newServer = new LiveServer(guild.getIdLong(), player, this.historicTrackService);
            player.addListener(newServer.getQueueController());

            this.liveServerRepository.save(newServer);

            guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

            return newServer;
        });
    }

    private static class AudioPlayerSendHandler implements AudioSendHandler {
        private final AudioPlayer audioPlayer;
        private final ByteBuffer buffer;
        private final MutableAudioFrame frame;

        public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
            this.buffer = ByteBuffer.allocate(1024);
            this.frame = new MutableAudioFrame();
            this.frame.setBuffer(buffer);
        }

        @Override
        public boolean canProvide() {
            return this.audioPlayer.provide(this.frame);
        }

        @Override
        public ByteBuffer provide20MsAudio() {
            ((Buffer) this.buffer).flip();
            return this.buffer;
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }
}
