package com.github.t1.check;

import com.github.t1.check.CheckResult.CheckResultBuilder;

import java.util.function.Supplier;

public interface Check extends Supplier<CheckResult> {
    default CheckResultBuilder type(String type) { return CheckResult.builder().type(type); }

    default CheckResultBuilder status(Status status) { return type(getType()).status(status); }

    default String getType() { return getClass().getName(); }
}
