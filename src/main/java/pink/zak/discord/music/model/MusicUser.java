package pink.zak.discord.music.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MusicUser {

    @Id
    private long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricTrack> trackHistory;

    public MusicUser(long id) {
        this.id = id;
        this.trackHistory = new ArrayList<>();
    }
}
