package threadPool;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;


public class ThreadPool<V> implements Executor {

    public Collection<Thread> threadsList;
    private  WaitablePQueue<Task> taskPQ;

    private int currnetNumberOfThreads = 0;
    private Object lock = new Object();
    private final Object syncList = new Object();
    private boolean isShutdown;
    private boolean isPaused;


    /****************************************************************************/

    public ThreadPool(int numberOfThreads) {

        this.threadsList = new LinkedList<>();
        taskPQ = new WaitablePQueue<>();
        isShutdown = false;
        isPaused = false;
        ThreadsCreate(threadsList, numberOfThreads);
    }

    /****************************************************************************/

    @Override
    public void execute(Runnable runnable) {
        submit(Executors.callable(runnable, null), Priority.LOW);
    }

    /****************************************************************************/
    public Future<Void> submit(Runnable run, Priority priority) {
        if (run == null) {
            throw new NullPointerException();
        }
        return submit(Executors.callable(run, null), priority);
    }

    /****************************************************************************/
    public <V> Future<V> submit(Runnable run, Priority priority, V value) {
        if (run == null) {
            throw new NullPointerException();
        }
        return (submit(Executors.callable(run, value), priority));
    }


    /****************************************************************************/
    public <V> Future<V> submit(Callable<V> callable) {
        return (submit(callable, Priority.LOW));
    }

    /****************************************************************************/
    public <V> Future<V> submit(Callable<V> callable, Priority priority) {
        if(isShutdown){
            throw new RejectedExecutionException("ThreadPool isShutdown");
        }
        Task<V> task = new Task<>(callable, priority.getValue());
            taskPQ.enqueue(task);

        return (task.getFuture());
    }

    /****************************************************************************/
    public void setNumOfThreads(int numThreads) {
        if (numThreads > currnetNumberOfThreads) {
            ThreadsCreate(threadsList, numThreads - currnetNumberOfThreads);
        } else{
            ThreadsDestroy(currnetNumberOfThreads - numThreads, Priority.HIGH.getValue()+1);
        }
    }

    /****************************************************************************/

    public void pause() {
        this.isPaused = true;
    }

    /****************************************************************************/

    public void resume() {
        this.isPaused = false;
        synchronized (lock){
            lock.notifyAll();
        }
    }

    /****************************************************************************/

    public void shutdown() {
        if(isShutdown) {
            throw new RejectedExecutionException();
        }
        isShutdown = true;
        if(isPaused){
            resume();
        }
        ThreadsDestroy(currnetNumberOfThreads, 0);
    }

    /****************************************************************************/

    public void awaitTermination(){
            synchronized(syncList){
                while(!threadsList.isEmpty()){
                    try{
                        syncList.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
    }

    /****************************************************************************/

    private class Task<V> implements Comparable<Task> {

        private Callable<V> callable = null;
        private int priority = 0;
        private Taskfuture<V> future = null;
        private Task(Callable<V> callable, int  priority) {
            this.callable = callable;
            this.priority = priority;
            this.future = new Taskfuture();
        }

        /*----------------------------------------------------------*/
        @Override
        public int compareTo(Task task) {
            return task.priority - this.priority;
        }

        /*----------------------------------------------------------*/
        private Future<V> getFuture() {
            return (future);
        }

        /*----------------------------------------------------------*/
        private Callable<V> getCallable() {
            return (callable);
        }

        /****************************************************************************/
        private class Taskfuture<V> implements Future<V> {
            V value = null;
            boolean isCancelled = false;
            boolean isDone = false;
            boolean isFirstTIme = true;
            Semaphore sema = new Semaphore(0);
            public void setValue(V value) {
                this.value = value;
            }

            /*----------------------------------------------------------*/
            public void setIsDone(boolean trueOrfalse) {
                this.isDone = trueOrfalse;
            }

            /*----------------------------------------------------------*/
            @Override
            public boolean cancel(boolean isRemoved) {
                isRemoved = taskPQ.remove(Task.this);
                if (isRemoved){
                    isCancelled = true;
                }
                return (isRemoved);
            }

            /*----------------------------------------------------------*/
            @Override
            public boolean isCancelled() {
                return isCancelled;
            }

            /*----------------------------------------------------------*/
            @Override
            public boolean isDone() {
                return (isDone || isCancelled);
            }

            /*----------------------------------------------------------*/
            @Override
            public V get() throws InterruptedException, ExecutionException {

                if(isFirstTIme && !isCancelled) {
                    sema.acquire();
                }
                this.isFirstTIme = false;
                return value;
            }

            /*----------------------------------------------------------*/
            @Override
            public V get(long timeToWait, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                if(isFirstTIme) {
                    if (!sema.tryAcquire(timeToWait, timeUnit)) {
                        throw new TimeoutException();

                    }
                    isFirstTIme = false;
                }
                return value;
            }
        }
    }

    /****************************************************************************/
    private void ThreadsDestroy(int trdToKill, int priority) {
        Callable<V> task = SetRunningFalse();
        for (int i = 0; i < trdToKill; ++i) {
            Task<V> destroy = new Task<>(task, priority);
            taskPQ.enqueue(destroy);

        }
        currnetNumberOfThreads -= trdToKill;
    }

    /****************************************************************************/
    private Callable<V> SetRunningFalse() {
        Callable<V> task = () -> {
            Thread curThread = Thread.currentThread();
            ((WorkeringThread) curThread).setRunning(false);
            return null;
        };
        return (task);
    }

    /****************************************************************************/
    private class WorkeringThread extends Thread {

        private boolean isRunning = true;
        Task<V> task;

        @Override
        public void run() {

            while (isRunning) {
                if (isPaused) {
                    PauseThreads();
                }
                task = taskPQ.dequeue();
                try {
                    task.future.value = task.getCallable().call();
                } catch (Exception e) {
                    throw new RuntimeException("in Working thread");
                }
                task.future.setIsDone(true);
                task.future.sema.release();
            }
            synchronized(syncList)
            {
                threadsList.remove(this);
                syncList.notifyAll();
            }
        }

        /*----------------------------------------------------------*/
        public void setRunning(boolean running) {
            isRunning = running;
        }
    }

    /****************************************************************************/

    /* ------------- Private Methods --------- */
    private void ThreadsCreate(Collection<Thread> threadsList, int trdToCreate) {
        for (int i = 0; i < trdToCreate; ++i) {
            WorkeringThread newThread = new WorkeringThread();
            synchronized(syncList) {
                threadsList.add(newThread);
            }
            newThread.start();
        }
        currnetNumberOfThreads +=trdToCreate;
    }

    /*----------------------------------------------------------*/
    private void PauseThreads(){
        try {
            synchronized (lock){
                lock.wait();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


