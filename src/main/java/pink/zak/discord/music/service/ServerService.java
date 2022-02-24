package pink.zak.discord.music.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pink.zak.discord.music.model.Server;
import pink.zak.discord.music.repository.HistoricTrackRepository;
import pink.zak.discord.music.repository.ServerRepository;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final HistoricTrackRepository historicTrackRepository;



    protected Server createServer(long id) {
        return this.serverRepository.save(new Server(id));
    }
}
