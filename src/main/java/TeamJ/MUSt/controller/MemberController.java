package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members/new")
    public Long create(@RequestParam("name") String name, @RequestParam("password") String password){
        Member member = new Member(name, password);
        return memberService.join(member);
    }

    @PostMapping("/login")
    public LoginResultDto login(
            @Valid @RequestParam("loginId") String loginId,
            @Valid @RequestParam("password") String password,
            HttpServletRequest request){
        Member loginMember = memberService.login(loginId, password);
        if(loginMember == null)
            return new LoginResultDto(false);

        String uuid = UUID.randomUUID().toString();
        HttpSession session = request.getSession();
        session.setAttribute(uuid, loginMember);
        return new LoginResultDto(true);
    }

    @Getter
    @Setter
    static class LoginResultDto{
        boolean success;
        public LoginResultDto(boolean success) {
            this.success = success;
        }
    }
}
