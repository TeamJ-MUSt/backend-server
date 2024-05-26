package TeamJ.MUSt.util;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.song.SongRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class NlpModule {
    static String contextScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\context_definition.py";
    static String similarWordScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\similar_words.py";
    static String wordDbFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\word_db";
    static String embeddingFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\nlp-module\\embeddings";

    public int reflectContext(String lyric, Word word, int order) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        WordData wordData = new WordData(word.getSpelling(), word.getMeaning().stream().map(Meaning::getMeaning).toList());

        String[] sentences = lyric.split("\n");

        ArrayList<String> matchingSentences = new ArrayList<>();
        if(matchingSentences.size() == 0){
            System.out.println("문제가 되는 녀석 : " + word.getSpelling());
        }
        for (String sentence : sentences) {
            if(sentence.contains(word.getSpelling()))
                matchingSentences.add(sentence);
        }
        String targetSentence = matchingSentences.get(order);
        String query = targetSentence + "@" + mapper.writeValueAsString(wordData);

        query = query.replace('\"', '#');
        System.out.println(query);
        ProcessBuilder contextProcessBuilder = new ProcessBuilder("python", contextScript, query);
        Process extractProcess = contextProcessBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();
        System.out.println(targetSentence + "에서 " + word.getSpelling() + "의 뜻은 " + word.getMeaning().get(Integer.parseInt(str.substring(1, 2))));
        return Integer.parseInt(str.substring(1, 2));
    }

    public List<String> getSimilarWord(String spell, int num) throws IOException {
        ProcessBuilder contextProcessBuilder = new ProcessBuilder("python", similarWordScript, wordDbFile, embeddingFile, spell, String.valueOf(num));
        Process extractProcess = contextProcessBuilder.start();
        List<SimilarWord> wordData = null;
        try{
           BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
           String str = br.readLine().replaceAll("'", "\"");
           System.out.println("str = " + str);
           ObjectMapper mapper = new ObjectMapper();
           wordData = mapper.readValue(str, new TypeReference<List<SimilarWord>>() {});
       }catch (Exception e){
           System.out.println("에러 메세지 : " + e.getMessage());
       }
       return wordData.stream().map(SimilarWord::getLemma).toList();
    }
    @Getter
    static class WordData{
        String word;
        List<String> definitions;

        public WordData(String spelling, List<String> meanings) {
            this.word = spelling;
            this.definitions = meanings;
        }
    }

    @Getter
    @ToString
    static class SimilarWord{
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
