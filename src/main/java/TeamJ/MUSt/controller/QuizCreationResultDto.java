package TeamJ.MUSt.controller;

import lombok.Getter;

import java.util.List;

@Getter
public class QuizCreationResultDto {
    private boolean success;

    public QuizCreationResultDto(boolean success) {
        this.success = success;
    }
}
