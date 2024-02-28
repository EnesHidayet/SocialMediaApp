package org.enes.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.enes.utility.enums.EStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class UserProfile extends BaseEntity implements Serializable {

    @Id
    private String id;

    private Long authId;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String address;
    private String about;
    private List<String> followList;
    private int followers;
    private int following;

    @Builder.Default
    private EStatus status=EStatus.PENDING;
}
