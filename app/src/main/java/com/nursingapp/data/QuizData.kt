package com.nursingapp.data

/**
 * Represents a single quiz question (trivia or guess-the-song).
 *
 * @param id       Unique identifier.
 * @param question The question / song lyric prompt shown face-up.
 * @param answer   The answer revealed when the card is tapped.
 * @param hint     Optional extra hint shown below the question.
 * @param category Whether this is a [QuizCategory.TRIVIA] or [QuizCategory.GUESS_THE_SONG] card.
 */
data class QuizItem(
    val id: Int,
    val question: String,
    val answer: String,
    val hint: String = "",
    val category: QuizCategory,
)

enum class QuizCategory(val displayName: String) {
    TRIVIA("Trivia"),
    GUESS_THE_SONG("Guess the Song"),
}

val allQuizItems: List<QuizItem> = listOf(
    // ── Trivia ──────────────────────────────────────────────────────────────
    QuizItem(
        id = 1,
        question = "What year did World War II end?",
        answer = "1945",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 2,
        question = "Who was the first President of the United States?",
        answer = "George Washington",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 3,
        question = "What year did humans first land on the Moon?",
        answer = "1969",
        hint = "Neil Armstrong made history that July.",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 4,
        question = "Who painted the Mona Lisa?",
        answer = "Leonardo da Vinci",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 5,
        question = "What is the capital city of France?",
        answer = "Paris",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 6,
        question = "What ship famously sank on its maiden voyage in 1912?",
        answer = "The Titanic",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 7,
        question = "How many stripes are on the American flag?",
        answer = "13 stripes (7 red, 6 white)",
        hint = "They represent the original colonies.",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 8,
        question = "Which country gifted the Statue of Liberty to the United States?",
        answer = "France",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 9,
        question = "What is the world's largest ocean?",
        answer = "The Pacific Ocean",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 10,
        question = "Who wrote the play 'Romeo and Juliet'?",
        answer = "William Shakespeare",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 11,
        question = "In what year did the Berlin Wall fall?",
        answer = "1989",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 12,
        question = "Who invented the telephone?",
        answer = "Alexander Graham Bell",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 13,
        question = "What is the longest river in the world?",
        answer = "The Nile River",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 14,
        question = "How many days are in a leap year?",
        answer = "366 days",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 15,
        question = "What sport is played at the Wimbledon Championship?",
        answer = "Tennis",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 16,
        question = "What was the first animated feature film?",
        answer = "Snow White and the Seven Dwarfs (1937)",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 17,
        question = "What is the smallest planet in our solar system?",
        answer = "Mercury",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 18,
        question = "Who was known as 'The King of Rock and Roll'?",
        answer = "Elvis Presley",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 19,
        question = "What is the capital of Australia?",
        answer = "Canberra",
        hint = "Not Sydney or Melbourne!",
        category = QuizCategory.TRIVIA,
    ),
    QuizItem(
        id = 20,
        question = "Which planet is known as the Red Planet?",
        answer = "Mars",
        category = QuizCategory.TRIVIA,
    ),

    // ── Guess the Song ──────────────────────────────────────────────────────
    QuizItem(
        id = 21,
        question = "♪ \"You ain't nothin' but a hound dog, cryin' all the time…\"",
        answer = "Hound Dog – Elvis Presley (1956)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 22,
        question = "♪ \"Somewhere over the rainbow, way up high…\"",
        answer = "Over the Rainbow – Judy Garland (1939)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 23,
        question = "♪ \"You are my sunshine, my only sunshine…\"",
        answer = "You Are My Sunshine – Jimmie Davis (1940)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 24,
        question = "♪ \"When the moon hits your eye like a big pizza pie, that's amore…\"",
        answer = "That's Amore – Dean Martin (1953)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 25,
        question = "♪ \"What a wonderful world…\"",
        answer = "What a Wonderful World – Louis Armstrong (1967)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 26,
        question = "♪ \"Fly me to the moon, let me play among the stars…\"",
        answer = "Fly Me to the Moon – Frank Sinatra (1964)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 27,
        question = "♪ \"My girl, talkin' 'bout my girl…\"",
        answer = "My Girl – The Temptations (1964)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 28,
        question = "♪ \"Oh Danny Boy, the pipes, the pipes are calling…\"",
        answer = "Danny Boy (traditional Irish ballad)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 29,
        question = "♪ \"Rock around the clock tonight, rock around the clock…\"",
        answer = "Rock Around the Clock – Bill Haley & His Comets (1954)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 30,
        question = "♪ \"Take me out to the ball game, take me out with the crowd…\"",
        answer = "Take Me Out to the Ball Game (1908 – traditional)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 31,
        question = "♪ \"This land is your land, this land is my land…\"",
        answer = "This Land Is Your Land – Woody Guthrie (1944)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 32,
        question = "♪ \"I've been working on the railroad, all the live-long day…\"",
        answer = "I've Been Working on the Railroad (traditional American folk song)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 33,
        question = "♪ \"Oh, give me a home where the buffalo roam…\"",
        answer = "Home on the Range (Kansas state song, 1872)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 34,
        question = "♪ \"You make me feel so young, you make me feel like spring has sprung…\"",
        answer = "You Make Me Feel So Young – Frank Sinatra (1956)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
    QuizItem(
        id = 35,
        question = "♪ \"Edelweiss, edelweiss, every morning you greet me…\"",
        answer = "Edelweiss – from The Sound of Music (1959)",
        category = QuizCategory.GUESS_THE_SONG,
    ),
)
