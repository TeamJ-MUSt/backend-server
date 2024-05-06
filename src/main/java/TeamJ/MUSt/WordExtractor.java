package TeamJ.MUSt;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.song.SongInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WordExtractor {
    static String queryFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\queries.txt";
    static String extractScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\extract_words.py";
    static String meaningScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\search_definitions.py";
    private final SongRepository songRepository;
    public List<WordInfo> extractWords(Long songId) throws IOException {
        List<WordInfo> wordsList = new ArrayList<>();
        Song findSong = songRepository.findById(songId).get();
        String lyrics = new String(findSong.getLyric());
        makeQuery(lyrics);

        try{
            List<ParsingResult> extractResult = getExtractResult();

            for (ParsingResult result : extractResult) {
                if(result.getSurface().equals("\\"))
                    continue;
                WordInfo wordInfo = new WordInfo(
                        result.getSurface(),
                        result.getSpeechFields().get(0),
                        result.getPronunciation(),
                        result.getLemma().split("-")[0]);
                MeaningResult meaningResult = getMeaningResult(wordInfo.getLemma());
                if(meaningResult != null) {
                    wordInfo.setMeaning(meaningResult.getDefinitions());
                    wordsList.add(wordInfo);
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return wordsList;
    }
    private void makeQuery(String lyrics) {
        try (PrintWriter pw = new PrintWriter(queryFile)) {
            pw.println(lyrics);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    private List<ParsingResult> getExtractResult() throws IOException {
        ProcessBuilder extractProcessBuilder = new ProcessBuilder("python", extractScript, queryFile);
        Process extractProcess = extractProcessBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));

        String str = br.readLine();
        str = str.replaceAll("\'", "\"");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(str, new TypeReference<List<ParsingResult>>(){});
    }
    private MeaningResult getMeaningResult(String word) throws IOException{
        if(word == null || word.isEmpty() || word.equals(" "))
            return null;
        ProcessBuilder meaningProcessBuilder = new ProcessBuilder("python", meaningScript, word);
        Process meaningProcess = meaningProcessBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(meaningProcess.getInputStream(), StandardCharsets.UTF_8));

        String str = br.readLine();
        if(str == null)
            return null;
        str = str.replaceAll("'(\\w+)':", "\"$1\":").replaceAll("'(.*?)'", "\"$1\"");


        ObjectMapper mapper = new ObjectMapper();
        MeaningResult meaningResult = null;
        try{
            meaningResult = mapper.readValue(str, MeaningResult.class);
            List<String> definitions = meaningResult.getDefinitions();
            definitions = definitions.stream().map(String::trim).toList();
            meaningResult.setDefinitions(definitions);
        }catch (Exception e){
            return null;
        }
        return meaningResult;
    }
    @Getter
    @Setter
    @ToString
    static class ParsingResult{
        private String surface;
        private List<String> speechFields;
        private String pronunciation;
        private String lemma;
        public ParsingResult() {
        }
    }
    @Getter
    @Setter
    @ToString
    static private class MeaningResult {
        private String word;
        private List<String> definitions;

        public MeaningResult(){
        }
    }
}
