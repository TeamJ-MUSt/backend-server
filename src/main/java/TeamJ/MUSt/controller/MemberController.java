package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members/new")
    public Long create(@RequestParam String name){
        Member member = new Member(name);
        return memberService.join(member);
    }

}
