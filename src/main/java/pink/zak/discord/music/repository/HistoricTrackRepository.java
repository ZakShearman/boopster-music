package pink.zak.discord.music.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pink.zak.discord.music.model.HistoricTrack;

import java.util.UUID;

@Repository
public interface HistoricTrackRepository extends PagingAndSortingRepository<HistoricTrack, UUID> {

    Page<HistoricTrack> findByServerId(long serverId, Pageable pageable);

    Page<HistoricTrack> findByMusicUserId(long userId, Pageable pageable);
}
