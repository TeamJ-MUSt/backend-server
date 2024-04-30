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
    public Song searchSong(Long memberId, String title, String artist) throws NoSearchResultException {
        List<Song> songs = songRepository.findRequestSong(memberId, title,artist);
        if(songs.isEmpty()){
            System.out.println("노래가 없어서 검색");
            SongInfo songInfo = PythonExecutor.callBugsApi(title, artist);
            byte[] thumbnail = imageToByte(songInfo.getThumbnailUrl());
            return songRepository.save(new Song(
                    songInfo.getTitle(),
                    songInfo.getArtist().split("\\((.*?)\\)")[0],
                    songInfo.getLyrics(),
                    thumbnail));
        }
        else
            return songs.get(0);
    }

    @Transactional
    public String registerSong(Long userId, Long songId) {
        Song song = songRepository.findById(songId).get();
        Member member = memberRepository.findById(userId).get();
        MemberSong memberSong = new MemberSong();
        memberSong.createMemberSong(member, song);
        return song.getTitle() + " " + song.getArtist() + "가 성공적으로 등록되었습니다.";
    }

    public byte[] imageToByte(String imageURL){
        byte[] imageBytes = null;
        try {
            URL url = new URL(imageURL);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream stream = url.openStream();

            while ((bytesRead = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            imageBytes = outputStream.toByteArray();

            stream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

}
