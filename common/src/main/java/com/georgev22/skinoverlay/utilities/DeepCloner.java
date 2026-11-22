package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.datastructures.maps.*;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility for cloning objects with support for:
 * <ul>
 *     <li>{@link Cloneable} objects (via public {@code clone()})</li>
 *     <li>Standard JDK collections/maps/queues/deques</li>
 *     <li>Concurrent collections</li>
 *     <li>Fallback reflection-based instantiation if no handler is registered</li>
 *     <li>Special handling of known immutables (returned as-is)</li>
 * </ul>
 *
 * <p>Cloning modes:</p>
 * <ul>
 *     <li>{@code deep = true}: recursively clones contents.</li>
 *     <li>{@code deep = false}: creates a new container but reuses element references.</li>
 *     <li>If cloning fails, returns the same reference.</li>
 * </ul>
 *
 * <p>This class is thread-safe and extensible:
 * you can register custom cloners and immutables at runtime.</p>
 */
@SuppressWarnings("unchecked")
public final class DeepCloner {
    /**
     * Registry of collection/map cloners (thread-safe).
     */
    private static final ConcurrentMap<Class<?>, Function<Object, Object>> CLONERS = new ConcurrentHashMap<>();

    /**
     * Registry of known immutable classes (thread-safe).
     */
    private static final Set<Class<?>> IMMUTABLES = ConcurrentHashMap.newKeySet();

    static {
        // === Built-in immutables ===
        IMMUTABLES.addAll(Set.of(
                String.class,
                UUID.class,

                // Numbers
                Integer.class, Long.class, Short.class, Byte.class,
                Double.class, Float.class, BigDecimal.class, BigInteger.class,

                // Primitive wrappers
                Boolean.class, Character.class,

                // Optionals
                Optional.class, OptionalInt.class, OptionalLong.class, OptionalDouble.class
        ));

        // === MAPS ===
        register(ObjectMap.class, o -> switch (o) {
            case ObservableObjectMap<?, ?> ignored -> new ObservableObjectMap<>();
            case ConcurrentHashObjectMap<?, ?> ignored -> new ConcurrentHashObjectMap<>();
            case HashObjectMap<?, ?> ignored -> new HashObjectMap<>();
            case LinkedHashObjectMap<?, ?> ignored -> new LinkedHashObjectMap<>();
            case TreeObjectMap<?, ?> treeObjectMap -> new TreeObjectMap<>(treeObjectMap.comparator());
            case UnmodifiableObjectMap<?, ?> ignored ->
                    throw new UnsupportedOperationException("Cannot clone unmodifiable map");
            default -> throw new IllegalArgumentException("Unknown map type: " + o.getClass());
        });
        register(HashMap.class, o -> new HashMap<>());
        register(LinkedHashMap.class, o -> new LinkedHashMap<>());
        register(TreeMap.class, o -> new TreeMap<>(((TreeMap<?, ?>) o).comparator()));
        register(WeakHashMap.class, o -> new WeakHashMap<>());
        register(IdentityHashMap.class, o -> new IdentityHashMap<>());
        register(EnumMap.class, o -> new EnumMap<>(((EnumMap<?, ?>) o)));

        register(ConcurrentHashMap.class, o -> new ConcurrentHashMap<>());
        register(ConcurrentSkipListMap.class, o -> new ConcurrentSkipListMap<>(((ConcurrentSkipListMap<?, ?>) o).comparator()));

        register(Hashtable.class, o -> new Hashtable<>());
        register(Properties.class, o -> new Properties());

        // === LISTS ===
        register(ArrayList.class, o -> new ArrayList<>(((List<?>) o).size()));
        register(CopyOnWriteArrayList.class, o -> new CopyOnWriteArrayList<>());
        register(Vector.class, o -> new Vector<>(((List<?>) o).size()));

        // === STACK ===
        register(Stack.class, o -> new Stack<>());

        // === SETS ===
        register(HashSet.class, o -> new HashSet<>());
        register(LinkedHashSet.class, o -> new LinkedHashSet<>());
        register(TreeSet.class, o -> new TreeSet<>(((TreeSet<?>) o).comparator()));
        register(EnumSet.class, o -> EnumSet.copyOf((EnumSet<?>) o));

        register(CopyOnWriteArraySet.class, o -> new CopyOnWriteArraySet<>());
        register(ConcurrentSkipListSet.class, o -> new ConcurrentSkipListSet<>(((ConcurrentSkipListSet<?>) o).comparator()));

        // === QUEUES ===
        register(ArrayDeque.class, o -> new ArrayDeque<>(((Queue<?>) o).size()));
        register(PriorityQueue.class, o -> new PriorityQueue<>(((PriorityQueue<?>) o).size(), ((PriorityQueue<?>) o).comparator()));
        register(LinkedList.class, o -> new LinkedList<>());

        register(ConcurrentLinkedQueue.class, o -> new ConcurrentLinkedQueue<>());
        register(PriorityBlockingQueue.class, o -> new PriorityBlockingQueue<>(((PriorityBlockingQueue<?>) o).size(), ((PriorityBlockingQueue<?>) o).comparator()));
        register(LinkedBlockingQueue.class, o -> new LinkedBlockingQueue<>(((LinkedBlockingQueue<?>) o).remainingCapacity() + ((LinkedBlockingQueue<?>) o).size()));
        register(ArrayBlockingQueue.class, o -> {
            ArrayBlockingQueue<?> q = (ArrayBlockingQueue<?>) o;
            return new ArrayBlockingQueue<>(q.size() + q.remainingCapacity(), false);
        });
        register(DelayQueue.class, o -> new DelayQueue<>());
        register(SynchronousQueue.class, o -> new SynchronousQueue<>());
        register(LinkedTransferQueue.class, o -> new LinkedTransferQueue<>());

        // === DEQUES ===
        register(ConcurrentLinkedDeque.class, o -> new ConcurrentLinkedDeque<>());
    }

    private DeepCloner() {
        // utility class
    }

    /**
     * Register a custom cloner for a given type.
     * Thread-safe.
     *
     * @param clazz   the class to register
     * @param factory a factory that produces a new empty instance of the given type
     */
    public static void register(Class<?> clazz, Function<Object, Object> factory) {
        CLONERS.put(clazz, factory);
    }

    /**
     * Register a custom immutable type.
     * Thread-safe.
     *
     * @param clazz the immutable class to register
     */
    public static void registerImmutable(Class<?> clazz) {
        IMMUTABLES.add(clazz);
    }

    /**
     * Deep clones an object if possible.
     *
     * @param value the object to clone
     * @return a deep-cloned copy, or the same reference if immutable or unhandled
     */
    public static Object cloneValue(Object value) {
        return cloneValue(value, true);
    }

    /**
     * Clones an object with control over depth.
     *
     * @param value the object to clone
     * @param deep  whether to recursively clone contents (true) or shallow copy (false)
     * @return a cloned copy of the object, or the same reference if immutable or unhandled
     */
    public static Object cloneValue(Object value, boolean deep) {
        if (value == null) return null;

        // Known immutables → return as-is
        if (IMMUTABLES.contains(value.getClass())) return value;

        // Enum values are immutable
        switch (value) {
            case Enum<?> anEnum -> {
                return anEnum;
            }

            // Skip cloning suppliers (generally stateless)
            case Supplier<?> supplier -> {
                return supplier;
            }

            // If Copyable with deepCopy() method
            case Copyable<?> cloneable -> {
                return cloneable.deepCopy();
            }

            // If Cloneable with clone() method
            case java.lang.Cloneable ignored -> {
                try {
                    return value.getClass().getMethod("clone").invoke(value);
                } catch (Exception ignored1) {
                    // fallback to registry
                }
            }
            default -> {
            }
        }

        // Try registered cloners
        for (Map.Entry<Class<?>, Function<Object, Object>> entry : CLONERS.entrySet()) {
            if (entry.getKey().isInstance(value)) {
                Object cloned = entry.getValue().apply(value);
                fillCollection(value, cloned, deep);
                return cloned;
            }
        }

        // === GENERIC FALLBACK ===
        if (value instanceof Map<?, ?> map) {
            Object cloned = tryReflectiveCreate(value.getClass(), HashMap::new);
            for (Map.Entry<?, ?> e : map.entrySet()) {
                ((Map<Object, Object>) cloned).put(
                        deep ? cloneValue(e.getKey(), true) : e.getKey(),
                        deep ? cloneValue(e.getValue(), true) : e.getValue()
                );
            }
            return cloned;
        }

        if (value instanceof Collection<?> coll) {
            Object cloned = tryReflectiveCreate(value.getClass(), () ->
                    switch (coll) {
                        case Set<?> ignored -> new HashSet<>();
                        case Queue<?> ignored -> new ArrayDeque<>();
                        default -> new ArrayList<>();
                    });
            for (Object o : coll) {
                ((Collection<Object>) cloned).add(deep ? cloneValue(o, true) : o);
            }
            return cloned;
        }

        // Default: assume immutable
        return value;
    }

    /**
     * Fill a cloned collection/map with contents from the original.
     *
     * @param original the original collection/map
     * @param cloned   the new collection/map
     * @param deep     whether to clone contents deeply
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void fillCollection(Object original, Object cloned, boolean deep) {
        if (original instanceof Map mapOriginal && cloned instanceof Map mapCloned) {
            for (Object entryObj : mapOriginal.entrySet()) {
                Map.Entry e = (Map.Entry) entryObj;
                mapCloned.put(
                        deep ? cloneValue(e.getKey(), true) : e.getKey(),
                        deep ? cloneValue(e.getValue(), true) : e.getValue()
                );
            }
        } else if (original instanceof Collection collOriginal && cloned instanceof Collection collCloned) {
            for (Object o : collOriginal) {
                collCloned.add(deep ? cloneValue(o, true) : o);
            }
        }
    }

    /**
     * Attempt to create a new instance of the given class reflectively.
     * Falls back to the provided supplier if reflection fails.
     *
     * @param clazz    the class to instantiate
     * @param fallback a supplier of a fallback instance
     * @return a new instance of the given class or a fallback
     */
    private static Object tryReflectiveCreate(Class<?> clazz, Supplier<Object> fallback) {
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception ignored) {
            return fallback.get();
        }
    }
}
