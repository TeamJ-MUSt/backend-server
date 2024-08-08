package TeamJ.MUSt.util;

import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.service.song.SongInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BugsCrawler {
    static final String QUERY_FILE = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\queries.txt";
    static final String SEARCH_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\search.py";
    static final String LYRICS_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\lyrics.py";
    static final String PYTHON = "python";
    static final int MAX_RESULT_SIZE = 5;


    public static List<SongInfo> searchSongsInBugs(String title, String artist) throws NoSearchResultException {
        List<SongInfo> searchResults = new ArrayList<>();
        makeQuery(title, artist);

        try {
            Process searchProcess = startPythonScript(PYTHON, SEARCH_SCRIPT, QUERY_FILE);

            searchResults = getSearchResult(searchProcess);

            List<SongInfo> garbageList = findGarbageResult(searchResults);

            removeGarbageResultFrom(searchResults, garbageList);

            if (searchResults.isEmpty())
                throw new NoSearchResultException();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (searchResults.size() <= MAX_RESULT_SIZE)
            return searchResults;

        return getFirstFiveResults(searchResults);
    }

    public static String getLyrics(String id) throws IOException {
        Process lyricsProcess = startPythonScript(PYTHON, LYRICS_SCRIPT, id);

        BufferedReader br = new BufferedReader(new InputStreamReader(lyricsProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();

        if(str.isEmpty())
            return "none";

        return str;
    }
    public static byte[] imageToByte(String imageURL) {
        byte[] imageBytes = null;
        try {
            URL url = new URL(imageURL);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            imageBytes = readBytesFrom(inputStream, outputStream);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }
    private static void makeQuery(String title, String artist) {
        try (PrintWriter pw = new PrintWriter(QUERY_FILE)) {
            if (title == null){
                pw.println(artist);
                return;
            }

            if (artist == null){
                pw.println(title);
                return;
            }

            pw.println(title + " " + artist);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    private static byte[] readBytesFrom(InputStream stream, ByteArrayOutputStream outputStream) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[4096];
        while ((bytesRead = stream.read(buffer)) > 0)
            outputStream.write(buffer, 0, bytesRead);


        return outputStream.toByteArray();
    }

    private static List<SongInfo> getFirstFiveResults(List<SongInfo> searchResults) {
        List<SongInfo> subResults = new ArrayList<>();
        for (int i = 0; i < MAX_RESULT_SIZE; i++)
            subResults.add(searchResults.get(i));
        return subResults;
    }

    private static void removeGarbageResultFrom(List<SongInfo> searchResults, List<SongInfo> garbageList) {
        for (SongInfo songInfo : garbageList) {
            searchResults.remove(songInfo);
        }
    }

    private static List<SongInfo> findGarbageResult(List<SongInfo> searchResults) {
        List<SongInfo> garbageList = new ArrayList<>();
        for (SongInfo searchResult : searchResults) {
            String id = searchResult.getMusic_id();
            if (id == null)
                garbageList.add(searchResult);
        }
        return garbageList;
    }

    private static Process startPythonScript(String... commandLineInputs) throws IOException {
        ProcessBuilder searchProcessBuilder = new ProcessBuilder(commandLineInputs);
        return searchProcessBuilder.start();
    }

    private static List<SongInfo> getSearchResult(Process p) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine();

        ObjectMapper mapper = new ObjectMapper();
        List<List<SongInfo>> searchResult = mapper.readValue(str, new TypeReference<List<List<SongInfo>>>() {});
        return new ArrayList<>(searchResult.get(0));
    }
}
