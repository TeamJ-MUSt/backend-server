package TeamJ.MUSt.service.song;

import TeamJ.MUSt.PythonExecutor;
import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberSong;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final MemberRepository memberRepository;

    public List<Song> findUserSong(Long memberId) {
        return songRepository.findWithMemberSong(memberId);
    }

    public List<Song> findUserSong(Long memberId, Pageable pageRequest) {
        return songRepository.findWithMemberSong(memberId, pageRequest);
    }
    public List<Song> searchMySong(Long memberId, String title, String artist){
        return songRepository.findInMySong(memberId, title, artist);
    }
    public List<Song> searchDbSong(String title, String artist){
        System.out.println("노래가 없어서 검색");
        /*List<SongInfo> songInfo = PythonExecutor.callBugsApi(title, artist);
        List<Song> searchedSongs = songInfo.stream().map(info -> new Song(
                info.getTitle(),
                info.getArtist().split("\\((.*?)\\)")[0],
                info.getLyrics(),
                imageToByte(info.getThumbnailUrl()),
                Long.valueOf(info.getMusic_id())
        )).toList();
        songRepository.saveAll(searchedSongs);*/
        return songRepository.findRequestSong(title, artist);
    }

    @Transactional
    public String registerSong(Long userId, Long songId) throws NoSearchResultException {
        Member member = memberRepository.findById(userId).get();
        Song newSong = songRepository.findById(songId).get();
        MemberSong memberSong = new MemberSong();
        memberSong.createMemberSong(member, newSong);
        return newSong.getTitle() + " " + newSong.getArtist() + "가 성공적으로 등록되었습니다.";
    }
}