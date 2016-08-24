package com.thomaskioko.jokeprovider;

import java.util.Random;

public class JokeProvider {

    private static String[] jokesArray = new String[]{
            "Computers make very fast, very accurate mistakes.",
            "Be nice to the nerds, for all you know they might be the next Bill Gates!",
            "CAPS LOCK – Preventing Login Since 1980.",
            "To err is human – and to blame it on a computer is even more so.",
            "The truth is out there. Anybody got the URL?",
            "Artificial intelligence usually beats real stupidity.",
            "The box said ‘Requires Windows Vista or better’. So I installed LINUX.",
            "Bugs come in through open Windows.",
            "Unix is user friendly. It’s just selective about who its friends are.",
            "I would love to change the world, but they won’t give me the source code.",
            "Hey! It compiles! Ship it!",
            "Computers are like air conditioners: they stop working when you open Windows.",
            "My attitude isn’t bad. It’s in beta",
            "Programmers are tools for converting caffeine into code.",
            "There are only 10 types of people in the world: those that understand binary and those that don’t."
    };

    /**
     * Helper method to get a random joke from the array
     *
     * @return {@link String} Joke from the array
     */
    public static String getRandomJoke() {
        int size = jokesArray.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return jokesArray[index];
    }
}
