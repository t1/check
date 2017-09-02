package com.github.t1.check;

import lombok.RequiredArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static javax.ws.rs.core.MediaType.*;

@Provider
@Produces(TEXT_PLAIN)
public class CheckResponseTextPlainMessageBodyWriter implements MessageBodyWriter<CheckResponse> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CheckResponse.class.equals(type);
    }

    @Override
    public long getSize(CheckResponse s, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(CheckResponse response, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        @SuppressWarnings("resource") OutputStreamWriter out = new OutputStreamWriter(entityStream);
        out.write(new CheckResponsePlain(response).toString());
        out.flush();
    }

    @RequiredArgsConstructor
    private class CheckResponsePlain {
        private final CheckResponse response;

        @Override public String toString() { return summary() + checks(); }

        private String summary() {
            return "Summary " + response.getSummary().getStatus() + " -- "
                    + Stream.of(Status.values()).map(this::summary).collect(joining(", "))
                    + "\n";
        }

        private String summary(Status status) {
            return status + "=" + response.getSummary().getCounters().get(status);
        }

        private String checks() {
            return response.getChecks().stream().map(this::check).collect(joining());
        }

        private String check(CheckResult check) {
            return check.getType().getName() + " " + check.getStatus() + " -- " + check.getComment() + "\n";
        }
    }
}
