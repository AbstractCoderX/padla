package ru.progrm_jarvis.javacommons.collection;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class CollectionFactoryTest {

    @Test
    void testEmptyImmutableEnumSet() {
        val set = CollectionFactory.<TestEnum>createImmutableEnumSet();

        assertThat(set, empty());
        assertThat(set, hasSize(0));
        assertThat(set, emptyIterable());
        assertThat(set, iterableWithSize(0));
        // test immutability
        assertThrows(UnsupportedOperationException.class, () -> set.add(TestEnum.FOO));
        assertThat(set.remove(TestEnum.FOO), is(false));
    }

    @Test
    void testNonEmptyImmutableEnumSet() {
        val set = CollectionFactory.createImmutableEnumSet(TestEnum.BAR, TestEnum.BAZ, TestEnum.BAR);

        assertThat(set, not(empty()));
        assertThat(set, hasSize(2));
        assertThat(set, not(emptyIterable()));
        assertThat(set, iterableWithSize(2));
        // test immutability
        assertThrows(UnsupportedOperationException.class, () -> set.add(TestEnum.FOO));
        assertThrows(UnsupportedOperationException.class, () -> set.remove(TestEnum.FOO));
        assertThrows(UnsupportedOperationException.class, () -> set.add(TestEnum.BAR));
        assertThrows(UnsupportedOperationException.class, () -> set.remove(TestEnum.BAR));
        assertThrows(UnsupportedOperationException.class, () -> set.add(TestEnum.BAZ));
        assertThrows(UnsupportedOperationException.class, () -> set.remove(TestEnum.BAZ));
        // test containment
        assertThat(set, containsInAnyOrder(TestEnum.BAR, TestEnum.BAZ));
        assertTrue(set.contains(TestEnum.BAR));
        assertTrue(set.contains(TestEnum.BAZ));
        assertThat(set, not(contains(TestEnum.FOO)));
        // test equality (both sides)
        assertThat(new HashSet<>(Arrays.asList(TestEnum.BAR, TestEnum.BAZ)), equalTo(set));
        assertThat(set, equalTo(new HashSet<>(Arrays.asList(TestEnum.BAR, TestEnum.BAZ))));
        assertThat(set, equalTo(new ArrayList<>(Arrays.asList(TestEnum.BAR, TestEnum.BAZ))));
    }

    public enum TestEnum {
        FOO, BAR,
        BAZ {
            @Override
            public int foo() {
                return ThreadLocalRandom.current().nextInt();
            }
        };

        public int foo() {
            return 1;
        }
    }
}