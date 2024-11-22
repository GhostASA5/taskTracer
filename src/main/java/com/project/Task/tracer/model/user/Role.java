package com.project.Task.tracer.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    private RoleType role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(role.toString());
    }

    public static Role from(RoleType type) {
        var role = new Role();
        role.setRole(type);
        return role;
    }
}
