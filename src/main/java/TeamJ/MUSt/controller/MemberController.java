package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members/new")
    public Long create(@RequestParam("name") String name, @RequestParam("password") String password) {

        Member member = new Member(name, password);

        return memberService.join(member);

    }
    @Getter
    @Setter
    static class LoginResultDto {
        boolean success;

        public LoginResultDto(boolean success) {
            this.success = success;
        }
    }
}
