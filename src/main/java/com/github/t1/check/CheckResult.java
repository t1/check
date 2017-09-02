package com.github.t1.check;

import lombok.*;

@Value
@Builder
public class CheckResult {
    private String type;
    private Status status;
    private String comment;
}
