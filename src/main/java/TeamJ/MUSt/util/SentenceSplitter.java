package TeamJ.MUSt.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class SentenceSplitter {
    static final String SPLITTER_SCRIPT = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\sentence-splitter\\split.py";
    static final String PYTHON = "python";
    static final String EMPTY_RESULT = "[]";

    public static String[] splitSentence(String sentence) throws IOException {
        Process extractProcess = startPythonScript(PYTHON, SPLITTER_SCRIPT, sentence);

        BufferedReader br = new BufferedReader(new InputStreamReader(extractProcess.getInputStream(), StandardCharsets.UTF_8));
        String str = br.readLine().replaceAll("'", "");
        
        if(str.equals(EMPTY_RESULT))
            return new String[]{};

        str = removeBrackets(str);
        return splitIntoChunks(str);
    }

    private static String[] splitIntoChunks(String str) {
        return str.split(", ");
    }

    private static String removeBrackets(String str) {
        str = str.substring(1, str.length() - 1);
        return str;
    }

    private static Process startPythonScript(String... commandLineInputs) throws IOException {
        ProcessBuilder searchProcessBuilder = new ProcessBuilder(commandLineInputs);
        return searchProcessBuilder.start();
    }
}
