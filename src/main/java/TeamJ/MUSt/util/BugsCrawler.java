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
    static String queryFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\queries.txt";
    static String searchScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\search.py";
    static String lyricsScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\lyrics.py";


    public static List<SongInfo> callBugsApi(String title, String artist) throws NoSearchResultException {
        List<SongInfo> searchResults = new ArrayList<>();
        makeQuery(title, artist);

        try {
            ProcessBuilder searchProcessBuilder = new ProcessBuilder("python", searchScript, queryFile);
            Process searchProcess = searchProcessBuilder.start();
            searchResults = getSearchResult(searchProcess);
            List<SongInfo> garbageList = new ArrayList<>();

            for (SongInfo searchResult : searchResults) {
                String id = searchResult.getMusic_id();
                if (id == null) {
                    garbageList.add(searchResult);
                }
            }

            for (SongInfo songInfo : garbageList) {
                searchResults.remove(songInfo);
            }

            if (searchResults.isEmpty()) {
                throw new NoSearchResultException();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        if (searchResults.size() <= 5)
            return searchResults;
        else {
            List<SongInfo> subResults = new ArrayList<>();
            for (int i = 0; i < 5; i++)
                subResults.add(searchResults.get(i));
            return subResults;
        }
    }

    private static List<SongInfo> getSearchResult(Process p) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String str = "";
        str = br.readLine();

        ObjectMapper mapper = new ObjectMapper();
        List<List<SongInfo>> nestedList = mapper.readValue(str, new TypeReference<List<List<SongInfo>>>() {});
        return new ArrayList<>(nestedList.get(0));
    }

    public static String getLyrics(String id) throws IOException {
        ProcessBuilder lyricsProcessBuilder = new ProcessBuilder("python", lyricsScript, id);
        Process lyricsProcess = lyricsProcessBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(lyricsProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = "";
        str = br.readLine();
        return str;
    }

    private static void makeQuery(String title, String artist) {
        try (PrintWriter pw = new PrintWriter(queryFile)) {
            if (title == null)
                pw.println(artist);
            else if (artist == null)
                pw.println(title);
            else
                pw.println(title + " " + artist);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static byte[] imageToByte(String imageURL) {
        byte[] imageBytes = null;
        try {
            URL url = new URL(imageURL);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream stream = url.openStream();

            while ((bytesRead = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            imageBytes = outputStream.toByteArray();

            stream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

}
