package com.spring.securityPractice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userId;
    private String email;
    private String password;
    private String role;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "role")
//    private Set<String> roles;
//
//    public void setRoles(Set<String> roles) {
//        this.roles = roles;
//    }
//
//    public Set<String> getRoles() {
//        return this.roles;
//    }
}
