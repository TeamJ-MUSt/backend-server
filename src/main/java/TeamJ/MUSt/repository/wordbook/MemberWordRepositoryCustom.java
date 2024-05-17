package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.MemberWord;

import java.util.List;

public interface MemberWordRepositoryCustom {
    List<MemberWord> findWithMemberAndWord(Long MemberId);
    long deleteBySpellingAndMeaning(String spelling, String meaning);
    long deleteMemberWordByMemberIdAndSongId(Long memberId, Long songId);
}
