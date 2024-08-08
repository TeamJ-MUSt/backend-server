package TeamJ.MUSt.util;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class NlpModule {
    static final String CONTEXT_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\context_definition.py";
    static final String SIMILAR_WORD_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\similar_words.py";
    static final String WORD_DB_FILE = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\word_db";
    static final String EMBEDDING_FILE = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\embeddings.txt";
    static final String PYTHON = "python";
    static final String SENTENCE_WORD_DELIMITER = "@";
    static final String KEY_VALUE_DELIMITER = "#";

    public static ArrayList<String> reflectContext(String entireQuery) throws IOException {
        Process extractProcess = startPythonScript(PYTHON, CONTEXT_SCRIPT, entireQuery);


        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(str, new TypeReference<ArrayList<String>>() {
        });
    }

    public static List<String> getSpellingOfSimilarWord(String spell, int wordNum) throws IOException {
        Process extractProcess = startPythonScript(PYTHON, SIMILAR_WORD_SCRIPT, WORD_DB_FILE, EMBEDDING_FILE, spell, String.valueOf(wordNum));

        List<SimilarWord> wordData = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
            String str = br.readLine().replaceAll("'", "\"");

            ObjectMapper mapper = new ObjectMapper();
            wordData = mapper.readValue(str, new TypeReference<List<SimilarWord>>() {});
        } catch (Exception e) {
            System.out.println("에러 메세지 : " + e.getMessage());
        }
        return wordData.stream().map(SimilarWord::getLemma).toList();
    }

    public static String createQuerySentence(String lyric, Word word, String conjugation) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> meanings = word.getMeaning().stream().map(Meaning::getContent).toList();
        WordData wordData = new WordData(conjugation, meanings);

        String[] sentences = lyric.split("\\\\n");

        String targetSentence = findMatchingSentence(conjugation, sentences);
        String query = getSingleWordQuery(targetSentence, mapper, wordData);

        return query.replace("\"", KEY_VALUE_DELIMITER);
    }

    private static Process startPythonScript(String... commandLineInputs) throws IOException {
        ProcessBuilder searchProcessBuilder = new ProcessBuilder(commandLineInputs);
        return searchProcessBuilder.start();
    }

    private static String findMatchingSentence(String conjugation, String[] sentences) {
        String targetSentence = "";
        for (String sentence : sentences) {
            if (sentence.contains(conjugation)) {
                targetSentence = sentence;
                break;
            }
        }
        return targetSentence;
    }

    private static String getSingleWordQuery(String targetSentence, ObjectMapper mapper, WordData wordData) throws JsonProcessingException {
        return targetSentence + SENTENCE_WORD_DELIMITER + mapper.writeValueAsString(wordData);
    }

    @Getter
    static class WordData {
        String word;
        List<String> definitions;

        public WordData(String spelling, List<String> meanings) {
            this.word = spelling;
            this.definitions = meanings;
        }
    }

    @Getter
    @ToString
    static class SimilarWord {
        String lemma;
        Float similarity;

        public SimilarWord(String lemma, Float similarity) {
            this.lemma = lemma;
            this.similarity = similarity;
        }

        public SimilarWord() {
        }
    }
}
