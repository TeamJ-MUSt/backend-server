package TeamJ.MUSt.controller.quiz;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class QuizResultDto {
    private boolean success;
    private List<QuizDto> quizDtos = new ArrayList<>();

    public QuizResultDto(boolean success, List<QuizDto> quizDtos) {
        this.success = success;
        this.quizDtos = quizDtos;
    }

    public QuizResultDto(boolean success) {
        this.success = success;
    }

    public QuizResultDto() {
    }
}
