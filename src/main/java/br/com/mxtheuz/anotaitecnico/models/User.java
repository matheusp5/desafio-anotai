package br.com.mxtheuz.anotaitecnico.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private String password;
}
