package TeamJ.MUSt;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberSong;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.exception.NoSearchResultException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class InitDb {
    static private String prefix = "C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\thumbnail\\";
    static private String suffix = ".jpg";

    private final InitService initService;

    @PostConstruct
    public void init() throws IOException, NoSearchResultException {
        initService.initDb();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;

        public void initDb() throws IOException, NoSearchResultException {
            Member[] members = new Member[2];
            members[0] = new Member("member1");
            members[1] = new Member("member2");

            for (Member member : members)
                em.persist(member);

            Song[] songs = new Song[7];
            songs[0] = createSampleSong("BETELGEUSE", "Yuuri");
            songs[1] = createSampleSong("nandemonaiya", "RADWIMPS");
            songs[2] = createSampleSong("lemon", "Yonezu kenshi");
            songs[3] = createSampleSong("Flamingo", "Yonezu kenshi");
            songs[4] = createSampleSong("Madou Ito", "Masaki Suda");
            songs[5] = createSampleSong("Leo", "Yuuri");
            songs[6] = createSampleSong("BETELGEUSE", "KSUKE");
            //Song song4 = new Song("Bling-Bang-Bang-Born", "Creepy Nuts", PythonExecutor.callBugsApi("Bling-Bang-Bang-Born", "Creepy Nuts").getLyrics()
            //, extractBytes("C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\thumbnail\\Bling-Bang-Bang-Born_Creepy Nuts.jpg"));

            for (Song song : songs)
                em.persist(song);

            for(int i = 0; i < songs.length; i++)
                new MemberSong().createMemberSong(members[0], songs[i]);

            for(int i = 2; i < songs.length; i++)
                new MemberSong().createMemberSong(members[1], songs[i]);

        }
    }

    private static Song createSampleSong(String title, String artist) throws IOException, NoSearchResultException {
        return new Song(title, artist, PythonExecutor.callBugsApi(title, artist).getLyrics()
                , extractBytes(prefix + title + "_" + artist + suffix));
    }

    public static byte[] extractBytes(String imagePath) throws IOException {
        return Files.readAllBytes(new File(imagePath).toPath());
    }

}
