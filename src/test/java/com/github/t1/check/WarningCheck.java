package com.github.t1.check;

import static com.github.t1.check.Status.WARNING;

class WarningCheck implements Check {
    @Override public CheckResult get() { return status(WARNING).comment("bar").build(); }
}
