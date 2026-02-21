package com.nursingapp.data

/**
 * Represents a single activity suggestion for nursing-home residents.
 *
 * @param id               Unique identifier.
 * @param name             Short display name.
 * @param description      Brief description of the activity.
 * @param duration         Typical time range (e.g. "30–45 min").
 * @param mobilityRequired Physical effort level required.
 * @param supplies         List of materials / items needed.
 * @param category         High-level category for filtering.
 */
data class ActivityItem(
    val id: Int,
    val name: String,
    val description: String,
    val duration: String,
    val mobilityRequired: MobilityLevel,
    val supplies: List<String>,
    val category: ActivityCategory,
    val isCustom: Boolean = false
)

enum class MobilityLevel(val displayName: String, val emoji: String) {
    SEATED("Seated", "🪑"),
    LIGHT("Light Movement", "🚶"),
    MODERATE("Moderate", "🏃"),
}

enum class ActivityCategory(val displayName: String) {
    ART_CRAFTS("Art & Crafts"),
    BAKING_COOKING("Baking & Cooking"),
    MUSIC("Music"),
    GAMES("Games"),
    EXERCISE("Exercise"),
}

val allActivityItems: List<ActivityItem> = listOf(
    // ── Art & Crafts ────────────────────────────────────────────────────────
    ActivityItem(
        id = 1,
        name = "Watercolor Painting",
        description = "Create simple, relaxing watercolor paintings of flowers, landscapes, or abstract patterns. Great for self-expression and fine motor skills.",
        duration = "45–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Watercolor paints", "Watercolor paper", "Paintbrushes (various sizes)", "Water cups", "Paper towels", "Aprons"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 2,
        name = "Paper Flower Making",
        description = "Fold and cut colorful construction paper into beautiful flowers. Finished pieces can decorate the common room or be given as gifts.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Construction paper (assorted colors)", "Scissors", "Glue sticks", "Pipe cleaners (for stems)", "Markers"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 3,
        name = "Greeting Card Making",
        description = "Design personalized cards for birthdays, holidays, or just because. Residents can send them to family and friends.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Blank card stock", "Stickers and stamps", "Colored pencils & markers", "Glue", "Scissors", "Envelopes"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 4,
        name = "Adult Coloring Pages",
        description = "Color intricate adult coloring pages featuring nature scenes, mandalas, or vintage illustrations. Promotes calm focus and creativity.",
        duration = "20–40 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Printed coloring pages", "Colored pencils", "Fine-tip markers", "Pencil sharpeners"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 5,
        name = "Simple Knitting / Crocheting",
        description = "Knit or crochet simple scarves, dishcloths, or squares. Finished items can be donated to local shelters.",
        duration = "45–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Yarn (bulky weight is easiest)", "Knitting needles or crochet hooks", "Scissors", "Pattern cards (optional)"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 6,
        name = "Collage Making",
        description = "Cut out images from old magazines to create themed collages – gardens, travel, favorite foods, or memories.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Old magazines and catalogues", "Scissors", "Glue sticks", "Poster board or cardstock", "Markers"),
        category = ActivityCategory.ART_CRAFTS,
    ),

    // ── Baking & Cooking ────────────────────────────────────────────────────
    ActivityItem(
        id = 7,
        name = "No-Bake Chocolate Oatmeal Cookies",
        description = "Make delicious chocolate oatmeal cookies on the stovetop – no oven needed! Everyone leaves with a sweet treat.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Rolled oats (3 cups)", "Sugar (2 cups)", "Butter (½ cup)", "Cocoa powder (¼ cup)", "Milk (½ cup)", "Vanilla extract", "Wax paper", "Mixing bowls", "Saucepan"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 8,
        name = "Cookie Decorating",
        description = "Decorate pre-baked sugar cookies with colorful frosting and sprinkles. A fun and tasty creative outlet for all skill levels.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Pre-baked sugar cookies", "Royal icing (various colors)", "Sprinkles & edible decorations", "Piping bags or small zip-lock bags", "Napkins"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 9,
        name = "Fresh Fruit Salad",
        description = "Work together to wash, cut, and combine seasonal fruits into a colorful, healthy salad. Taste-test along the way!",
        duration = "30–40 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Assorted fresh fruit (berries, melon, grapes, etc.)", "Plastic cutting boards", "Plastic or butter knives", "Large serving bowl", "Small bowls & forks", "Optional: honey or mint for dressing"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 10,
        name = "Homemade Lemonade",
        description = "Squeeze fresh lemons, mix with simple syrup, and pour over ice for a refreshing batch of classic lemonade.",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.LIGHT,
        supplies = listOf("Fresh lemons (6–8)", "Citrus juicer", "Sugar (for simple syrup)", "Water", "Pitcher", "Cups", "Ice"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 11,
        name = "Smoothie Bar",
        description = "Blend a variety of colorful, nutritious smoothies. Residents can choose their own fruit and flavor combinations.",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Fresh or frozen fruit (bananas, berries, mango)", "Yogurt or milk", "Blender", "Cups with lids and straws", "Optional toppings: granola, honey"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 12,
        name = "Bread Baking",
        description = "Bake a loaf of simple white or cinnamon-raisin bread. Enjoy warm slices together with butter — the aroma is half the fun!",
        duration = "90–120 min (includes rising time)",
        mobilityRequired = MobilityLevel.LIGHT,
        supplies = listOf("Bread flour", "Active dry yeast", "Salt & sugar", "Butter or oil", "Warm water", "Mixing bowls", "Loaf pan", "Kitchen towel", "Oven mitts"),
        category = ActivityCategory.BAKING_COOKING,
    ),

    // ── Music ────────────────────────────────────────────────────────────────
    ActivityItem(
        id = 13,
        name = "Group Sing-Along",
        description = "Lead a group sing-along of classic favorites from the 1940s–70s. Improves mood and encourages social connection.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Printed song lyric sheets", "Bluetooth speaker or CD player", "Song playlist (classic standards, folk, country)", "Optional: tambourines or shakers"),
        category = ActivityCategory.MUSIC,
    ),
    ActivityItem(
        id = 14,
        name = "Name That Tune",
        description = "Play the first few seconds of classic songs and see who can name the song and artist first. Great for memory and friendly competition.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Device with music streaming or pre-downloaded songs", "Bluetooth speaker", "Score sheet & pens", "Small prizes (optional)"),
        category = ActivityCategory.MUSIC,
    ),
    ActivityItem(
        id = 15,
        name = "Rhythm Instruments Circle",
        description = "Pass out simple percussion instruments and play along to classic songs together. No musical experience required!",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Shakers / maracas", "Tambourines", "Hand drums", "Clapping sticks", "Music player & speaker", "Song list"),
        category = ActivityCategory.MUSIC,
    ),

    // ── Games ────────────────────────────────────────────────────────────────
    ActivityItem(
        id = 16,
        name = "Bingo",
        description = "A classic favorite! Play themed bingo – seasonal themes, nature, food, or travel – to keep things fresh and exciting.",
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Bingo cards (printed or purchased)", "Bingo markers or chips", "Caller card", "Prize bag (candy, small items)", "Microphone or loud voice!"),
        category = ActivityCategory.GAMES,
    ),
    ActivityItem(
        id = 17,
        name = "Trivia Challenge",
        description = "Host a fun themed trivia game with teams. Topics can include history, music, pop culture, or local knowledge.",
        duration = "45–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Trivia question cards or printed sheets", "Score board / whiteboard", "Pens & paper for teams", "Small prizes for winners"),
        category = ActivityCategory.GAMES,
    ),
    ActivityItem(
        id = 18,
        name = "Card Games",
        description = "Play classic card games in small groups – Rummy, Go Fish, Crazy Eights, or Solitaire. Promotes strategy and social interaction.",
        duration = "30–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Standard playing cards (multiple decks)", "Score pads & pens", "Card holders for easier holding (optional)"),
        category = ActivityCategory.GAMES,
    ),
    ActivityItem(
        id = 19,
        name = "Word Search & Crosswords",
        description = "Complete individually or work together on large-print word puzzles. Ideal for cognitive engagement at any pace.",
        duration = "20–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Large-print word search or crossword books", "Pens and pencils", "Magnifying glasses if needed"),
        category = ActivityCategory.GAMES,
    ),
    ActivityItem(
        id = 20,
        name = "Jigsaw Puzzle",
        description = "Work together on a jigsaw puzzle displayed on a communal table. Pick themes residents enjoy – landmarks, nature, vintage art.",
        duration = "Ongoing (30–60 min sessions)",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Jigsaw puzzle (300–500 pieces recommended)", "Large flat table", "Puzzle mat or board (to save progress)"),
        category = ActivityCategory.GAMES,
    ),

    // ── Exercise ─────────────────────────────────────────────────────────────
    ActivityItem(
        id = 21,
        name = "Chair Yoga",
        description = "Gentle seated yoga stretches and breathing exercises to improve flexibility and reduce stress. Suitable for all mobility levels.",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Sturdy chairs without wheels", "Non-slip floor mats (optional)", "Calming background music", "Printed or projected pose guide"),
        category = ActivityCategory.EXERCISE,
    ),
    ActivityItem(
        id = 22,
        name = "Balloon Volleyball",
        description = "Keep a balloon in the air using hands, paddles, or a pool noodle. Fun, low-impact, and surprisingly competitive!",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Balloons (multiple, in case of pops)", "Open seating area in a circle", "Optional: pool noodles or paddles", "Scoreboard"),
        category = ActivityCategory.EXERCISE,
    ),
    ActivityItem(
        id = 23,
        name = "Gentle Walking Club",
        description = "A supervised indoor or outdoor walking group at a comfortable pace. A wonderful way to enjoy fresh air and community.",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.LIGHT,
        supplies = listOf("Comfortable walking shoes", "Water bottles", "Walking route map or planned path", "First aid kit nearby", "Staff supervision"),
        category = ActivityCategory.EXERCISE,
    ),
    ActivityItem(
        id = 24,
        name = "Seated Stretching Circle",
        description = "A guided group stretching session for arms, neck, and legs — all done from a chair. Helps with circulation and morning stiffness.",
        duration = "15–20 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Sturdy chairs without wheels", "Relaxing music", "Printed stretch guide for staff reference"),
        category = ActivityCategory.EXERCISE,
    ),
    ActivityItem(
        id = 25,
        name = "Bean Bag Toss",
        description = "Toss bean bags into targets or numbered buckets for a friendly competition. Improves hand-eye coordination and is endlessly adaptable.",
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.LIGHT,
        supplies = listOf("Bean bags (6–10)", "Target board or numbered buckets", "Score sheet & pen", "Tape for throwing line"),
        category = ActivityCategory.EXERCISE,
    ),
)
