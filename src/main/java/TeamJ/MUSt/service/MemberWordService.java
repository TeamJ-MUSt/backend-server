package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberWord;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.word.WordRepository;
import TeamJ.MUSt.repository.wordbook.MemberWordRepository;
import TeamJ.MUSt.util.NlpModule;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberWordService {
    private final MemberWordRepository memberWordRepository;
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;
    public List<Word> findUserWord(Long memberId){

        return memberWordRepository.findWithWordByMemberId(memberId);

    }
    @Transactional
    public long deleteWord(Long memerId, Long songId) {
        long deleted = memberWordRepository.deleteMemberWordByMemberIdAndSongId(memerId, songId);
        em.flush();
        em.clear();
        return deleted;
    }
    @Transactional
    public boolean register(Long memberId, Long songId){
        Optional<Member> member = memberRepository.findById(memberId);
        List<Word> words = wordRepository.findWithSong(songId);

        if(isInvalidRequest(member, words))
            return false;

        Member findMember = member.get();

        for (Word word : words) {
            MemberWord memberWord = new MemberWord(findMember, word);
            findMember.getMemberWords().add(memberWord);
        }

        return true;
    }

    public List<Word> getSimilarWords(Long wordId, Integer num) throws IOException {
        Word findWord = wordRepository.findById(wordId).get();

        List<String> similarWordSpellings = NlpModule.getSpellingOfSimilarWord(findWord.getSpelling(), num);

        return findSimilarWordEntity(similarWordSpellings);
    }

    private List<Word> findSimilarWordEntity(List<String> similarWordSpellings) {
        List<Word> result = new ArrayList<>();
        for (String spelling : similarWordSpellings) {
            Word similarWord = wordRepository.findBySpelling(spelling);
            if(similarWord != null)
                result.add(similarWord);
        }
        return result;
    }

    private static boolean isInvalidRequest(Optional<Member> member, List<Word> words) {
        return member.isEmpty() || words.isEmpty();
    }
}
