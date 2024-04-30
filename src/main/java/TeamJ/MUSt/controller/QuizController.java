package TeamJ.MUSt.controller;

import TeamJ.MUSt.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/quizzes/{songId}/{type}")
    public List<QuizDto> quizzes(@PathVariable Long songId, @PathVariable String type) {
        //dto로 바꾸기
        return quizService.findQuizzes(songId, type)
                .stream().map(quiz -> new QuizDto(quiz)).toList();

    }

    //다 풀고 나서 단어장 등록을 위한 것
    @PostMapping("/quizzes/{userId}/{songId}")
    public void solve(@PathVariable Long userId, @PathVariable Long songId) {
        quizService.updateWordBook(userId, songId);
    }
}
