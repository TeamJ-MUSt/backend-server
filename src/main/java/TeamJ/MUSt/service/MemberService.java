package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Long join(Member member){
        memberRepository.save(member);
        return member.getId();
    }
}
