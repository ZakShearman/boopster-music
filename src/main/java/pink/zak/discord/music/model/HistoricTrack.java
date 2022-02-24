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
import javax.persistence.ManyToOne;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HistoricTrack {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    private Server server;

    @ManyToOne
    private MusicUser musicUser;

    @Column(name = "play_time")
    private Instant playTime;

    private Duration length;

    private String title;

    private String author;

    private String identifier;

    private boolean stream;

    private String url;

    public HistoricTrack(Server server, MusicUser user, Instant playTime, Duration length, String title, String author, String identifier, boolean stream, String url) {
        this.server = server;
        this.musicUser = user;
        this.playTime = playTime;
        this.length = length;
        this.title = title;
        this.author = author;
        this.identifier = identifier;
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
            Duration.ofSeconds(meta.length),
            meta.title,
            meta.author,
            meta.identifier,
            meta.isStream,
            meta.uri
        );
    }

}
