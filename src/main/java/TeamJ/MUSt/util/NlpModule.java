package TeamJ.MUSt.util;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class NlpModule {
    static String contextScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\context_definition.py";
    static String similarWordScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\similar_words.py";
    static String wordDbFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\word_db";
    static String embeddingFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\embeddings.txt";

    public int reflectContext(String lyric, Word word, String conjugation, int order) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        WordData wordData = new WordData(conjugation, word.getMeaning().stream().map(Meaning::getMeaning).toList());

        String[] sentences = lyric.split("\\\\n");

        ArrayList<String> matchingSentences = new ArrayList<>();
        for (String sentence : sentences) {
            if (sentence.contains(conjugation))
                matchingSentences.add(sentence);
        }
        String targetSentence = matchingSentences.get(order);
        String query = targetSentence + "@" + mapper.writeValueAsString(wordData);

        query = query.replace('\"', '#');
        ProcessBuilder contextProcessBuilder = new ProcessBuilder("python", contextScript, query);
        Process extractProcess = contextProcessBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();
        return Integer.parseInt(str.substring(1, 2));
    }

    public ArrayList<String> reflectContextV2(String entireQuery) throws IOException {
        ProcessBuilder contextProcessBuilder = new ProcessBuilder("python", contextScript, entireQuery);
        Process extractProcess = contextProcessBuilder.start();
        ObjectMapper objectMapper = new ObjectMapper();
        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();
        return objectMapper.readValue(str, new TypeReference<ArrayList<String>>() {
        });
    }

    public List<String> getSimilarWord(String spell, int num) throws IOException {
        ProcessBuilder contextProcessBuilder = new ProcessBuilder("python", similarWordScript, wordDbFile, embeddingFile, spell, String.valueOf(num));
        Process extractProcess = contextProcessBuilder.start();
        List<SimilarWord> wordData = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
            String str = br.readLine().replaceAll("'", "\"");
            ObjectMapper mapper = new ObjectMapper();
            wordData = mapper.readValue(str, new TypeReference<List<SimilarWord>>() {
            });
        } catch (Exception e) {
            System.out.println("에러 메세지 : " + e.getMessage());
        }
        return wordData.stream().map(SimilarWord::getLemma).toList();
    }

    public String createQuerySentence(String lyric, Word word, String conjugation, int order) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WordData wordData = new WordData(conjugation, word.getMeaning().stream().map(Meaning::getMeaning).toList());

        String[] sentences = lyric.split("\\\\n");

        ArrayList<String> matchingSentences = new ArrayList<>();
        for (String sentence : sentences) {
            if (sentence.contains(conjugation))
                matchingSentences.add(sentence);
        }
        String targetSentence = matchingSentences.get(order);
        String query = targetSentence + "@" + mapper.writeValueAsString(wordData);

        return query.replace('\"', '#');
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
