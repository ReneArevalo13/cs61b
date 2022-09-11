package deque;

import jh61b.junit.In;

public class LinkedListDeque<Type> {
    private Node sentinel;
    private int size;
    /* nested class that builds each node. Doubly linked */
    public class Node {
        public Type item;
        public Node next;
        public Node prev;
        public Node(Type item, Node next, Node prev){
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
    /* create empty LLD*/
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(Type item){
        sentinel.next = new Node(item, sentinel.next, sentinel);
        sentinel.next.next.prev = sentinel.next;
        size++;
    }
    public void addLast(Type item){
        sentinel.prev = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.prev.next = sentinel.prev;
        size++;
    }

    public Type removeFirst() {
        if (sentinel.next == null) {
            return null;
        } else {
            Type firstRemoved = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev.prev = null;
            sentinel.next.prev.next = null;
            sentinel.next.prev = sentinel;
            size--;
            return firstRemoved;
        }
    }

    public Type removeLast() {
        if (sentinel.prev == null) {
            return null;
        } else {
            Type lastRemoved = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next.prev = null;
            sentinel.prev.next.next = null;
            sentinel.prev.next = sentinel;
            size--;
            return lastRemoved;
        }
    }

    public Type get(int index){
        // set current node as the first node
        Node current_Node = sentinel.next;
        int currentIndex = 0;

        while (current_Node.next != sentinel){
            if (currentIndex == index){
                return current_Node.item;
            } else {
              currentIndex++;
              current_Node = current_Node.next;
            }
        }
        return null;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }

    public static void main(String[] args){

        LinkedListDeque<Integer> LLD = new LinkedListDeque();
        LLD.addLast(9);
        LLD.addFirst(3);
        LLD.addLast(8);
        LLD.addLast(7);
        LLD.addLast(55);
        int output = LLD.get(2);
//        Integer removed = LLD.removeLast();
//        System.out.println(removed);

    }


}
