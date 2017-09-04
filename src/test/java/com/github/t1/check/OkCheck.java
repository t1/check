package com.github.t1.check;

import lombok.RequiredArgsConstructor;

import static com.github.t1.check.Status.*;

@RequiredArgsConstructor
class OkCheck implements Check {
    private final String comment;

    public OkCheck() { this("foo"); }

    @Override public CheckResult get() { return status(OK).comment(comment).build(); }
}
