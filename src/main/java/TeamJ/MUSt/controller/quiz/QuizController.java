package TeamJ.MUSt.controller.quiz;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import TeamJ.MUSt.repository.QuizRepository;
import TeamJ.MUSt.service.QuizService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static TeamJ.MUSt.domain.QuizType.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class QuizController {
    private final QuizService quizService;
    private final QuizRepository quizRepository;

    @GetMapping("/quiz/info")
    public SetNumDto quizz(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type) {
        int quizNum = quizRepository.countBySongIdAndType(songId, type);
        if (quizNum == 0)
            return new SetNumDto();
        int setNum = quizNum % 20 <= 10 ? quizNum / 20 : quizNum / 20 + 1;
        return new SetNumDto(true, setNum);
    }

    @GetMapping("/quiz/{type}/set/{setNum}")
    public QuizResultDto quizzesSet(@RequestParam("songId") Long songId,
                                    @PathVariable("type") QuizType type,
                                    @PathVariable("setNum") Integer pageNum) {
        List<QuizDto> quizDtos = quizService.findQuizzes(songId, type, pageNum).stream()
                .map(QuizDto::new).toList();
        if (quizDtos.isEmpty())
            return new QuizResultDto(false);
        return new QuizResultDto(true, quizDtos);
    }

    @PostMapping("/quiz/new")
    public QuizResultDto makeQuiz(@RequestParam("songId") Long songId, @RequestParam("type") QuizType type) throws IOException {
        List<Quiz> quizzes = new ArrayList<>();
        if (type == MEANING)
            quizzes = quizService.createMeaningQuiz(songId);
        else if (type == READING)
            quizzes = quizService.createReadingQuiz(songId);
        else if (type == SENTENCE)
            quizzes = quizService.createSentenceQuiz(songId);

        if (quizzes == null || quizzes.isEmpty())
            return new QuizResultDto(false);

        return new QuizResultDto(true);
    }

    @Getter
    @Setter
    static class SetNumDto {
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
