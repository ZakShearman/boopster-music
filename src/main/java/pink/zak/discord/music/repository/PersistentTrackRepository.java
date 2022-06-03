package pink.zak.discord.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pink.zak.discord.music.model.PersistentTrack;

public interface PersistentTrackRepository extends JpaRepository<PersistentTrack, String> {
}
