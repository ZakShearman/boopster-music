package pink.zak.discord.music.service;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pink.zak.discord.music.model.HistoricTrack;
import pink.zak.discord.music.model.MusicUser;
import pink.zak.discord.music.model.Server;
import pink.zak.discord.music.repository.HistoricTrackRepository;
import pink.zak.discord.music.repository.MusicUserRepository;
import pink.zak.discord.music.repository.ServerRepository;

@Service
@RequiredArgsConstructor
public class HistoricTrackService {
    private final ServerRepository serverRepository;
    private final MusicUserRepository userRepository;
    private final HistoricTrackRepository historicTrackRepository;

    // todo clean up the flow of creating a new user and server so we can do direct requests
    public void logTrackPlay(long guildId, long userId, AudioTrack track) {
        Server server = this.serverRepository.findById(guildId).orElseGet(() -> this.serverRepository.save(new Server(guildId)));
        MusicUser user = this.userRepository.findById(userId).orElseGet(() -> this.userRepository.save(new MusicUser(userId)));
        HistoricTrack historicTrack = HistoricTrack.fromAudioTrack(server, user, track);
        server.getTrackHistory().add(historicTrack);
        user.getTrackHistory().add(historicTrack);

        this.historicTrackRepository.save(historicTrack);
        this.serverRepository.save(server);
        this.userRepository.save(user);
    }
}
