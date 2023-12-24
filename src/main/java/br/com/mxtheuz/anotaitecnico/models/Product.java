package br.com.mxtheuz.anotaitecnico.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_products")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private float price;
    @ManyToOne
    private Category category;
    @ManyToOne
    private User owner;
}
