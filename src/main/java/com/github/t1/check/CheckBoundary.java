package com.github.t1.check;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.*;

@Path("/-system/check")
public class CheckBoundary {
    @Inject Instance<Check> checks;

    @GET public CheckResponse get() {
        return new CheckResponse(collect());
    }

    private List<CheckResult> collect() {
        List<CheckResult> checkResults = new ArrayList<>();
        checks.forEach(check -> {
            try {
                checkResults.add(check.get());
            } finally {
                checks.destroy(check);
            }
        });
        return checkResults;
    }
}
