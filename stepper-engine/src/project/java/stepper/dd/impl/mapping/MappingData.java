package project.java.stepper.dd.impl.mapping;

import project.java.stepper.step.api.DataDefinitionDeclaration;

public class MappingData<K,V> {
    private final K key;
    private final V value;

    public MappingData(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

}
