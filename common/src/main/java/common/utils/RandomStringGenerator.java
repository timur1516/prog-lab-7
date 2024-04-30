package common.utils;

import java.security.SecureRandom;

public class RandomStringGenerator {
    private final SecureRandom randomizer;

    public RandomStringGenerator() {
        this.randomizer = new SecureRandom();
    }

    public String generate() {
        return generate(this.randomizer.nextInt(25, 75 + 1));
    }

    public String generate(Integer seqLen) {
        return this.randomizer
                .ints(seqLen, 0x21, 0x7F)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
