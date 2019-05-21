package com.wl4g.devops.ci.devtool;

/**
 * @author vjay
 * @date 2019-05-21 09:58:00
 */
public class JoinDemo {

    public static void main(String[] args) throws Exception {

        /*ThreadPoolTask thread = new ThreadPoolTask(1);
        ThreadPoolTask thread2 = new ThreadPoolTask(2);
        ThreadPoolTask thread3 = new ThreadPoolTask(3);

        //ThreadPool.pool.execute(thread);
        //ThreadPool.pool.execute(thread2);
        //ThreadPool.pool.execute(thread3);
        thread.start();
        thread2.start();
        thread3.start();

        //Thread.sleep(2000L);

        thread.join();
        thread2.join();
        thread3.join();*/




        System.out.println("Now all thread done!");

    }

    private static void mainWork() throws Exception{

        System.out.println("Main thread start work!");

        //sleep

        Thread.sleep(2000L);

        System.out.println("Main Thread work done!");

    }

    /**

     * 子线程类

     * @author fuhg

     */

    private static class SubThread implements Runnable{

        public void run() {

            // TODO Auto-generated method stub

            System.out.println("Sub thread is starting!");

            try {

                Thread.sleep(5000L);

            } catch (InterruptedException e) {

                // TODO Auto-generated catch block

                e.printStackTrace();

            }

            System.out.println("Sub thread is stopping!");

        }

    }

}
