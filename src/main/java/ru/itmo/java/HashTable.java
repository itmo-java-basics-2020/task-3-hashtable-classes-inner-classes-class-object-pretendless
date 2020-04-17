package ru.itmo.java;

import java.util.Map;

public class HashTable {
    private final float LOAD_FACTOR_BY_DEFAULT = 0.5f;
    private final int INITIAL_SIZE_BY_DEFAULT = 1024;

    private class HashObject {
        Object key, value;
        HashObject(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }
    }

    private int Hash_Code(Object key) {
        int hash = key.hashCode() % entry.length;
        return hash;
    }

    private HashObject[] entry;
    private final float load_factor;
    private int size = 0;
    private int fullsize = 0;

    HashTable(int initial_size, float load_factor) {
        this.load_factor = load_factor;
        entry = new HashObject[initial_size];
    }

    HashTable(int initial_size) {
        this.load_factor = LOAD_FACTOR_BY_DEFAULT;
        entry = new HashObject[initial_size];
    }

    HashTable() {
        this.load_factor = LOAD_FACTOR_BY_DEFAULT;
        entry = new HashObject[INITIAL_SIZE_BY_DEFAULT];
    }

    Object put(Object key, Object value) {
        int hash = Hash_Code(key);
        while(entry[hash] != null)
        {
            if(entry[hash].key == key) {
                Object old_value = entry[hash].value;
                entry[hash].value = value;
                return old_value;
            }

            hash++;
            if(hash == entry.length) {
                hash = 0;
            }
        }

        entry[hash] = new HashObject(key, value);
        fullsize++;
        size++;

        if(entry.length * load_factor < fullsize) {
            resize();
        }

        return null;
    }

    Object get(Object key) {
        int hash = Hash_Code(key);
        while(entry[hash] != null) {
            if(key.equals(entry[hash].key)) {
                return entry[hash].value;
            }

            hash++;
            if(hash == entry.length) {
                hash = 0;
            }
        }

        return 0;
    }

    Object remove(Object key) {
        int hash = Hash_Code(key);
        while(entry[hash] != null) {
            if(key.equals(entry[hash].key)) {
                entry[hash].key = null;
                size--;
                return entry[hash].value;
            }

            hash++;
            if(hash == entry.length) {
                hash = 0;
            }
        }

        return null;
    }

    int size() {
        return size;
    }

    void resize() {
        HashObject[] old_entry_map = entry;
        entry = new HashObject[old_entry_map.length * 2];
        size = 0;
        fullsize = 0;
        for(HashObject element : old_entry_map)
            if(element != null && element.key != null){
                put(element.key, element.value);
            }
    }

}
