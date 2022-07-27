package pink.zak.discord.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "track")

@Getter
@Setter

// todo use persistent track for history
@NoArgsConstructor
@AllArgsConstructor
public class PersistentTrack {

    @Id
    @Column(name = "youtube_id")
    private String youtubeId; // Only YouTube videos can be persisted.

    private String title;

    public String getUrl() {
        return "https://youtube.com/watch?v=" + this.youtubeId;
    }
}
