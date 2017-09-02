package com.github.t1.check;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;

public class CheckResponseHtmlMessageBodyWriterTest {
    private static final URL FILE = CheckResponseHtmlMessageBodyWriterTest.class.getResource("check-response.html");
    private CheckResponseHtmlMessageBodyWriter writer = new CheckResponseHtmlMessageBodyWriter();

    @Test
    public void shouldNotWriteCheck() throws Exception {
        assertThat(writer.isWriteable(String.class, null, null, TEXT_HTML_TYPE)).isFalse();
    }

    @Test
    public void shouldGetSizeMinusOne() throws Exception {
        assertThat(writer.getSize(null, null, null, null, TEXT_HTML_TYPE)).isEqualTo(-1);
    }

    @Test
    public void shouldGenerate() throws Exception {
        List<CheckResult> checks = asList(
                new OkCheck().get(),
                new WarningCheck().get(),
                new UnknownCheck().get(),
                new OkCheck().get(),
                new UnknownCheck().get(),
                new OkCheck().get());
        CheckResponse response = new CheckResponse(checks);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.writeTo(response, null, null, null, TEXT_HTML_TYPE, null, stream);
        String html = stream.toString("UTF-8");

        assertThat(html).isEqualTo(contentOf(FILE));
    }
}
