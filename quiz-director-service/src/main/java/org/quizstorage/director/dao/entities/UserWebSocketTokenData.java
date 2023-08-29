package org.quizstorage.director.dao.entities;

import lombok.*;
import org.quizstorage.director.security.QuizUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document("games")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class UserWebSocketTokenData {

    @Id
    private String userId;

    @Field("tokenId")
    private UUID tokenId;

    @Field("name")
    private String name;

    @Field("roles")
    private Set<String> roles;

    public UserWebSocketTokenData(QuizUser user, UUID tokenId) {
        this.userId = user.id();
        this.name = user.name();
        this.roles = new HashSet<>(user.roles());
        this.tokenId = tokenId;
    }

}
