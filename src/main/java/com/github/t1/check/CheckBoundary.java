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

@Path("/-system/checks")
public class CheckBoundary {
    @Inject Instance<Check> checks;

    @GET public Response get(@QueryParam("type") String type) {
        if (type == null) {
            CheckResponse checkResponse = new CheckResponse(collect());
            return Response.status(map(checkResponse.getSummary().getStatus())).entity(checkResponse).build();
        } else {
            CheckResult result = collect()
                    .stream()
                    .filter(check -> check.getType().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("no check found with type '" + type + "'"));
            return Response.status(map(result.getStatus())).entity(result).build();
        }
    }

    private List<CheckResult> collect() {
        return StreamSupport.stream(checks.spliterator(), false)
                            .map(check -> {
                                try {
                                    return check.get();
                                } catch (RuntimeException e) {
                                    return CheckResult.builder()
                                                      .type(check.getType())
                                                      .status(FAILURE)
                                                      .comment(e.getMessage())
                                                      .build();
                                } finally {
                                    checks.destroy(check);
                                }
                            })
                            .collect(toList());
    }

    private static Response.Status map(Status status) { return (status == FAILURE) ? INTERNAL_SERVER_ERROR : OK; }
}
