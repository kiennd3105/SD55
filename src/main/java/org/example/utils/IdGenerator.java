package org.example.utils;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {}

    public static String generate8Hex() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
