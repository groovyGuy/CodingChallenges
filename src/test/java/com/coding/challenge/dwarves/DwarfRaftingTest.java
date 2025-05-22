package com.coding.challenge.dwarves;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DwarfRaftingTest {

    @Test
    public void basicScenario() {
        assertEquals(6, DwarfRafting.solution(4, "1B 1C 4B 1D 2A", "3B 2D"));
        assertEquals(7, DwarfRafting.solution(4, "1B 1C 4B 1D 2A", "3B 2D"));
    }
}