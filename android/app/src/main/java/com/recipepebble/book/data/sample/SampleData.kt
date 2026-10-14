package com.recipepebble.book.data.sample

import com.recipepebble.book.domain.model.Ingredient
import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.model.RecipeStep

object SampleData {

    const val SPRITE_HERB = "herb"
    const val SPRITE_BREAD = "bread"
    const val SPRITE_PEPPER = "pepper"
    const val SPRITE_CUPCAKE = "cupcake"

    val sprites: List<String> = listOf(SPRITE_HERB, SPRITE_BREAD, SPRITE_PEPPER, SPRITE_CUPCAKE)

    fun spriteFor(id: String): String {
        val index = (id.hashCode() and 0x7FFFFFFF) % sprites.size
        return sprites[index]
    }

    val recipes: List<Recipe> = listOf(
        Recipe(
            id = "seed-tomato-basil-soup",
            name = "Tomato Basil Soup",
            minutes = 35,
            favourite = true,
            spriteKey = SPRITE_HERB,
            tags = listOf("SOUP", "DINNER"),
            ingredients = listOf(
                Ingredient("Ripe tomatoes", "8 medium"),
                Ingredient("Sweet onion", "1 large"),
                Ingredient("Garlic cloves", "3"),
                Ingredient("Fresh basil", "1 handful"),
                Ingredient("Olive oil", "2 tbsp"),
                Ingredient("Vegetable stock", "600 ml"),
                Ingredient("Sea salt", "to taste")
            ),
            steps = listOf(
                RecipeStep("Halve the tomatoes and warm them in the oven with the oil until the edges darken."),
                RecipeStep("Soften the onion and garlic in a deep pan over a low flame."),
                RecipeStep("Add the tomatoes and the stock, then simmer for twenty minutes."),
                RecipeStep("Blend until smooth and finish with torn basil and salt.")
            )
        ),
        Recipe(
            id = "seed-lemon-herb-chicken",
            name = "Lemon Herb Chicken",
            minutes = 55,
            favourite = false,
            spriteKey = SPRITE_PEPPER,
            tags = listOf("DINNER", "QUICK"),
            ingredients = listOf(
                Ingredient("Chicken thighs", "6"),
                Ingredient("Lemon", "1 whole"),
                Ingredient("Thyme sprigs", "4"),
                Ingredient("Butter", "30 g"),
                Ingredient("New potatoes", "500 g"),
                Ingredient("Black pepper", "to taste")
            ),
            steps = listOf(
                RecipeStep("Rub the thighs with butter, lemon zest and pepper and leave them to rest."),
                RecipeStep("Halve the potatoes and scatter them around the meat in a wide dish."),
                RecipeStep("Bake until the skin crisps and the juices run clear."),
                RecipeStep("Squeeze the lemon over everything just before serving.")
            )
        ),
        Recipe(
            id = "seed-morning-oat-pancakes",
            name = "Morning Oat Pancakes",
            minutes = 20,
            favourite = true,
            spriteKey = SPRITE_BREAD,
            tags = listOf("BREAKFAST", "QUICK", "SWEET"),
            ingredients = listOf(
                Ingredient("Rolled oats", "120 g"),
                Ingredient("Milk", "250 ml"),
                Ingredient("Eggs", "2"),
                Ingredient("Baking powder", "1 tsp"),
                Ingredient("Honey", "1 tbsp"),
                Ingredient("Butter for the pan", "a knob")
            ),
            steps = listOf(
                RecipeStep("Blitz the oats with the milk and let the batter rest for five minutes."),
                RecipeStep("Whisk in the eggs, the baking powder and the honey."),
                RecipeStep("Cook small rounds in a buttered pan until bubbles break the surface."),
                RecipeStep("Serve warm with more honey and a spoon of thick yoghurt.")
            )
        ),
        Recipe(
            id = "seed-garlic-butter-pasta",
            name = "Garlic Butter Pasta",
            minutes = 18,
            favourite = false,
            spriteKey = SPRITE_HERB,
            tags = listOf("PASTA", "QUICK"),
            ingredients = listOf(
                Ingredient("Linguine", "300 g"),
                Ingredient("Butter", "60 g"),
                Ingredient("Garlic cloves", "4"),
                Ingredient("Parsley", "1 handful"),
                Ingredient("Parmesan", "40 g")
            ),
            steps = listOf(
                RecipeStep("Boil the linguine in well salted water until it still has a bite."),
                RecipeStep("Melt the butter with thin slices of garlic over a gentle heat."),
                RecipeStep("Toss the pasta through the butter with a splash of the cooking water."),
                RecipeStep("Finish with chopped parsley and a shower of parmesan.")
            )
        ),
        Recipe(
            id = "seed-roasted-pepper-salad",
            name = "Roasted Pepper Salad",
            minutes = 30,
            favourite = false,
            spriteKey = SPRITE_PEPPER,
            tags = listOf("SALAD", "DINNER"),
            ingredients = listOf(
                Ingredient("Red peppers", "4"),
                Ingredient("Feta", "150 g"),
                Ingredient("Red onion", "half"),
                Ingredient("Olive oil", "3 tbsp"),
                Ingredient("Red wine vinegar", "1 tbsp"),
                Ingredient("Oregano", "1 tsp")
            ),
            steps = listOf(
                RecipeStep("Blacken the peppers under a hot grill until the skins lift away."),
                RecipeStep("Rest them under a lid, then peel and tear them into wide ribbons."),
                RecipeStep("Dress with oil, vinegar and oregano while they are still warm."),
                RecipeStep("Crumble the feta over the top with slivers of raw onion.")
            )
        ),
        Recipe(
            id = "seed-honey-oat-cookies",
            name = "Honey Oat Cookies",
            minutes = 28,
            favourite = false,
            spriteKey = SPRITE_CUPCAKE,
            tags = listOf("BAKING", "SWEET"),
            ingredients = listOf(
                Ingredient("Rolled oats", "160 g"),
                Ingredient("Plain flour", "120 g"),
                Ingredient("Butter", "110 g"),
                Ingredient("Honey", "3 tbsp"),
                Ingredient("Brown sugar", "80 g"),
                Ingredient("Cinnamon", "1 tsp")
            ),
            steps = listOf(
                RecipeStep("Warm the butter with the honey until the two run together."),
                RecipeStep("Stir in the dry ingredients and leave the dough to firm up."),
                RecipeStep("Press walnut sized pieces onto a lined tray with plenty of room."),
                RecipeStep("Bake until the edges set, then cool them on the tray.")
            )
        ),
        Recipe(
            id = "seed-mushroom-risotto",
            name = "Mushroom Risotto",
            minutes = 45,
            favourite = false,
            spriteKey = SPRITE_HERB,
            tags = listOf("DINNER", "PASTA"),
            ingredients = listOf(
                Ingredient("Risotto rice", "280 g"),
                Ingredient("Chestnut mushrooms", "350 g"),
                Ingredient("Shallots", "2"),
                Ingredient("White wine", "120 ml"),
                Ingredient("Vegetable stock", "1 litre"),
                Ingredient("Butter", "40 g"),
                Ingredient("Parmesan", "50 g"),
                Ingredient("Thyme", "2 sprigs")
            ),
            steps = listOf(
                RecipeStep("Fry the mushrooms hard in a dry pan until they colour, then set them aside."),
                RecipeStep("Sweat the shallots, add the rice and let every grain turn glassy."),
                RecipeStep("Pour in the wine, then the warm stock one ladle at a time."),
                RecipeStep("Fold the mushrooms, butter and parmesan through at the end."),
                RecipeStep("Rest it for two minutes under a lid before serving.")
            )
        ),
        Recipe(
            id = "seed-spiced-apple-porridge",
            name = "Spiced Apple Porridge",
            minutes = 15,
            favourite = false,
            spriteKey = SPRITE_BREAD,
            tags = listOf("BREAKFAST", "QUICK", "SWEET"),
            ingredients = listOf(
                Ingredient("Rolled oats", "90 g"),
                Ingredient("Milk", "400 ml"),
                Ingredient("Apple", "1"),
                Ingredient("Cinnamon", "half tsp"),
                Ingredient("Maple syrup", "1 tbsp")
            ),
            steps = listOf(
                RecipeStep("Simmer the oats with the milk, stirring until the pan looks creamy."),
                RecipeStep("Grate the apple in near the end so it keeps a little texture."),
                RecipeStep("Season with cinnamon and sweeten with the syrup.")
            )
        )
    )

    val draftName: String = "Garden Herb Omelette"

    val draftIngredients: List<Ingredient> = listOf(
        Ingredient("Eggs", "3"),
        Ingredient("Butter", "15 g"),
        Ingredient("Chives", "1 small bunch")
    )

    val draftSteps: List<RecipeStep> = listOf(
        RecipeStep("Beat the eggs with a pinch of salt until the yolks disappear."),
        RecipeStep("Melt the butter in a small pan and pour the eggs in while it foams.")
    )

    val draftTags: List<String> = listOf("BREAKFAST", "QUICK")
}
