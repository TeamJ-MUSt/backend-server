package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;

    @PostMapping("/login")
    public LoginResultDto login(@Valid @ModelAttribute LoginForm loginForm, HttpServletRequest request){
        Member findMember = memberService.findLoginMember(loginForm.username, loginForm.getPassword());
        if(findMember == null)
            return new LoginResultDto(false);

        HttpSession session = request.getSession();
        session.setAttribute("memberId", findMember.getId());

        return new LoginResultDto(true);
    }

    @PostMapping("/logout")
    public LoginResultDto logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session == null)
            return new LoginResultDto(false);

        session.invalidate();
        return new LoginResultDto(true);
    }

    @Getter
    static class LoginResultDto{
        boolean success;
        public LoginResultDto(boolean success){
            this.success = success;
        }
    }
}
