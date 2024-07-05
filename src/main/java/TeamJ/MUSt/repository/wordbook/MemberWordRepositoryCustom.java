package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.MemberWord;

import java.util.List;

public interface MemberWordRepositoryCustom {


    long deleteMemberWordByMemberIdAndSongId(Long memberId, Long songId);
}
