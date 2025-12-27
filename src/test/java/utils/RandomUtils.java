package utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    /**
     * Возвращает случайное положительное целое число в диапазоне [min, max]
     * включая обе границы.
     *
     * @param min нижняя граница (включительно)
     * @param max верхняя граница (включительно)
     * @return случайное Integer в заданном диапазоне
     */
    public static Integer getRandomInteger(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min не может быть больше max");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
