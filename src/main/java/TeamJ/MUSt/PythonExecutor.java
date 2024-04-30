package TeamJ.MUSt;

import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.service.song.SongInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PythonExecutor {
    static String queryFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\queries.txt";
    static String pythonFile = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\lyrics-fetcher\\fetch.py";
    static String noResultMessage = "Unrecognized token 'None': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";

    public static SongInfo callBugsApi(String title, String artist) throws NoSearchResultException {
        SongInfo result = null;
        makeQuery(title, artist);

        try {
            ProcessBuilder pb = new ProcessBuilder("python", pythonFile, queryFile);
            Process p = pb.start();

            result = callApi(p);
            printErrorMessage(p);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new NoSearchResultException();
        }
        return result;
    }

    private static void printErrorMessage(Process p) throws IOException{
        InputStream errorStream = p.getErrorStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            System.out.println(errorLine);
        }
    }

    private static SongInfo callApi(Process p) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));

        String str = "";
        int line = 0;
        StringBuilder sb = new StringBuilder();
        str = br.readLine();
        str = str.replaceAll("'", "\"");
        ObjectMapper mapper = new ObjectMapper();
        SongInfo[] songs = mapper.readValue(str, SongInfo[].class);
        return songs[0];
    }

    private static void makeQuery(String title, String artist) {
        try (PrintWriter pw = new PrintWriter(queryFile)) {
            pw.println(title + " " + artist);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


}
