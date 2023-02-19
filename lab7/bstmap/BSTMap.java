package bstmap;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    private BSTNode root;
    private class BSTNode {

        private K key;
        private V value;
        private BSTNode left, right;
        private int size;

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }
    @Override
    public void clear(){
        root = null;
    }
    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to containsKey() is null");
//        if (get(key) == null){
//            return true;
//        }
        return get(key) != null;
    }
    @Override
    public int size(){
        return size(root);
    }
    private int size(BSTNode x) {
        if (x == null) {
            return 0;
        } else {
            return x.size;
        }
    }
    @Override
    public V get(K key) {
        return get(root, key);
    }
    private V get(BSTNode x, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        // check for empty node
        if (x == null) {
            return null;
        }
        int compare = key.compareTo(x.key);
        if (compare < 0) {
            return get(x.left, key);
        } else if (compare > 0) {
            return get(x.right, key);
        } else {
            return x.value;
        }
    }
    @Override
    public void put(K key, V value) {
        root = put (root, key, value);
    }
    private BSTNode put(BSTNode x, K key, V value){
        if (x == null) {
            return new BSTNode(key, value, 1);
        }
        int compare = key.compareTo(x.key);
        if (compare < 0) {
            x.left = put(x.left, key, value);
        } else if (compare > 0) {
            x.right = put(x.right, key, value);
        } else {
//            x.key = key;
            x.value = value;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
