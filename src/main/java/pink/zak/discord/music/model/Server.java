package pink.zak.discord.music.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Server {

    @Id
    @Column(name = "discord_id")
    private long id;

    private int volume;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "server")
    private List<HistoricTrack> trackHistory;

    public Server(long id) {
        this.id = id;
        this.volume = 20;
        this.trackHistory = new ArrayList<>();
    }
}
