package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V>
{
    /**
     * Default object for this registry, returned when an object is not found.
     */
    private final V defaultObject;

    public RegistryDefaulted(V defaultObjectIn)
    {
        this.defaultObject = defaultObjectIn;
    }

    public V getObject(K name)
    {
        V lvt_2_1_ = super.getObject(name);
        return (V)(lvt_2_1_ == null ? this.defaultObject : lvt_2_1_);
    }
}
