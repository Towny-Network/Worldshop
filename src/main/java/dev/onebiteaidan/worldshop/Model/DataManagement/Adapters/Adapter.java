package dev.onebiteaidan.worldshop.Model.DataManagement.Adapters;

import java.util.Map;

public interface Adapter<A> {

    /**
     * Serializes object to a map
     * @param object Object to serialize
     */
    Map<String, Object> serialize(A object);

    /**
     * Deserializes map into object.
     * @param data data to parse
     * @return returns the deserialized object.
     */
    A deserialize(Map<String, Object> data);

}