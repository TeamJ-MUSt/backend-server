package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MemberSong {
    @Id @GeneratedValue
    @Column(name = "member_song_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    private Song song;

    public void createMemberSong(Member member, Song song){
        this.member = member;
        this.song = song;
        member.getMemberSongs().add(this);
        song.getMemberSongs().add(this);
    }
}
