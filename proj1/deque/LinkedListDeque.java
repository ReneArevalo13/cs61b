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
        if (sentinel.next == sentinel || sentinel.prev == sentinel) {
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
        if (sentinel.next == sentinel || sentinel.prev == sentinel) {
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
        // set current node as the first nodesentinel
        Node currentNode = sentinel.next;
        for (int i = 0; i <= index; i++){
            if (i == index){
                return currentNode.item;
            }else{
                currentNode = currentNode.next;
            }
        }
        return null;
    }
    public Type getRecursive(int index){
        if (index > size){
            return null;
        }else{
            return recursiveHelper(sentinel.next, index);
        }
    }
    private Type recursiveHelper(Node n, int index){
        if (index == 0){
            return n.item;
        }else{
            return recursiveHelper(n.next, index - 1);
        }
    }

    public void printDeque(){
        for (Node p = sentinel.next; p != sentinel; p = p.next){
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }

    public Type getLast(){
        return sentinel.prev.item;
    }

    public static void main(String[] args){

        LinkedListDeque<Integer> LLD = new LinkedListDeque();
        LLD.addLast(9);
        LLD.addFirst(3);
        LLD.addLast(8);
        LLD.addLast(7);
        LLD.addLast(55);
        int output = LLD.get(4);
        int outputRecurse = LLD.getRecursive(4);
        LLD.printDeque();

//        Integer removed = LLD.removeLast();
//        System.out.println(removed);

    }


}
