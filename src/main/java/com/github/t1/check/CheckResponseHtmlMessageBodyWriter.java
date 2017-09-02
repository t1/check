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
@Produces(TEXT_HTML)
public class CheckResponseHtmlMessageBodyWriter implements MessageBodyWriter<CheckResponse> {
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
        out.write(new CheckResponseHtml(response).toString());
        out.flush();
    }

    @RequiredArgsConstructor
    private class CheckResponseHtml {
        private final CheckResponse response;

        @Override public String toString() {
            return ""
                    + "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<head>\n"
                    + head()
                    + "</head>\n"
                    + "<body>\n"
                    + summaryTable()
                    + "\n"
                    + "<br>\n"
                    + "\n"
                    + checksTable()
                    + "\n"
                    + "</body>\n"
                    + "</html>\n";
        }

        private String head() {
            return "    <meta charset=\"UTF-8\">\n"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                    + "    <title>System Checks</title>\n"
                    + "    <style>\n"
                    + "        table, tr, td {\n"
                    + "            border: 1pt solid;\n"
                    + "            padding: 3pt;\n"
                    + "        }\n"
                    + "    </style>\n";
        }

        private String summaryTable() {
            return "<table>\n"
                    + "    <tbody>\n"
                    + "    <tr>\n"
                    + "        <th>Summary</th>\n"
                    + "        <td>" + response.getSummary().getStatus() + "</td>\n"
                    + "    </tr>\n"
                    + Stream.of(Status.values()).map(this::summary).collect(joining())
                    + "    </tbody>\n"
                    + "</table>\n";
        }

        private String summary(Status status) {
            return ""
                    + "    <tr>\n"
                    + "        <th>" + status + "</th>\n"
                    + "        <td>" + response.getSummary().getCounters().get(status) + "</td>\n"
                    + "    </tr>\n";
        }

        private String checksTable() {
            return "<table>\n"
                    + "    <tbody>\n"
                    + "    <tr>\n"
                    + "        <th>Check</th>\n"
                    + "        <th>Status</th>\n"
                    + "        <th>Comment</th>\n"
                    + "    </tr>\n"
                    + response.getChecks().stream().map(this::check).collect(joining())
                    + "    </tbody>\n"
                    + "</table>\n";
        }

        private String check(CheckResult check) {
            return "    <tr>\n"
                    + "        <td>" + check.getType().getName() + "</td>\n"
                    + "        <td>" + check.getStatus() + "</td>\n"
                    + "        <td>" + check.getComment() + "</td>\n"
                    + "    </tr>\n";
        }
    }
}
