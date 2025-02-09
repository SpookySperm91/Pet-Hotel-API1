package john.api1.application.components;

import java.util.concurrent.ThreadLocalRandom;

public class IDGenerator {
    public static int generate6DigitId() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }
}
