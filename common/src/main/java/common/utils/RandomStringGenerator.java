package common.utils;

import java.security.SecureRandom;

/**
 * Class to generate strings with random symbols
 */
public class RandomStringGenerator {
    /**
     * Randomizer which is used to generate random numbers
     */
    private final SecureRandom randomizer;

    public RandomStringGenerator() {
        this.randomizer = new SecureRandom();
    }

    /**
     * Method to generate string of random symbols with random length in range from 25 to 75
     * @return Generated string
     */
    public String generate() {
        return generate(this.randomizer.nextInt(25, 75 + 1));
    }

    /**
     * Method to generate string of random symbols with given length
     * @param seqLen Length of string
     * @return Generated string
     */
    public String generate(Integer seqLen) {
        return this.randomizer
                .ints(seqLen, 0x21, 0x7F)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
