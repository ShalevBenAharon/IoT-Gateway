package threadPool;

import java.util.PriorityQueue;

import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitablePQueue<T>  {

    private PriorityQueue<T> queue;
    Lock lock = new ReentrantLock();
    Semaphore sem = new Semaphore(0);
    public WaitablePQueue(Comparator<? super T> comparator) {
        queue = new PriorityQueue<>(comparator);
    }
    public WaitablePQueue() {
        queue = new PriorityQueue<>();
    }

    public void enqueue(T element){
        lock.lock();
        queue.add(element);
        lock.unlock();
        sem.release();
    }

    public T dequeue() {
        T retDate;
        try {
            sem.acquire();
            lock.lock();
            retDate = queue.poll();
            lock.unlock();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return (retDate);
    }

    //time in milliseconds
    public T dequeue(int time) {
        boolean timeOut;
        T retValue = null;
        try{
            timeOut = sem.tryAcquire(time, TimeUnit.MILLISECONDS);
            if(!timeOut){
                throw new TimeoutException();
            }
            lock.lock();
            retValue = queue.poll();
            lock.unlock();

        }catch(InterruptedException | TimeoutException e) {
            throw new RuntimeException();
        }
        return(retValue);
    }

    public boolean remove(T element)  {
        boolean isRemoved = false;
        if(sem.tryAcquire()) {
            lock.lock();
            isRemoved = queue.remove(element);
            if (!isRemoved) {
                sem.release();
            }
            lock.unlock();
        }
        return(isRemoved);
    }
}