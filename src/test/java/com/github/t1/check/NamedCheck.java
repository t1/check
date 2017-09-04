package com.github.t1.check;

import static com.github.t1.check.Status.*;

class NamedCheck implements Check {
    @Override public CheckResult get() { return status(OK).comment("bar").build(); }

    @Override public String getType() { return "bar-name"; }
}
