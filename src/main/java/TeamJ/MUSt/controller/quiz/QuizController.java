package TeamJ.MUSt.controller.quiz;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import TeamJ.MUSt.repository.quiz.QuizRepository;
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
    public static final int SET_SIZE = 20;
    private final QuizService quizService;
    private final QuizRepository quizRepository;

    @GetMapping("/quiz/info")
    public SetNumDto quizz(@RequestParam("songId") Long songId, @RequestParam("quizType") QuizType quizType) {
        int quizNum = quizRepository.countBySongIdAndType(songId, quizType);

        if (isQuizAlreadyMade(quizNum))
            return new SetNumDto();

        int setNum = getSetNum(quizNum);

        return new SetNumDto(true, setNum);
    }

    @GetMapping("/quiz/{type}/set/{setNum}")
    public QuizResultDto quizzesSet(
            @RequestParam("songId") Long songId,
            @PathVariable("type") QuizType type,
            @PathVariable("setNum") Integer pageNum
    ) {
        List<QuizDto> quizDtos = quizService
                .findQuizzes(songId, type, pageNum)
                .stream()
                .map(QuizDto::new)
                .toList();

        if (quizDtos.isEmpty())
            return new QuizResultDto(false);

        return new QuizResultDto(true, quizDtos);
    }

    @PostMapping("/quiz/new")
    public QuizResultDto makeQuiz(@RequestParam("songId") Long songId, @RequestParam("quizType") QuizType quizType) throws IOException {
        List<Quiz> createdQuizzes = new ArrayList<>();

        if (quizType == MEANING)
            createdQuizzes = quizService.createMeaningQuiz(songId);

        else if (quizType == READING)
            createdQuizzes = quizService.createReadingQuiz(songId);

        else if (quizType == SENTENCE)
            createdQuizzes = quizService.createSentenceQuiz(songId);

        if (createdQuizzes == null || createdQuizzes.isEmpty())//null을 반환하지 않게 하자
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


    private static boolean isQuizAlreadyMade(int quizNum) {
        return quizNum == 0;
    }

    private static int getSetNum(int quizNum) {
        return quizNum % SET_SIZE <= 10 ? quizNum / SET_SIZE : quizNum / SET_SIZE + 1;
    }
}
