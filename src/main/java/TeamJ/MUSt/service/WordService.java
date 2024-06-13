package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.WordRepository;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {
    static String wordFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\word_db";
    private final WordRepository wordRepository;

    public void writeWordList() {
        List<Word> words = wordRepository.findAll();
        List<WordDbForm> list = words.stream().map(WordDbForm::new).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();


        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(new File(wordFile), JsonEncoding.UTF8)) {
            jsonGenerator.writeStartArray();

            for (WordDbForm word : list) {
                objectMapper.writeValue(jsonGenerator, word);
            }

            jsonGenerator.writeEndArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Getter
    static class WordDbForm {
        String lemma;
        String speechField;
        String pronunciation;
        List<String> meanings;

        public WordDbForm(Word word) {
            this.lemma = word.getSpelling();
            this.speechField = word.getClassOfWord();
            this.pronunciation = word.getJpPronunciation();
            this.meanings = word.getMeaning().stream().map(Meaning::getMeaning).toList();
        }
    }
}
