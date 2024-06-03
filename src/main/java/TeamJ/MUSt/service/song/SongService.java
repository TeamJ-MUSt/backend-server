package TeamJ.MUSt.service.song;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.util.BugsCrawler;
import TeamJ.MUSt.util.WordExtractor;
import TeamJ.MUSt.util.WordInfo;
import com.querydsl.core.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final MemberRepository memberRepository;
    private final WordExtractor wordExtractor;
    private final WordRepository wordRepository;
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
        return songRepository.findRequestSong(title, artist);
    }

    public List<Tuple> searchSong(Long memberId, String title, String artist){
        return songRepository.findWithMemberSongCheckingRegister(memberId, title, artist);
    }

    @Transactional
    public List<Song> searchRemoteSong(String title, String artist) {
        List<Song> newSongs = new ArrayList<>();
        try{
            List<SongInfo> searchResults = BugsCrawler.callBugsApi(title, artist);
            System.out.println("searchResults.size() = " + searchResults.size());
            for (SongInfo searchResult : searchResults) {
                String lyrics = searchResult.getLyrics();
                if(lyrics.length() == 4)
                    lyrics = "";
                else
                    lyrics = lyrics.substring(2, lyrics.length() - 2);
                Song newSong = new Song(searchResult.getTitle(), searchResult.getArtist(), lyrics, BugsCrawler.imageToByte(searchResult.getThumbnailUrl_large()));
                songRepository.save(newSong);
                newSongs.add(newSong);
            }

        }catch (NoSearchResultException e){
            return null;
        }
        return newSongs;
    }

    @Transactional
    public String registerSong(Long userId, Long songId) throws NoSearchResultException {
        long start = System.currentTimeMillis();
        Member member = memberRepository.findById(userId).get();
        Song newSong = songRepository.findById(songId).get();
        System.out.println("이 노래의 단어 수 = " + newSong.getSongWords().size());
        if(newSong.getSongWords().isEmpty()){
            List<WordInfo> wordInfos = wordExtractor.extractWords(newSong);
            System.out.println("추출된 단어 수 = " + wordInfos.size());
            for (WordInfo wordInfo : wordInfos) {
                String spelling = wordInfo.getSurface();
                Word findWord = wordRepository.findBySpelling(spelling);

                if(findWord == null){
                    List<String> before = wordInfo.getMeaning();
                    before = before.stream()
                            .map(s -> s.endsWith(".") ? s.substring(0, s.length() - 1) : s).toList();
                    List<Meaning> after = before.stream().map(Meaning::new).toList();
                    Word newWord = new Word(
                            wordInfo.getLemma(),
                            wordInfo.getPronunciation(),
                            after,
                            wordInfo.getSpeechFields());
                    wordRepository.save(newWord);
                    for (Meaning meaning : after) {
                        meaning.setWord(newWord);
                    }
                    SongWord songWord = new SongWord();
                    songWord.createSongWord(newSong, newWord, wordInfo.getSurface());
                    newSong.getSongWords().add(songWord);
                }
            }
        }
        MemberSong memberSong = new MemberSong();
        memberSong.createMemberSong(member, newSong);
        long end = System.currentTimeMillis();
        System.out.println("걸리는 시간 : " + (end - start) / 1000);
        return newSong.getTitle() + " " + newSong.getArtist() + "가 성공적으로 등록되었습니다.";
    }
}
