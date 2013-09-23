package de.twenty11.unitprofile.domain.test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.twenty11.unitprofile.domain.Clock;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {
    
    @Test
    public void should_start_immediately() {
        Clock clock = new Clock();
        assertThat(clock.getStart(), is(not(nullValue())));
    }

    @Test
    public void new_clock_should_be_running() {
        Clock clock = new Clock();
        assertThat(clock.isStopped(), is(false));
    }

    @Test
    public void elapsedTime_should_be_positive_when_running() {
        Clock clock = new Clock();
        assertThat(clock.getElapsed(), is(greaterThanOrEqualTo(0L)));
    }

    @Test
    public void elapsedTime_should_be_positive_when_stopped() {
        Clock clock = new Clock();
        clock.stop();
        assertThat(clock.getElapsed(), is(greaterThanOrEqualTo(0L)));
    }
    
    @Test
    public void toString_contains_Now_when_not_stopped() {
        Clock clock = new Clock();
        assertThat(clock.toString(), containsString("Now"));
    }

    @Test
    public void toString_contains_End_when_stopped() {
        Clock clock = new Clock();
        clock.stop();
        assertThat(clock.toString(), containsString("End"));
    }


}
