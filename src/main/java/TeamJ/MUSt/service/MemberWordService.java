package TeamJ.MUSt.service;

import TeamJ.MUSt.controller.WordSearch;
import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberWord;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.wordbook.MemberWordRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(member.isEmpty() || words.isEmpty())
            return false;
        Member findMember = member.get();
        for (Word word : words) {
            MemberWord memberWord = new MemberWord(findMember, word);
            findMember.getMemberWords().add(memberWord);
        }
        return true;
    }
}