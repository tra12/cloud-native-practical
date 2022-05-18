package com.ezgroceries.shoppinglist.dto;

import java.util.List;
import java.util.UUID;

public class CocktailResource {
    private UUID  id;
    private String name;
    private String glass;
    private String instructions;
    private String image;
    private List<String> ingredients;

    public CocktailResource(UUID id, String name, String glass, String instructions, String image, List ingredients) {
        this.id = id;
        this.name = name;
        this.glass = glass;
        this.instructions = instructions;
        this.image = image;
        this.ingredients = ingredients;
    }
// standard getters and setters
}
