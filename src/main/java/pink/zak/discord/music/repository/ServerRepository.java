package pink.zak.discord.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pink.zak.discord.music.model.Server;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
}
