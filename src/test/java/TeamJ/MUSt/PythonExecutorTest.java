package TeamJ.MUSt;

import TeamJ.MUSt.service.song.SongInfo;
import org.junit.jupiter.api.Test;

import java.util.List;


public class PythonExecutorTest {
    @Test
    public void 노래_검색() throws Exception {
        List<SongInfo> songInfos = BugsCrawler.callBugsApi("betelgeuse", "yuuri");
        for (SongInfo songInfo : songInfos)
            System.out.println(songInfo);
    }

    @Test
    public void 가사에_따옴표가_들어간_노래_검색() throws Exception {
        List<SongInfo> songInfos = BugsCrawler.callBugsApi("bling-bang-bang-born", "creepy nuts");
        for (SongInfo songInfo : songInfos)
            System.out.println(songInfo);
    }

    @Test
    public void 가수만_검색() throws Exception {
        List<SongInfo> songInfos = BugsCrawler.callBugsApi(null, "lil wayne");
        for (SongInfo songInfo : songInfos)
            System.out.println(songInfo);
    }

    @Test
    public void 노래만_검색() throws Exception {
        List<SongInfo> songInfos = BugsCrawler.callBugsApi("song cry", null);
        for (SongInfo songInfo : songInfos)
            System.out.println(songInfo);
    }
}