package kopo.poly.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "USER_INFO")
public class UserInfoEntity {

    @Id
    private String userSeq;

    @NonNull
    @Field(name = "USER_ID")
    private String userId;

    @NonNull
    @Field(name = "USER_NAME")
    private String userName;

    @NonNull
    @Field(name = "PASSWORD")
    private String password;

    @NonNull
    @Field(name = "EMAIL")
    private String email;

    @NonNull
    @Field(name = "ADDR1")
    private String addr1;

    @NonNull
    @Field(name = "ADDR2")
    private String addr2;

    @Field(name = "REG_ID")
    private String regId;

    @Field(name = "REG_DT")
    private String regDt;

    @Field(name = "CHG_ID")
    private String chgId;

    @Field(name = "CHG_DT")
    private String chgDt;

}
