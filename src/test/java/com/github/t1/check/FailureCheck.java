package com.github.t1.check;

import static com.github.t1.check.Status.FAILURE;

class FailureCheck implements Check {
    @Override public CheckResult get() { return status(FAILURE).comment("boo").build(); }
}
