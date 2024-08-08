package TeamJ.MUSt.controller.quiz;

import TeamJ.MUSt.domain.Answer;
import TeamJ.MUSt.domain.Choice;
import TeamJ.MUSt.domain.Quiz;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class QuizDto {
    private String word;
    private List<String> answers = new ArrayList<>();
    private List<String> choices = new ArrayList<>();



    public QuizDto(Quiz quiz) {

        if(quiz.getWord() == null)
            this.word = null;
        else
            this.word = quiz.getWord().getSpelling();

        this.answers = quiz.getAnswers().stream().map(Answer::getContent).toList();

        this.choices = quiz.getChoices().stream().map(Choice::getContent).toList();
    }
}
