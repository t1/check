package com.github.t1.check;

import org.junit.Test;

import javax.ws.rs.ext.MessageBodyWriter;
import java.io.*;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.*;
import static javax.ws.rs.core.MediaType.*;
import static org.assertj.core.api.Assertions.*;

public class CheckResponseWriterTest {
    private static final URL TXT_FILE = CheckResponseWriterTest.class.getResource("check-response.txt");
    private static final URL HTML_FILE = CheckResponseWriterTest.class.getResource("check-response.html");

    private static final List<CheckResult> CHECKS = asList(
            new OkCheck().get(),
            new WarningCheck().get(),
            new UnknownCheck().get(),
            new OkCheck().get(),
            new UnknownCheck().get(),
            new OkCheck().get());

    private CheckResponseTextPlainMessageBodyWriter txtWriter = new CheckResponseTextPlainMessageBodyWriter();
    private CheckResponseHtmlMessageBodyWriter htmlWriter = new CheckResponseHtmlMessageBodyWriter();

    private String writeTo(MessageBodyWriter<CheckResponse> writer) throws IOException {
        CheckResponse response = new CheckResponse(CHECKS);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        writer.writeTo(response, null, null, null, TEXT_HTML_TYPE, null, stream);
        return stream.toString("UTF-8");
    }


    @Test
    public void shouldNotWriteStringAsText() throws Exception {
        assertThat(txtWriter.isWriteable(String.class, null, null, TEXT_HTML_TYPE)).isFalse();
    }

    @Test
    public void shouldGetTextSizeMinusOne() throws Exception {
        assertThat(txtWriter.getSize(null, null, null, null, TEXT_HTML_TYPE)).isEqualTo(-1);
    }

    @Test
    public void shouldGenerateText() throws Exception {
        String text = writeTo(txtWriter);

        assertThat(text).isEqualTo(contentOf(TXT_FILE));
    }


    @Test
    public void shouldNotWriteStringAsHtml() throws Exception {
        assertThat(htmlWriter.isWriteable(String.class, null, null, TEXT_HTML_TYPE)).isFalse();
    }

    @Test
    public void shouldGetHtmlSizeMinusOne() throws Exception {
        assertThat(htmlWriter.getSize(null, null, null, null, TEXT_HTML_TYPE)).isEqualTo(-1);
    }

    @Test
    public void shouldGenerateHtml() throws Exception {
        String html = writeTo(htmlWriter);

        assertThat(html).isEqualTo(contentOf(HTML_FILE));
    }
}
