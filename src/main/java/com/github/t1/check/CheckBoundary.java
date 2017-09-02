package com.github.t1.check;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.github.t1.check.Status.*;
import static java.util.stream.Collectors.*;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.Status.OK;

@Path("/-system/check")
public class CheckBoundary {
    @Inject Instance<Check> checks;

    @GET public Response get() {
        CheckResponse checkResponse = new CheckResponse(collect());
        return Response.status(map(checkResponse.getSummary().getStatus())).entity(checkResponse).build();
    }

    private static Response.Status map(Status status) { return (status == FAILURE) ? INTERNAL_SERVER_ERROR : OK; }

    private List<CheckResult> collect() {
        return StreamSupport.stream(checks.spliterator(), false)
                            .map(check -> {
                                try {
                                    return check.get();
                                } finally {
                                    checks.destroy(check);
                                }
                            })
                            .collect(toList());
    }
}
