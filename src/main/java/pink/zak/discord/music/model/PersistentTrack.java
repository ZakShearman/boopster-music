package pink.zak.discord.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersistentTrack {

    @Id
    private String youtubeId; // Only YouTube videos can be persisted.

    private String title;

    public String getUrl() {
        return "https://youtube.com/watch?v=" + this.youtubeId;
    }
}
