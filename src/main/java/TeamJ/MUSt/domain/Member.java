package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name")
    private String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<MemberSong> memberSongs = new ArrayList<>();

    public Member(String username) {
        this.username = username;
    }

    public Member() {

    }
}
