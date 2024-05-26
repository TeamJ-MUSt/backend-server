package TeamJ.MUSt.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class SentenceSplitter {
    static String splitterScript = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\sentence-splitter\\split.py";

    public String[] splitSentence(String sentence) throws IOException {
        ProcessBuilder splitProcessBuilder = new ProcessBuilder("python", splitterScript, sentence);
        Process extractProcess = splitProcessBuilder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine().replaceAll("'", "");
        if(str.equals("[]"))
            return null;
        str = str.substring(1, str.length() - 1);
        return str.split(", ");
    }
}
