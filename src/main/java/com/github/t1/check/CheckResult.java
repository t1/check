package com.github.t1.check;

import lombok.*;

@Value
@Builder
public class CheckResult {
    public static CheckResultBuilder of(Class<? extends Check> type) { return builder().type(type); }

    private Class<? extends Check> type;
    private Status status;
    private String comment;
}
