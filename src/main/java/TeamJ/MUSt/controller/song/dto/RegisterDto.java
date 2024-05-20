package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private Long memberId;
    private Long songId;


    public RegisterDto(Long memberId, Long songId) {
        this.memberId = memberId;
        this.songId = songId;
    }

    public RegisterDto() {
    }
}
