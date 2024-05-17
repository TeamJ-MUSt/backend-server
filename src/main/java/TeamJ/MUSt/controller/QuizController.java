package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import TeamJ.MUSt.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/quiz")
    public QuizResultDto quizzes(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type) {
        //dto로 바꾸기
        List<QuizDto> quizDtos = quizService.findQuizzes(songId, type)
                .stream().map(quiz -> new QuizDto(quiz)).toList();
        if(quizDtos.isEmpty())
            return new QuizResultDto(false);
        return new QuizResultDto(true, quizDtos);
    }

    @PostMapping("/quiz/create")
    public QuizResultDto makeQuiz(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type){
        List<Quiz> quizzes = new ArrayList<>();
        if(type == QuizType.MEANING)
            quizzes = quizService.createMeaningQuiz(songId);
        else if(type == QuizType.READING)
            quizzes = quizService.createReadingQuiz(songId);

        if(quizzes == null || quizzes.isEmpty())
            return new QuizResultDto(false);

        System.out.println("이 명령은 끝났다");
        return new QuizResultDto(true);
    }

    //다 풀고 나서 단어장 등록을 위한 것
    /*@PostMapping("/quiz/{userId}/{songId}")
    public void solve(@PathVariable Long userId, @PathVariable Long songId) {
        quizService.updateWordBook(userId, songId);
    }*/
}
