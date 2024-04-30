package TeamJ.MUSt.controller.song.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SongSearch {
    private String title;
    private String artist;
    private Integer pageNum;
}
