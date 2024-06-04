package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    private String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MemberSong> memberSongs = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MemberWord> memberWords = new ArrayList<>();

    @NotEmpty
    private String password;
    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Member() {

    }
}
