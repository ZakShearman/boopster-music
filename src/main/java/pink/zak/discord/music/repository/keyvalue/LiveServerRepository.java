package pink.zak.discord.music.repository.keyvalue;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;
import pink.zak.discord.music.model.LiveServer;

@Repository
public interface LiveServerRepository extends KeyValueRepository<LiveServer, Long> {
}
