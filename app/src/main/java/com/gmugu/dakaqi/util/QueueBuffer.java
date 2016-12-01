package com.gmugu.dakaqi.util;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by mugu on 16/11/27.
 */

public class QueueBuffer<T> {
    private Queue<T> queue;

    public QueueBuffer() {
        queue = new LinkedList<>();
    }

    public QueueBuffer(Queue<T> queue) {
        this.queue = queue;
    }

    public synchronized T pop() {
        if (queue.isEmpty())
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        return queue.poll();
    }

    public synchronized void put(T n) {
        queue.offer(n);
        notify();
    }


    public synchronized int getBufferSize() {
        return queue.size();
    }

}
