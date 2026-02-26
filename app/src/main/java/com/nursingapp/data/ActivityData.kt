package com.nursingapp.data


data class ActivityItem(
    val id: Int,
    val name: String,
    val description: String,
    val instructions: List<String>, // Step-by-step guide
    val benefits: List<String>,     // Clinical/social purpose
    val duration: String,
    val mobilityRequired: MobilityLevel,
    val supplies: List<String>,
    val category: ActivityCategory,
    val isCustom: Boolean = false,
    val scheduledDate: String? = null
)

data class SpecialDay(
    val name: String,
    val date: String, // Format "MM-dd"
    val type: String
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
        description = "Create simple, relaxing watercolor paintings of flowers or landscapes.",
        instructions = listOf(
            "Set up water cups and distribute watercolor paper.",
            "Demonstrate how to wet the brush and lift color from the palette.",
            "Encourage free-form shapes or provide simple nature-themed templates.",
            "Help residents sign their artwork once dry."
        ),
        benefits = listOf("Fine motor coordination", "Creative self-expression", "Anxiety reduction"),
        duration = "45–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Watercolor paints", "Paper", "Brushes", "Water cups", "Paper towels"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 2,
        name = "Paper Flower Making",
        description = "Fold and cut colorful construction paper into beautiful flowers.",
        instructions = listOf(
            "Pre-cut basic petal shapes for those with limited dexterity.",
            "Show how to layer petals and glue them to a central circle.",
            "Attach pipe cleaners to the back for stems.",
            "Arrange the finished flowers in a communal 'vase' or basket."
        ),
        benefits = listOf("Hand-eye coordination", "Social pride", "Color recognition"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Construction paper", "Scissors", "Glue sticks", "Pipe cleaners"),
        category = ActivityCategory.ART_CRAFTS,
    ),
    ActivityItem(
        id = 3,
        name = "Greeting Card Making",
        description = "Design personalized cards for birthdays, holidays, or family.",
        instructions = listOf(
            "Fold cardstock in half and provide various decorative materials.",
            "Offer stamps and stickers for easy design elements.",
            "Assist in writing short, meaningful messages inside.",
            "Help address envelopes for residents who wish to mail them."
        ),
        benefits = listOf("Maintains social connections", "Cognitive focus", "Manual dexterity"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Cardstock", "Stickers", "Stamps", "Pens", "Envelopes"),
        category = ActivityCategory.ART_CRAFTS,
    ),

    // ── Baking & Cooking ────────────────────────────────────────────────────
    ActivityItem(
        id = 7,
        name = "No-Bake Oatmeal Cookies",
        description = "Make delicious chocolate oatmeal cookies on the stovetop.",
        instructions = listOf(
            "Combine sugar, butter, cocoa, and milk in a saucepan.",
            "Bring to a boil for exactly one minute, then remove from heat.",
            "Stir in oats and vanilla until well coated.",
            "Drop spoonfuls onto wax paper and let cool until firm."
        ),
        benefits = listOf("Sensory stimulation (smell/taste)", "Sequential memory", "Social bonding"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Oats", "Sugar", "Butter", "Cocoa", "Milk", "Wax paper"),
        category = ActivityCategory.BAKING_COOKING,
    ),
    ActivityItem(
        id = 8,
        name = "Cookie Decorating",
        description = "Decorate pre-baked sugar cookies with frosting and sprinkles.",
        instructions = listOf(
            "Provide each resident with 2-3 pre-baked cookies.",
            "Set out bowls of different colored frosting and spoons.",
            "Demonstrate simple patterns or dot-work.",
            "Enjoy the cookies together with tea or coffee."
        ),
        benefits = listOf("Artistic expression", "Fine motor skills", "Social interaction"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Pre-baked cookies", "Frosting", "Sprinkles", "Plates"),
        category = ActivityCategory.BAKING_COOKING,
    ),

    // ── Music ────────────────────────────────────────────────────────────────
    ActivityItem(
        id = 13,
        name = "Group Sing-Along",
        description = "Lead a group sing-along of classic favorites from the 40s–70s.",
        instructions = listOf(
            "Distribute large-print lyric sheets to all participants.",
            "Play a familiar song at a moderate volume to start.",
            "Encourage clapping or tapping feet along to the rhythm.",
            "Take requests from the group for the final few songs."
        ),
        benefits = listOf("Respiratory health", "Mood elevation", "Long-term memory recall"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Lyric sheets", "Speaker", "Playlist"),
        category = ActivityCategory.MUSIC,
    ),
    ActivityItem(
        id = 14,
        name = "Name That Tune",
        description = "Identify classic songs and artists from short clips.",
        instructions = listOf(
            "Explain the rules: first person to raise a hand gets to guess.",
            "Play 5–10 seconds of a very popular song.",
            "Award points to individuals or teams for correct answers.",
            "Share a brief fact or memory about the artist between songs."
        ),
        benefits = listOf("Auditory processing", "Cognitive speed", "Competitive spirit"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Music player", "Bouton/Buzzer (optional)", "Scoreboard"),
        category = ActivityCategory.MUSIC,
    ),

    // ── Games ────────────────────────────────────────────────────────────────
    ActivityItem(
        id = 16,
        name = "Bingo",
        description = "A classic favorite game of chance and focus.",
        instructions = listOf(
            "Hand out cards and markers to everyone.",
            "Call out numbers slowly and clearly, repeating them twice.",
            "Walk around to assist those who may have missed a number.",
            "Verify the 'Bingo' and offer a small reward or praise."
        ),
        benefits = listOf("Listening skills", "Number recognition", "Social excitement"),
        duration = "30–45 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Bingo cards", "Markers", "Number cage or app"),
        category = ActivityCategory.GAMES,
    ),
    ActivityItem(
        id = 17,
        name = "Trivia Challenge",
        description = "Host a fun themed trivia game with teams.",
        instructions = listOf(
            "Divide the group into two or three small teams.",
            "Read questions clearly, focusing on topics like 'Nature' or 'History'.",
            "Allow teams 30 seconds to discuss their answer.",
            "Keep score on a board and celebrate all participants."
        ),
        benefits = listOf("Knowledge retention", "Teamwork", "Mental agility"),
        duration = "45–60 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Trivia cards", "Whiteboard", "Markers"),
        category = ActivityCategory.GAMES,
    ),

    // ── Exercise ─────────────────────────────────────────────────────────────
    ActivityItem(
        id = 21,
        name = "Chair Yoga",
        description = "Gentle seated yoga stretches and breathing exercises.",
        instructions = listOf(
            "Start with deep, rhythmic breathing for 2 minutes.",
            "Move to neck rolls and shoulder shrugs.",
            "Perform seated 'cat-cow' stretches for the spine.",
            "Finish with a gentle seated twist and final relaxation."
        ),
        benefits = listOf("Flexibility", "Stress reduction", "Core strength"),
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Sturdy chairs", "Calm music"),
        category = ActivityCategory.EXERCISE,
    ),
    ActivityItem(
        id = 22,
        name = "Balloon Volleyball",
        description = "Keep a balloon in the air using hands or pool noodles.",
        instructions = listOf(
            "Arrange chairs in a tight circle.",
            "Introduce one or two balloons into the center.",
            "Encourage residents to hit the balloon to keep it from the floor.",
            "Change directions or add a second balloon to increase the challenge."
        ),
        benefits = listOf("Upper body mobility", "Quick reflexes", "Laughter/Joy"),
        duration = "20–30 min",
        mobilityRequired = MobilityLevel.SEATED,
        supplies = listOf("Balloons", "Pool noodles (optional)"),
        category = ActivityCategory.EXERCISE,
    )
)