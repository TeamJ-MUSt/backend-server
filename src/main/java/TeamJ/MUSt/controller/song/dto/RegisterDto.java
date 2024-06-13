package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private Long memberId;
    private Long songId;
    private String bugsId;

    public RegisterDto(Long memberId, Long songId, String bugsId) {
        this.memberId = memberId;
        this.songId = songId;
        this.bugsId = bugsId;
    }

    public RegisterDto() {
    }
}
