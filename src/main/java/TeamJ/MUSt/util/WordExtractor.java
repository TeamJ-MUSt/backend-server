package TeamJ.MUSt.util;

import TeamJ.MUSt.domain.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WordExtractor {
    static final String QUERY_FILE = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\queries.txt";
    static final String EXTRACT_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\extract_words.py";
    static final String MEANING_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\word-extractor\\search_definitions.py";
    static final String PYTHON = "python";
    static final String EMPTY_SING = "[]";
    static final int MAX_MEANING_LENGTH = 25;

    public static void findMeaning(List<WordInfo> wordList, Song song){
        StringBuilder sb = new StringBuilder();
        makeQuery(wordList, sb);

        writeToQueryFile(sb.toString().trim());

        int wordCountHavingLevel = 0;
        HashMap<Integer, Integer> levelMap = initLevelMap();
        
        try{
            List<MeaningResult> meaningList = getMeaningResult();
            wordCountHavingLevel = assembleWordAndMeaning(wordList, meaningList, levelMap);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        
        if(hasJlptWord(wordCountHavingLevel)){
            calculateLevelOf(song, levelMap);
            return;
        }

        song.updateLevel(0);
    }

    public static List<MeaningResult> getMeaningResult() throws IOException{
        Process meaningProcess = startPythonScript(PYTHON, MEANING_SCRIPT, QUERY_FILE, "--threads=4");
        BufferedReader br = new BufferedReader(new InputStreamReader(meaningProcess.getInputStream(), StandardCharsets.UTF_8));
        String outputJson = br.readLine();

        if(isEmptyResult(outputJson))
            return new ArrayList<>();

        outputJson = adjustJsonFormat(outputJson);

        //if(str.length() <= 2)
        //    return meaningResults;


        List<MeaningResult> meaningResults = new ArrayList<>();

        if(outputJson.equals(EMPTY_SING))
            return meaningResults;

        outputJson = removeBrackets(outputJson);
        parseJsonIntoMeaning(outputJson, meaningResults);

        return meaningResults;
    }


    public static List<WordInfo> extractWords(Song song) {
        if(hasNoLyric(song)){
            song.updateLevel(0);
            return new ArrayList<>();
        }

        String lyrics = convertLyricIntoString(song);

        writeToQueryFile(lyrics);

        List<WordInfo> wordsList = new ArrayList<>();
        try{
            convertExtractResultIntoWordInfo(wordsList);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        if(wordsList.isEmpty())
            return new ArrayList<>();

        return wordsList.stream().distinct().toList();
    }
    private static void parseJsonIntoMeaning(String json, List<MeaningResult> meaningResults) {
        String[] jsonItems = splitIntoEachJsonItem(json);

        ObjectMapper mapper = new ObjectMapper();
        for (String item : jsonItems) {
            item = "{" + item + "}";

            try {
                MeaningResult result = mapper.readValue(item, MeaningResult.class);

                if (hasNoDefinition(result)){
                    addEmtpyItemInto(meaningResults);
                    continue;
                }

                result.setDefinitions(refineMeanings(result));

                meaningResults.add(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                addEmtpyItemInto(meaningResults);
            }
        }
    }

    private static void convertExtractResultIntoWordInfo(List<WordInfo> wordsList) throws IOException {
        List<ExtractResult> extractResults = getExtractResult();
        for (ExtractResult extractResult : extractResults) {
            if (existInDictionary(extractResult)){
                WordInfo wordInfo = new WordInfo(
                        extractResult.getSurface(),
                        extractResult.getSpeechFields().get(0),
                        extractResult.getLemma().split("-")[0]);
                wordsList.add(wordInfo);
            }
        }
    }

    private static void calculateLevelOf(Song song, HashMap<Integer, Integer> levelMap) {
        float easyQuizNum = getAverageCountOfEasyLevels(levelMap);
        float normalQuizNum = getMiddleLevelCount(levelMap);
        float hardQuizNum = getAverageCountOfHardLevels(levelMap);

        float max = Math.max(easyQuizNum, Math.max(normalQuizNum, hardQuizNum));

        if(max == easyQuizNum){
            song.updateLevel(1);
            return;
        }

        if(max == normalQuizNum) {
            song.updateLevel(2);
            return;
        }

        if(max == hardQuizNum) {
            song.updateLevel(3);
            return;
        }

        song.updateLevel(0);
    }

    private static int assembleWordAndMeaning(List<WordInfo> wordList, List<MeaningResult> meaningResult, HashMap<Integer, Integer> levelMap) {
        int wordCountHavingLevel = 0;
        for(int i = 0; i < meaningResult.size(); i++){
            WordInfo current = wordList.get(i);
            levelMap.merge(meaningResult.get(i).getLevel(), 1, Integer::sum);

            if(current.getSurface().equals("\\"))
                continue;
            if(hasJlptLevel(meaningResult, i))
                wordCountHavingLevel++;

            if(doesMeaningResultExist(meaningResult, i)) {
                current.setMeaning(meaningResult.get(i).getDefinitions());
                current.setPronunciation(meaningResult.get(i).getPronunciation());
            }
        }
        return wordCountHavingLevel;
    }

    private static HashMap<Integer, Integer> initLevelMap() {
        HashMap<Integer, Integer> map = new HashMap<>();

        for(int i = 1; i <= 5; i++)
            map.put(i, 0);
        return map;
    }

    public static void writeToQueryFile(String input) {
        try (PrintWriter pw = new PrintWriter(QUERY_FILE)) {
            pw.print(input);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<ExtractResult> getExtractResult() throws IOException {
        Process extractProcess = startPythonScript(PYTHON, EXTRACT_SCRIPT, QUERY_FILE);

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();
        str = str.replaceAll("\'", "\"");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(str, new TypeReference<List<ExtractResult>>(){});
    }

    private static List<String> refineMeanings(MeaningResult result) {
        return result.getDefinitions().stream()
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> isShortEnough(s))
                .toList();
    }
    private static float getAverageCountOfHardLevels(HashMap<Integer, Integer> levelMap) {
        return ((float) levelMap.get(1) + levelMap.get(2)) / 2;
    }

    private static Integer getMiddleLevelCount(HashMap<Integer, Integer> levelMap) {
        return levelMap.get(3);
    }

    private static float getAverageCountOfEasyLevels(HashMap<Integer, Integer> levelMap) {
        return ((float) levelMap.get(4) + levelMap.get(5)) / 2;
    }

    private static boolean hasJlptWord(int wordCountHavingLevel) {
        return wordCountHavingLevel != 0;
    }

    private static boolean existInDictionary(ExtractResult extractResult) {
        if(extractResult.getSurface().equals("\\"))
            return false;

        return true;
    }

    private static String convertLyricIntoString(Song song) {
        return new String(song.getLyric());
    }

    private static boolean hasNoLyric(Song song) {
        return song.getLyric() == null || song.getLyric().length == 0;
    }

    private static void makeQuery(List<WordInfo> wordList, StringBuilder sb) {
        for (WordInfo wordInfo : wordList)
            sb.append(wordInfo.getLemma()).append(" ");
    }

    private static boolean isShortEnough(String s) {
        return s.length() < MAX_MEANING_LENGTH;
    }

    private static void addEmtpyItemInto(List<MeaningResult> results) {
        results.add(new MeaningResult());
    }

    private static boolean hasNoDefinition(MeaningResult result) {
        return result.getDefinitions().get(0).equals("empty");
    }

    private static String[] splitIntoEachJsonItem(String str) {
        return str.split("},\\s*\\{");
    }

    private static String removeBrackets(String str) {
        return str.substring(2, str.length() - 2);
    }

    private static String adjustJsonFormat(String str) {
        return str.replaceAll("'(\\w+)':", "\"$1\":").replaceAll("'(.*?)'", "\"$1\"");
    }
    private static boolean hasJlptLevel(List<MeaningResult> meaningResult, int i) {
        return meaningResult.get(i).getLevel() != -1;
    }

    private static boolean isEmptyResult(String str) {
        return str == null;
    }
    private static boolean doesMeaningResultExist(List<MeaningResult> meaningResult, int i) {
        List<String> definitions = meaningResult.get(i).getDefinitions();

        if(definitions == null || definitions.isEmpty())
            return false;

        return true;
    }

    private static Process startPythonScript(String... commandLineInputs) throws IOException {
        ProcessBuilder searchProcessBuilder = new ProcessBuilder(commandLineInputs);
        return searchProcessBuilder.start();
    }

    @Getter
    @Setter
    @ToString
    static class ExtractResult {
        private String surface;
        private List<String> speechFields;
        private String lemma;
        public ExtractResult() {
        }
    }
}