package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.domain.WordBook;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.QuizRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.repository.wordbook.WordBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final SongRepository songRepository;
    private final WordBookRepository wordBookRepository;
    public List<Quiz> findQuizzes(Long songId, String type){
        return quizRepository.findBySongIdAndType(songId, type);
    }

    @Transactional
    public void updateWordBook(Long userId, Long songId){
        Optional<Member> findMember = memberRepository.findById(userId);
        List<Word> usedWords = songRepository.findUsedWords(songId);
        for (Word word : usedWords) {
            WordBook wordBook = new WordBook(findMember.get(), word);
            wordBookRepository.save(wordBook);
        }
    }
}
