package com.github.t1.check;

import com.github.t1.testtools.MockInstance;
import org.junit.*;

import static com.github.t1.check.Status.*;
import static org.assertj.core.api.Assertions.*;

public class CheckBoundaryTest {
    private final CheckBoundary boundary = new CheckBoundary();

    @Before public void setUp() throws Exception { givenChecks(); }

    private void givenChecks(Check... items) { boundary.checks = new MockInstance<>(items); }


    private static class FooCheck implements Check {
        @Override public CheckResult get() {
            return CheckResult.of(FooCheck.class).status(OK).comment("foo").build();
        }
    }

    private static class BarCheck implements Check {
        @Override public CheckResult get() {
            return CheckResult.of(BarCheck.class).status(WARNING).comment("bar").build();
        }
    }

    @Test
    public void shouldReturnEmptyChecks() throws Exception {
        CheckResponse response = boundary.get();

        assertThat(response.getSummary().getStatus()).isEqualTo(OK);
        assertThat(response.getSummary().getCounters()).isEqualTo(Status.mapOf(0, 0, 0, 0));
        assertThat(response.getChecks()).isEmpty();
    }

    @Test
    public void shouldReturnOneCheck() throws Exception {
        givenChecks(new FooCheck());

        CheckResponse response = boundary.get();

        assertThat(response.getSummary().getStatus()).isEqualTo(OK);
        assertThat(response.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 0, 0));
        assertThat(response.getChecks()).containsExactly(
                CheckResult.of(FooCheck.class).status(OK).comment("foo").build());
    }

    @Test
    public void shouldReturnTwoChecks() throws Exception {
        givenChecks(new FooCheck(), new BarCheck());

        CheckResponse response = boundary.get();

        assertThat(response.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(response.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 1, 0));
        assertThat(response.getChecks()).containsExactly(
                CheckResult.of(FooCheck.class).status(OK).comment("foo").build(),
                CheckResult.of(BarCheck.class).status(WARNING).comment("bar").build());
    }

    @Test
    public void shouldReturnThreeChecks() throws Exception {
        givenChecks(new FooCheck(), new BarCheck(), new BarCheck());

        CheckResponse response = boundary.get();

        assertThat(response.getSummary().getStatus()).isEqualTo(WARNING);
        assertThat(response.getSummary().getCounters()).isEqualTo(Status.mapOf(1, 0, 2, 0));
        assertThat(response.getChecks()).containsExactly(
                CheckResult.of(FooCheck.class).status(OK).comment("foo").build(),
                CheckResult.of(BarCheck.class).status(WARNING).comment("bar").build(),
                CheckResult.of(BarCheck.class).status(WARNING).comment("bar").build());
    }
}
