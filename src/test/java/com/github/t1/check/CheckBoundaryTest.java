package com.github.t1.check;

import com.github.t1.testtools.MockInstance;
import org.junit.*;

import javax.ws.rs.core.Response;

import static com.github.t1.check.Status.OK;
import static com.github.t1.check.Status.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

public class CheckBoundaryTest {
    private final CheckBoundary boundary = new CheckBoundary();

    @Before public void setUp() throws Exception { givenChecks(); }

    private void givenChecks(Check... items) { boundary.checks = new MockInstance<>(items); }


    private CheckResponse checkResponse(Response response) { return (CheckResponse) response.getEntity(); }


    @Test
    public void shouldReturnEmptyChecks() throws Exception {
        Response response = boundary.get();

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(OK);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(0, 0, 0, 0));
        assertThat(check.getChecks()).isEmpty();
    }

    @Test
    public void shouldReturnOneCheck() throws Exception {
        givenChecks(new OkCheck());

        Response response = boundary.get();

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(OK);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 0, 0));
        assertThat(check.getChecks()).containsExactly(
                CheckResult.builder().type(OkCheck.class.getName()).status(OK).comment("foo").build());
    }

    @Test
    public void shouldReturnThreeChecks() throws Exception {
        givenChecks(new OkCheck(), new WarningCheck(), new WarningCheck());

        Response response = boundary.get();

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 2, 0));
        assertThat(check.getChecks()).containsExactly(
                CheckResult.builder().type(OkCheck.class.getName()).status(OK).comment("foo").build(),
                CheckResult.builder().type(WarningCheck.class.getName()).status(WARNING).comment("bar").build(),
                CheckResult.builder().type(WarningCheck.class.getName()).status(WARNING).comment("bar").build());
    }

    @Test
    public void shouldReturnTwoChecks() throws Exception {
        givenChecks(new OkCheck(), new WarningCheck());

        Response response = boundary.get();

        CheckResponse check = checkResponse(response);
        assertThat(check.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(check.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 1, 0));
        assertThat(check.getChecks()).containsExactly(
                CheckResult.builder().type(OkCheck.class.getName()).status(OK).comment("foo").build(),
                CheckResult.builder().type(WarningCheck.class.getName()).status(WARNING).comment("bar").build());
    }

    @Test
    public void shouldDestroyOneCheck() throws Exception {
        OkCheck okCheck = new OkCheck();
        givenChecks(okCheck);

        boundary.get();

        assertThat(((MockInstance<Check>) boundary.checks).getDestroyedInstances()).containsExactly(okCheck);
    }

    @Test
    public void shouldReturnOneCheckWithCustomTypeString() throws Exception {
        givenChecks(new Check() {
            @Override public CheckResult get() { return type("foo").status(WARNING).comment("bar").build(); }
        });

        Response response = boundary.get();

        CheckResponse check = checkResponse(response);
        assertThat(check.getChecks().get(0).getType()).isEqualTo("foo");
    }


    @Test
    public void shouldMapOkCheckTo200() throws Exception {
        givenChecks(new OkCheck());

        Response response = boundary.get();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapWarningCheckTo200() throws Exception {
        givenChecks(new WarningCheck());

        Response response = boundary.get();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapUnknownCheckTo200() throws Exception {
        givenChecks(new UnknownCheck());

        Response response = boundary.get();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @Test
    public void shouldMapFailureCheckTo500() throws Exception {
        givenChecks(new FailureCheck());

        Response response = boundary.get();

        assertThat(response.getStatusInfo()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}
