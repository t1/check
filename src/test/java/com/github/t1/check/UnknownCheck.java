package com.github.t1.check;

import static com.github.t1.check.Status.UNKNOWN;

class UnknownCheck implements Check {
    @Override public CheckResult get() { return status(UNKNOWN).comment("baz").build(); }
}
