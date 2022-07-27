package pink.zak.discord.music.model;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import pink.zak.discord.music.model.trackdata.TrackDataContainer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historic_track")

@Getter
@Setter

@NoArgsConstructor
public class HistoricTrack {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "server_discord_id")
    private Server server;

    @ManyToOne
    @JoinColumn(name = "user_discord_id")
    private MusicUser musicUser;

    @Column(name = "play_time")
    private Instant playTime;

    private Duration length;

    private String title;

    private String author;

    @Column(name = "source_id")
    private String sourceId;

    private boolean stream;

    private String url;

    public HistoricTrack(Server server, MusicUser user, Instant playTime, Duration length, String title, String author, String sourceId, boolean stream, String url) {
        this.server = server;
        this.musicUser = user;
        this.playTime = playTime;
        this.length = length;
        this.title = title;
        this.author = author;
        this.sourceId = sourceId;
        this.stream = stream;
        this.url = url;
    }

    public static HistoricTrack fromAudioTrack(Server server, MusicUser user, AudioTrack track) {
        TrackDataContainer data = track.getUserData(TrackDataContainer.class);
        AudioTrackInfo meta = track.getInfo();
        return new HistoricTrack(
            server,
            user,
            data.queueTime(),
            track.getInfo().isStream ? Duration.ZERO : Duration.ofSeconds(meta.length),
            meta.title,
            meta.author,
            meta.identifier,
            meta.isStream,
            meta.uri
        );
    }

}
