package com.github.t1.check;

class JavaLangErrorCheck implements Check {
    @Override public CheckResult get() { throw new Error("really bad"); }
}
