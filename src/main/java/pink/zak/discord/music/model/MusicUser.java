package pink.zak.discord.music.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")

@Getter
@Setter

@NoArgsConstructor
public class MusicUser {

    @Id
    @Column(name = "discord_id")
    private long id;

    @Column(name = "creation_timestamp")
    private Instant creationTimestamp;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "musicUser")
    private List<HistoricTrack> trackHistory;

    public MusicUser(long id) {
        this.id = id;
        this.creationTimestamp = Instant.now();
        this.trackHistory = new ArrayList<>();
    }
}
