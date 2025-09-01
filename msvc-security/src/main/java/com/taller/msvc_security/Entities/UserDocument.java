package com.taller.msvc_security.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "user_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private Set<Role> authorities = new HashSet<>();

    public void addRole(Role role) {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        authorities.add(role);
    }

    public boolean hasRole(Role role) {
        return authorities != null && authorities.contains(role);
    }
}