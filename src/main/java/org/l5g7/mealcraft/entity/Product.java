package org.l5g7.mealcraft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private int id;

    private String name;

    private String imageUrl;

    private Unit defaultUnit;

    private User owner;


}
