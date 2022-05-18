package com.ezgroceries.shoppinglist.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CocktailResource {
    private UUID  id;
    private String name;
    private String glass;
    private String instructions;
    private String image;
    private Arrays ingredients;

    public CocktailResource(UUID id, String name, String glass, String instructions, String image, Arrays ingredients) {
        this.id = id;
        this.name = name;
        this.glass = glass;
        this.instructions = instructions;
        this.image = image;
        this.ingredients = ingredients;
    }
// standard getters and setters
}
