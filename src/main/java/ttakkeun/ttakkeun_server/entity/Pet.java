package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Pet extends BaseEntity {

    @Id
    @Column(name = "pet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private String petName;

    private String petImageUrl;

    private String petVariety;

    private Date birth;

    @Enumerated(EnumType.STRING)
    private Neutralization neutralization;

    @Enumerated(EnumType.STRING)
    private PetType petType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<Todo> TodoList = new ArrayList<>();

    @OneToMany(mappedBy = "petId", cascade = CascadeType.ALL)
    private List<Record> RecordList = new ArrayList<>();


}
