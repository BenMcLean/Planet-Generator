package net.benmclean.planetgenerator;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkingTest {
    @Test
    public void thisAlwaysPasses() {
        assertTrue(true);
    }

    @Test
    @Ignore
    public void thisAlwaysFails() {
        System.out.println("wut");
        assertTrue(false);
    }

    @Test
    @Ignore
    public void thisIsIgnored() {
    }
}
