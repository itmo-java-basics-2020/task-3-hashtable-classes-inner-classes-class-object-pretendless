package ru.itmo.java;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashTableTest {
    private static final int NUMBER_OF_INVOCATIONS = 1_000_000;

    private final Random random = new Random();
    private final Supplier<Integer> defaultIntValuesSupplier = () -> random.nextInt(1_000_000);

    private Map<Object, Object> controlInstance;

    @Before
    public void init() {
        controlInstance = new HashMap<>();
    }

    @Test
    public void putMostly_fewKeys() {
        int numberOfKeys = 100;

        invoke(
                new HashTable(50, 0.3f), // initialCapacity, loadFactor
                new OperationPercentageProfile(90),
                new KeysProvider(numberOfKeys, () -> random.nextInt(numberOfKeys)),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    @Test
    public void putMostly_manyKeys() {
        int numberOfKeys = 10_000;

        invoke(
                new HashTable(1000), // initialCapacity
                new OperationPercentageProfile(90),
                new KeysProvider(numberOfKeys, () -> random.nextInt(numberOfKeys)),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    @Test
    public void putRemoveEqually_fewKeys() {
        int numberOfKeys = 100;

        invoke(
                new HashTable(50, 0.3f), // initialCapacity, loadFactor
                new OperationPercentageProfile(55),
                new KeysProvider(numberOfKeys, this::generateRandomString),
                this::generateRandomString,
                NUMBER_OF_INVOCATIONS
        );
    }


    @Test
    public void putRemoveEqually_ManyKeys() {
        int numberOfKeys = 100_000;

        invoke(
                new HashTable(1000), // initialCapacity
                new OperationPercentageProfile(55),
                new KeysProvider(numberOfKeys, this::generateRandomString),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    private void invoke(HashTable testInstance,
                        OperationPercentageProfile operationPercentageProfile,
                        KeysProvider keysProvider,
                        Supplier<?> valuesSupplier,
                        int numberOfInvocations) {

        for (int i = 0; i < numberOfInvocations; i++) {
            switch (operationPercentageProfile.nextOp()) {
                case PUT: {
                    Object key = keysProvider.randomKey();
                    Object value = valuesSupplier.get();

                    Object expectedPrevValue = controlInstance.put(key, value);
                    Object actualPrevValue = testInstance.put(key, value);

                    Assert.assertEquals(expectedPrevValue, actualPrevValue);
                    Assert.assertEquals(controlInstance.size(), testInstance.size());
                    Assert.assertEquals(value, testInstance.get(key));
                }
                case REMOVE: {
                    Object key = keysProvider.randomKey();

                    Object expectedPrevValue = controlInstance.remove(key);
                    Object actualPrevValue = testInstance.remove(key);

                    Assert.assertEquals(expectedPrevValue, actualPrevValue);
                    Assert.assertNull(testInstance.get(key));
                    Assert.assertEquals(controlInstance.size(), testInstance.size());
                }
            }
        }
    }

    private static class OperationPercentageProfile {
        private final float putPercentage;
        private final Random random = new Random();

        public OperationPercentageProfile(int putPercentage) {
            this.putPercentage = putPercentage / 100.0f;
        }

        Operation nextOp() {
            return random.nextDouble() < putPercentage ? Operation.PUT : Operation.REMOVE;
        }
    }

    private static class KeysProvider {
        private final List<Object> keys;
        private final Random random = new Random();

        public KeysProvider(int numberOfKeys, Supplier<Object> keyGenerator) {
            this.keys = Stream.generate(keyGenerator)
                    .limit(numberOfKeys)
                    .collect(Collectors.toList());
        }

        Object randomKey() {
            return keys.get(random.nextInt(keys.size()));
        }

    }

    private enum Operation {
        PUT, REMOVE
    }

    private String generateRandomString() {
        int length = random.nextInt(15);
        return random.ints(length, 0, Character.MAX_VALUE)
                .mapToObj(code -> Character.toString((char) code))
                .collect(Collectors.joining());
    }
}
