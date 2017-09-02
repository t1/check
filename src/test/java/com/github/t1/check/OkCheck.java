package com.github.t1.check;

import static com.github.t1.check.Status.OK;

class OkCheck implements Check {
    @Override public CheckResult get() { return status(OK).comment("foo").build(); }
}
