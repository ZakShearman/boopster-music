package pink.zak.discord.music.repository;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pink.zak.discord.music.model.Server;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Server s SET s.volume = ?1 WHERE s.id = ?2")
    void setVolumeById(int volume, long id);

    @Nullable
    @Query("SELECT s.volume FROM Server s WHERE s.id = ?1")
    Integer findVolumeById(long id);
}
