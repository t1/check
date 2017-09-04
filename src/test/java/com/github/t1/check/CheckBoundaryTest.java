package com.github.t1.check;

import com.github.t1.testtools.MockInstance;
import org.junit.*;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static com.github.t1.check.Status.*;
import static com.github.t1.check.Status.OK;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

public class CheckBoundaryTest {
    private final CheckBoundary boundary = new CheckBoundary();

    @Before public void setUp() throws Exception { givenChecks(); }

    private void givenChecks(Check... items) { boundary.checks = new MockInstance<>(items); }


    private CheckResponse checkResponse(Response response) { return (CheckResponse) response.getEntity(); }


    @Test
    public void shouldReturnEmptyChecks() throws Exception {
        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(OK);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(0, 0, 0, 0));
        assertThat(check.getChecks()).isEmpty();
    }

    @Test
    public void shouldReturnOneCheck() throws Exception {
        givenChecks(new OkCheck());

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(OK);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 0, 0));
        assertThat(check.getChecks()).containsExactly(new OkCheck().get());
    }

    @Test
    public void shouldReturnOneNamedCheck() throws Exception {
        givenChecks(new NamedCheck());

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(OK);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 0, 0));
        assertThat(check.getChecks()).containsExactly(new NamedCheck().get());
    }

    @Test
    public void shouldReturnOneThrowingCheck() throws Exception {
        givenChecks(new ThrowingCheck());

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(FAILURE);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(0, 0, 0, 1));
        assertThat(check.getChecks()).containsExactly(
                CheckResult.builder()
                           .type(ThrowingCheck.class.getName())
                           .status(FAILURE)
                           .comment("always fails")
                           .build());
    }

    @Test
    public void shouldFailToReturnErrorCheck() throws Exception {
        givenChecks(new JavaLangErrorCheck());

        Throwable throwable = catchThrowable(() -> boundary.get(null));

        assertThat(throwable).isExactlyInstanceOf(Error.class).hasMessage("really bad");
    }

    @Test
    public void shouldReturnTwoChecks() throws Exception {
        givenChecks(new OkCheck(), new WarningCheck());

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 1, 0));
        assertThat(check.getChecks()).containsExactly(
                new OkCheck().get(),
                new WarningCheck().get());
    }

    @Test
    public void shouldReturnThreeChecks() throws Exception {
        givenChecks(new OkCheck(), new WarningCheck(), new WarningCheck());

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 2, 0));
        assertThat(check.getChecks()).containsExactly(
                new OkCheck().get(),
                new WarningCheck().get(),
                new WarningCheck().get());
    }

    @Test
    public void shouldDestroyOneCheck() throws Exception {
        OkCheck okCheck = new OkCheck();
        givenChecks(okCheck);

        boundary.get(null);

        assertThat(((MockInstance<Check>) boundary.checks).getDestroyedInstances()).containsExactly(okCheck);
    }

    @Test
    public void shouldReturnOneCheckWithCustomTypeString() throws Exception {
        givenChecks(new Check() {
            @Override public CheckResult get() { return type("foo").status(WARNING).comment("bar").build(); }
        });

        Response response = boundary.get(null);

        CheckResponse check = checkResponse(response);
        assertThat(check.getChecks().get(0).getType()).isEqualTo("foo");
    }


    @Test
    public void shouldMapOkCheckTo200() throws Exception {
        givenChecks(new OkCheck());

        Response response = boundary.get(null);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapWarningCheckTo200() throws Exception {
        givenChecks(new WarningCheck());

        Response response = boundary.get(null);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapUnknownCheckTo200() throws Exception {
        givenChecks(new UnknownCheck());

        Response response = boundary.get(null);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapFailureCheckTo500() throws Exception {
        givenChecks(new FailureCheck());

        Response response = boundary.get(null);

        assertThat(response.getStatusInfo()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldGetSingleOkCheck() throws Exception {
        givenChecks(new NamedCheck());

        Response response = boundary.get("bar-name");

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.getEntity()).isEqualTo(new NamedCheck().get());
    }

    @Test
    public void shouldGetFirstOkCheck() throws Exception {
        OkCheck foo = new OkCheck("foo");
        OkCheck bar = new OkCheck("bar");
        givenChecks(foo, bar);

        Response response = boundary.get(OkCheck.class.getName());

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.getEntity()).isEqualTo(foo.get()).isNotEqualTo(bar.get());
    }

    @Test
    public void shouldGetSingleFailingCheck() throws Exception {
        givenChecks(new FailureCheck());

        Response response = boundary.get(FailureCheck.class.getName());

        assertThat(response.getStatusInfo()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getEntity()).isEqualTo(new FailureCheck().get());
    }

    @Test
    public void shouldFailToGetUnknownCheck() throws Exception {
        givenChecks(new FailureCheck());

        Throwable throwable = catchThrowable(() -> boundary.get("unknown"));

        assertThat(throwable).isInstanceOf(NotFoundException.class)
                             .hasMessage("no check found with type 'unknown'");
    }
}
