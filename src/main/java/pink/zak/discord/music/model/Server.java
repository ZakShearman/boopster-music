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
public class Server {

    @Id
    private long id;

    private int volume;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricTrack> trackHistory;

    public Server(long id) {
        this.id = id;
        this.volume = 20;
        this.trackHistory = new ArrayList<>();
    }
}
