package com.github.t1.check;

import java.util.*;

public enum Status {
    OK, UNKNOWN, WARNING, FAILURE;


    public static Map<Status, Integer> mapOf(int ok, int unknown, int warning, int failure) {
        Map<Status, Integer> map = new EnumMap<>(Status.class);
        map.put(OK, ok);
        map.put(UNKNOWN, unknown);
        map.put(WARNING, warning);
        map.put(FAILURE, failure);
        return map;
    }
}
