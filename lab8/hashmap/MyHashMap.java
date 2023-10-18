package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author ReneArevalo
 */
public class MyHashMap<K, V> implements Map61B<K, V> {



    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int INITIAL_SIZE = 16;
    private static final double LF_MAX = 0.75;
    private int initialSize;
    private double loadFactor;
    private int size = 0;
    private int sizeOfTable = 16;

    /** Constructors */
    public MyHashMap() {
        this.initialSize = INITIAL_SIZE;
        this.loadFactor = LF_MAX;
        createTable(initialSize);

    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.loadFactor = LF_MAX;
        createTable(initialSize);

    }


    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        buckets = (Collection<Node>[]) new Collection[tableSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();

        }
        return buckets;
    }
    private boolean calculateLoad(int size, int sizeOfTable) {
        double load = (double) size / sizeOfTable;
        return load >= LF_MAX;
    }
    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear(){
        buckets = (Collection<Node>[]) new Collection[initialSize];
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
        sizeOfTable = initialSize;
    }
    @Override
    public boolean containsKey(K key) {
        int location = arrayLocation(key);
        Collection<Node> bucket = buckets[location];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
               return true;
            }
        }
        return false;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
   public V get(K key) {
        int location = arrayLocation(key);
        Collection<Node> bucket = buckets[location];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }
    @Override
    public void put(K key, V value) {
        int location = arrayLocation(key);
        Collection<Node> bucket = buckets[location];

        //handle when key is already present in bucket: update node value
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;

        if (calculateLoad(size, sizeOfTable)) {
            resize();
        }

    }
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        Set<K> mySet = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                mySet.add(node.key);
            }
        }
        return mySet;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private int arrayLocation(K key) {
        return Math.floorMod(key.hashCode(), sizeOfTable);
    }
    private void resize() {
        sizeOfTable = sizeOfTable * 2;
        Collection<Node>[] newMap = (Collection<Node>[]) new Collection[sizeOfTable];

        ArrayList<Node> nodes = new ArrayList<>();

        for (Collection<Node> bucket : buckets) {
            nodes.addAll(bucket);
        }

        buckets = newMap;
        for (int i = 0; i < sizeOfTable; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
        for (Node node : nodes) {
            put(node.key, node.value);
        }

    }




}
