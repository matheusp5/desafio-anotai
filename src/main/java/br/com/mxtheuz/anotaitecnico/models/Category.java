package br.com.mxtheuz.anotaitecnico.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_categories")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    @ManyToOne
    private User owner;
}
