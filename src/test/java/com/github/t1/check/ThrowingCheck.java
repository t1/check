package com.github.t1.check;

class ThrowingCheck implements Check {
    @Override public CheckResult get() { throw new RuntimeException("always fails"); }
}
