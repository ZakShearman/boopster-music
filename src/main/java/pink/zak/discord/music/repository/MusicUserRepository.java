package pink.zak.discord.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pink.zak.discord.music.model.MusicUser;

@Repository
public interface MusicUserRepository extends JpaRepository<MusicUser, Long> {
}
