package com.github.t1.check;

import lombok.*;

import java.util.*;

import static com.github.t1.check.Status.*;
import static java.util.Collections.*;

@Data
public class CheckResponse {
    private final Summary summary;
    private final List<CheckResult> checks;

    public CheckResponse(List<CheckResult> checks) {
        this.checks = checks;
        this.summary = new Summary();
    }

    @Value
    public class Summary {
        Status status;
        private final Map<Status, Integer> counters;

        private Summary() {
            this.status = maxStatus();
            Map<Status, Integer> counters = Status.mapOf(0, 0, 0, 0);
            checks.stream().map(CheckResult::getStatus).forEach(status
                    -> counters.compute(status, (key, value) -> value + 1));
            this.counters = unmodifiableMap(counters);
        }

        private Status maxStatus() {
            return checks.stream()
                         .map(CheckResult::getStatus)
                         .map(this::summaryStatus)
                         .max(Status::compareTo)
                         .orElse(OK);
        }

        private Status summaryStatus(Status status) {
            switch (status) {
            case UNKNOWN:
                return OK;
            default:
                return status;
            }
        }
    }
}
