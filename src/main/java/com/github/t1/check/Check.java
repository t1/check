package com.github.t1.check;

import com.github.t1.check.CheckResult.CheckResultBuilder;

import java.util.function.Supplier;

public interface Check extends Supplier<CheckResult> {
    default CheckResultBuilder status(Status status) { return CheckResult.of(getClass()).status(status); }
}
