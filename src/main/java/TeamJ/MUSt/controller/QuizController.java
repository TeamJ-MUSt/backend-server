package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import TeamJ.MUSt.repository.QuizRepository;
import TeamJ.MUSt.service.QuizService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
//예외처리
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class QuizController {
    private final QuizService quizService;
    private final QuizRepository quizRepository;
    @GetMapping("/quiz/info")
    public SetNumDto quizz(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type){
        int quizNum = quizRepository.countBySongIdAndType(songId, type);
        if(quizNum == 0)
            return new SetNumDto();
        int setNum = quizNum % 20 <= 10 ? quizNum / 20 : quizNum / 20 + 1;
        return new SetNumDto(true, setNum);
    }

    @GetMapping("/quiz/set")
    public QuizResultDto quizzesSet(@RequestParam("songId") Long songId,
                                 @RequestParam("type") QuizType type,
                                 @RequestParam("pageNum") Integer pageNum) {
        List<QuizDto> quizDtos = quizService.findQuizzes(songId, type, pageNum).stream()
                .map(QuizDto::new).toList();
        if(quizDtos.isEmpty())
            return new QuizResultDto(false);
        return new QuizResultDto(true, quizDtos);
    }

    @PostMapping("/quiz/new")
    public QuizResultDto makeQuiz(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type){
        List<Quiz> quizzes = new ArrayList<>();
        if(type == QuizType.MEANING)
            quizzes = quizService.createMeaningQuiz(songId);
        else if(type == QuizType.READING)
            quizzes = quizService.createReadingQuiz(songId);

        if(quizzes == null || quizzes.isEmpty())
            return new QuizResultDto(false);

        return new QuizResultDto(true);
    }

    @Getter @Setter
    static class SetNumDto{
        boolean success;
        int setNum;

        public SetNumDto() {
        }

        public SetNumDto(boolean success, int setNum) {
            this.success = success;
            this.setNum = setNum;
        }
    }
}
