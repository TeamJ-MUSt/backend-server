package TeamJ.MUSt.controller;

import TeamJ.MUSt.AllFieldWordInfo;
import TeamJ.MUSt.WordExtractor;
import TeamJ.MUSt.WordInfo;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
public class WordController {
    /*private final WordRepository wordRepository;
    private final WordExtractor wordExtractor;
    private final SongRepository songRepository;
    @GetMapping("/words")
    public List<AllFieldWordInfo> allWord() throws IOException {
        List<AllFieldWordInfo> result = new ArrayList<>();
        for (long i = 1; i <= 6; i++){
            List<AllFieldWordInfo> infos = wordExtractor.extractWordsV2(i);
            for (AllFieldWordInfo info : infos) {
                boolean flag = true;
                for (AllFieldWordInfo saved : result) {
                    if(info.getLemma().equals(saved.getLemma())){
                        for(String mean : info.getMeaning()){
                            boolean check = true;
                            for(String savedMean : saved.getMeaning()){
                                if(mean.equals(savedMean)){
                                    check = false;
                                    break;
                                }
                            }
                            if (check)
                                saved.getMeaning().add(mean);
                        }
                        flag = false;
                        break;
                    }
                }
                if (flag)
                    result.add(info);
            }
            System.out.println("하나 끝");
        }
        System.out.println(result.size());
        return result;
    }*/
}
