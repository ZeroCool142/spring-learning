package ru.iteco.springl.concurrent;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class ConcurrentMapImpl<K, V> implements ConcurrentMap<K, V> {

    private static final int DEFAULT_SIZE = 2048;
    private final AtomicReference<Node<K, V>[]>[] table;
    private final Lock[] locks;
    private final AtomicInteger[] modCounts;

    public ConcurrentMapImpl() {
        this(DEFAULT_SIZE);
    }

    public ConcurrentMapImpl(int size) {
        table = new AtomicReference[size];
        locks = new ReentrantLock[size];
        modCounts = new AtomicInteger[size];
        Arrays.setAll(table, n -> new AtomicReference<>(new Node[0]));
        Arrays.setAll(locks, n -> new ReentrantLock());
        Arrays.setAll(modCounts, n -> new AtomicInteger(0));
    }

    public class Node<K, V> implements Map.Entry<K, V> {

        volatile int hash;
        volatile K key;
        volatile V value;

        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }

    @Override
    public int size() {
        return Arrays.stream(table).mapToInt(s -> s.get().length).sum();
    }

    @Override
    public boolean isEmpty() {
        return Arrays.stream(table).mapToInt(s -> s.get().length).sum() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return Arrays.stream(table[hash(key) & (table.length - 1)].get())
                .anyMatch(n -> n.key.equals(key));

    }

    @Override
    public boolean containsValue(Object value) {
        return Arrays.stream(table)
                .map(AtomicReference::get)
                .flatMap(e -> Arrays.stream(e).map(v -> v.value))
                .anyMatch(e -> e.equals(value));
    }

    @Override
    public V get(Object key) {
        int hash = hash(key);
        int sectionIndex = hash & (table.length - 1);
        Node<K, V> node = getNode(hash, table[sectionIndex].get(), key);

        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value);
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key);
        int sectionIndex = hash & (table.length - 1);
        Node<K, V>[] section = table[sectionIndex].get();
        Node<K, V>[] newNodes;
        Node<K, V> val;
        int modCount;

        do {
            modCount = modCounts[sectionIndex].get();
            int elementIndex = -1;
            for (int i = 0; i < section.length; ++i) {
                if (section[i] != null && ((section[i].hash == hash && section[i].key == key) || (key != null && key.equals(section[i])))) {
                    elementIndex = i;
                    break;
                }
            }
            if (elementIndex != -1) {
                newNodes = new Node[section.length - 1];
                val = section[elementIndex];
                System.arraycopy(section, 0, newNodes, 0, elementIndex);
                System.arraycopy(section, elementIndex, newNodes, elementIndex, (section.length - elementIndex - 1));
            } else {
                return null;
            }
        } while (!writeArray(newNodes, sectionIndex, modCount));

        return val == null ? null : val.value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Set<? extends Entry<? extends K, ? extends V>> entries = m.entrySet();

        entries.forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; ++i) {
            locks[i].lock();
            try {
                table[i] = new AtomicReference<>(new Node[0]);
                modCounts[i] = new AtomicInteger(0);
            } finally {
                locks[i].unlock();
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return Arrays.stream(table)
                .map(AtomicReference::get)
                .flatMap(e -> Arrays.stream(e).map(k -> k.key))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return Arrays.stream(table)
                .map(AtomicReference::get)
                .flatMap(e -> Arrays.stream(e).map(v -> v.value))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return Arrays.stream(table)
                .map(AtomicReference::get)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            return put(key, value);
        }
        return get(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        V v = get(key);
        if (v == value || (value != null && value.equals(v))) {
            remove(key);
            return true;
        }

        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        V v = get(key);
        if ((v == oldValue) || (oldValue != null && oldValue.equals(v))) {
            put(key, newValue);
            return true;
        }
        return false;
    }

    @Override
    public V replace(Object key, Object value) {
        return null;
    }

    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private V putVal(int hash, K key, V value) {
        int sectionIndex = hash & (table.length - 1);
        Node<K, V>[] nodes;
        Node<K, V>[] newNodes;
        V pVal = null;
        int modCount;

        do {
            modCount = modCounts[sectionIndex].get();
            nodes = table[sectionIndex].get();

            Node<K, V> kvNode = getNode(hash, nodes, key);
            if (kvNode != null) {
                newNodes = Arrays.copyOf(nodes, nodes.length);
                pVal = kvNode.value;
                kvNode.value = value;
            } else {
                int len = nodes.length;
                newNodes = Arrays.copyOf(nodes, len + 1);
                newNodes[len] = new Node<>(hash, key, value);
            }

        } while (!writeArray(newNodes, sectionIndex, modCount));

        return pVal;
    }

    private Node<K, V> getNode(int hash, Node<K, V>[] section, Object key) {
        for (Node<K, V> n : section) {
            if (n != null && ((n.hash == hash && n.key == key) || (key != null && key.equals(n.key)))) {
                return n;
            }
        }
        return null;
    }

    private boolean writeArray(Node<K, V>[] newNodes, int sectionIndex, int lastModCount) {
        final Lock lock = locks[sectionIndex];
        lock.lock();
        try {
            if (modCounts[sectionIndex].get() == lastModCount) {
                table[sectionIndex].set(newNodes);
                modCounts[sectionIndex].incrementAndGet();
                return true;
            }

        } finally {
            lock.unlock();
        }
        return false;
    }

//        putValue
//
//        Node<K, V> node;
//
//        if ((node = table[hash & (table.length - 1)]) == null) {
//            table[hash & (table.length - 1)] = (node = new Node<>(hash(key), key, value, null));
//            size.incrementAndGet();
//        } else {
//            synchronized (table[hash & (table.length - 1)]) {
//                Node<K, V> e;
//                K k;
//                if (node.hash == hash && ((k = node.key) == key || (key != null && key.equals(k)))) {
//                    e = node;
//                } else {
//                    while (true) {
//                        if ((e = node.next) == null) {
//                            node.next = new Node<>(hash, key, value, null);
//                            size.incrementAndGet();
//                            break;
//                        }
//                        if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
//                            break;
//                        }
//                        node = e;
//                    }
//                }
//                if (e != null) {
//                    V oldValue = e.value;
//                    if (!putIfAbsent || oldValue == null) {
//                        e.value = value;
//                    }
//                    return oldValue;
//                }
//            }
//        }
//
//        return node == null ? null : node.value;
}