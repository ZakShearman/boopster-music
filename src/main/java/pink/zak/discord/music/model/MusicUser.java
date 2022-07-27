package pink.zak.discord.music.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id")
    private List<HistoricTrack> trackHistory;

    public MusicUser(long id) {
        this.id = id;
        this.creationTimestamp = Instant.now();
        this.trackHistory = new ArrayList<>();
    }
}
