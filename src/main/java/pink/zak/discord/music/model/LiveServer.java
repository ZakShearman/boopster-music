package pink.zak.discord.music.model;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import pink.zak.discord.music.model.trackdata.TrackDataContainer;
import pink.zak.discord.music.service.HistoricTrackService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Data
@KeySpace("live_server")
public class LiveServer {

    @Id
    private final long id;

    private final @NotNull AudioPlayer audioPlayer;

    private final @NotNull QueueController queueController;

    public LiveServer(long id, @NotNull AudioPlayer audioPlayer, @NotNull HistoricTrackService historicTrackService) {
        this.id = id;
        this.audioPlayer = audioPlayer;
        this.queueController = new QueueController(historicTrackService);
    }

    @RequiredArgsConstructor
    public class QueueController extends AudioEventAdapter {
        private static final Logger LOGGER = LoggerFactory.getLogger(QueueController.class);

        private final List<AudioTrack> queue = new CopyOnWriteArrayList<>();
        private final AtomicLong repeatsRemaining = new AtomicLong();

        private final @NotNull HistoricTrackService serverService;

        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            this.serverService.logTrackPlay(LiveServer.this.id, track.getUserData(TrackDataContainer.class).user().id(), track);
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            if (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.STOPPED) {
                if (this.repeatsRemaining.get() > 0) {
                    player.playTrack(track.makeClone());
                    this.repeatsRemaining.updateAndGet(current -> current - 1);
                } else if (this.queue.size() > 0) {
                    AudioTrack newTrack = this.queue.remove(0);
                    player.playTrack(newTrack);
                }
            }
        }

        @Override
        public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
            player.playTrack(track);
            LOGGER.error("Track exception, retrying: ", exception);
        }

        @Override
        public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
            LOGGER.error("Woah track is stuck dude {}", track.getInfo().title);
        }

        @Override
        public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
            LOGGER.error("Track stuck exception: ", (Object[]) stackTrace);
        }

        public List<AudioTrack> getQueue() {
            return this.queue;
        }

        public AtomicLong getRepeatsRemaining() {
            return this.repeatsRemaining;
        }
    }
}
