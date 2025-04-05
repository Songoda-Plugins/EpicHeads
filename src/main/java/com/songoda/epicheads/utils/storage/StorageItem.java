package com.songoda.epicheads.utils.storage;

import java.util.ArrayList;
import java.util.List;

public class StorageItem {

    private final Object object;
    private String key = null;

    public StorageItem(Object object) {
        this.object = object;
    }

    public StorageItem(String key, Object object) {
        this.key = key;
        this.object = object;
    }

    public String getKey() {
        return key;
    }

    public String asString() {
        if (object == null) return null;
        if (!(object instanceof String))
            return String.valueOf(object);
        return (String) object;
    }

    public boolean asBoolean() {
        if (object == null) return false;
        return (boolean) object;
    }

    public int asInt() {
        if (object == null) return 0;
        if (object instanceof String)
            return Integer.parseInt(asString());
        return (int) object;
    }

    public Object asObject() {
        return object;
    }

    public List<Integer> asIntList() {
        List<Integer> list = new ArrayList<>();
        if (object == null) return list;
        String[] stack = ((String) object).split(";");
        for (String item : stack) {
            if (item.equals("")) continue;
            list.add(Integer.valueOf(item));
        }
        return list;
    }
}
