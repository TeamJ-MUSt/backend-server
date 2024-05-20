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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WordExtractor {
    static String queryFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\queries.txt";
    static String extractScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\extract_words.py";
    static String meaningScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\search_definitions.py";
    private final SongRepository songRepository;
    public List<WordInfo> extractWords(Song newSong) throws IOException {
        if(newSong.getLyric() == null || newSong.getLyric().length == 0)
            return null;

        List<WordInfo> wordsList = new ArrayList<>();
        //Song findSong = songRepository.findById(songId).get();
        String lyrics = new String(newSong.getLyric());
        makeQuery(lyrics);

        int levelSum = 0;
        int levelCount = 0;

        try{
            List<ParsingResult> extractResult = getExtractResult();
            StringBuilder sb = new StringBuilder();
            for (ParsingResult item : extractResult)
                sb.append(item.lemma).append(" ");

            makeQuery(sb.toString().trim());
            List<MeaningResult> meaningResult = getMeaningResult();


            for(int i = 0; i < meaningResult.size(); i++){
                ParsingResult current = extractResult.get(i);
                if(current.getSurface().equals("\\"))
                    continue;

                if(meaningResult.get(i).level != -1){
                    levelSum += meaningResult.get(i).level;
                    levelCount++;
                }

                WordInfo wordInfo = new WordInfo(
                        current.getSurface(),
                        current.getSpeechFields().get(0),
                        current.getPronunciation(),
                        current.getLemma().split("-")[0]);
                if(meaningResult.get(i).definitions != null) {
                    wordInfo.setMeaning(meaningResult.get(i).definitions);
                    wordsList.add(wordInfo);
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        newSong.setLevel(Math.round((float) levelSum / levelCount));
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

    private List<MeaningResult> getMeaningResult() throws IOException{
        List<MeaningResult> results = new ArrayList<>();
        ProcessBuilder meaningProcessBuilder = new ProcessBuilder("python", meaningScript, queryFile);
        Process meaningProcess = meaningProcessBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(meaningProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();
        if(str == null)
            return null;
        str = str.replaceAll("'(\\w+)':", "\"$1\":").replaceAll("'(.*?)'", "\"$1\"");
        str = str.substring(2, str.length() - 2);
        String[] jsonItems = str.split("},\\s*\\{");
        ObjectMapper mapper = new ObjectMapper();
        for (String item : jsonItems) {
            item = "{" + item + "}";
            try {
                MeaningResult result = mapper.readValue(item, MeaningResult.class);
                if (result.definitions.get(0).equals("empty")){
                    results.add(new MeaningResult());
                    continue;
                }
                result.definitions = result.definitions.stream()
                        .flatMap(s -> Arrays.stream(s.split(",")))
                        .map(String::trim)
                        .filter(s -> s.length() < 25)
                        .toList();
                results.add(result);
            } catch (Exception e) {
                System.out.println("문제 있는 녀석 " + item);
                System.out.println(e.getMessage());
                results.add(new MeaningResult());
            }
        }
        return results;
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
        private int level;
        public MeaningResult(){
        }
    }
}