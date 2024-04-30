package TeamJ.MUSt.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 3600)
@RestController
public class HelloController {
    @GetMapping("/hello")
    public TestDto hello(){
        System.out.println("옴");
        //return new TestDto("통신 성공");
        return new TestDto("통신 성공");
    }

    @Getter @Setter
    static class TestDto{
        String data;
        public TestDto(String data) {
            this.data = data;
        }
    }
}