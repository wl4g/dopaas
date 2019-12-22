package cn.edu.njupt.utils.uploadUtils;


/**
 * 使用 Snowflake 算法生成的 64 位 long 类型的 ID，结构如下:<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1) 01 位标识，由于 long 在 Java 中是有符号的，最高位是符号位，正数是 0，负数是 1，ID 一般使用正数，所以最高位是 0<br>
 * 2) 41 位时间截(毫秒级)，注意，41 位时间截不是存储当前时间的时间截，而是存储时间截的差值(当前时间 - 开始时间)得到的值，
 *       开始时间截，一般是业务开始的时间，由我们程序来指定，如 IdWorker 中的 START_TIMESTAMP 属性。
 *       41 位的时间截，可以使用 70 年: (2^41)/(1000*60*60*24*365) = 69.7 年<br>
 * 3) 10 位的数据机器位，可以部署在 1024 个节点，包括 5 位 datacenterId 和 5 位 workerId<br>
 * 4) 12 位序列，毫秒内的计数，12 位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生 4096 个 ID 序号<br>
 *
 * SnowFlake 的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生 ID 碰撞(由数据中心 ID 和机器 ID 作区分)，并且效率较高。
 * 最多支持 1024 台机器，每台机器每毫秒能够生成最多 4096 个 ID，整个集群理论上每秒可以生成 1024 * 1000 * 4096 = 42 亿个 ID。
 */
public class Snowflake {
    /** 开始时间截(2017-01-01)，单位毫秒 */
    private static final long START_TIMESTAMP = 1483228800000L;

    /** 机器 ID 所占的位数 */
    private static final long WORKER_ID_BITS = 5L;

    /** 数据标识 ID 所占的位数 */
    private static final long DATACENTER_ID_BITS = 5L;

    /** 支持的最大机器 ID，结果是 31: 0B11111 */
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    /** 支持的最大数据中心 ID，结果是 31: 0B11111 */
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);

    /** 序列在 ID 中占的位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 机器 ID 向左移 12 位 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /** 数据中心 ID 向左移 17 位(12+5) */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /** 时间截向左移 22 位(5+5+12) */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /** 生成序列的掩码，这里为 4095(0B111111111111=0xFFF=4095) */
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    /** 工作机器 ID(0~31) */
    private long workerId;

    /** 数据中心 ID(0~31) */
    private long datacenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成 ID 的时间截 */
    private long lastTimestamp = -1L;

    /**
     * 创建 ID 生成器的方式一: 使用工作机器的序号，范围是 [0, 1023]，优点是方便给机器编号
     *
     * @param workerId 工作机器 ID
     */
    public Snowflake(long workerId) {
        long maxMachineId = (MAX_DATACENTER_ID +1) * (MAX_WORKER_ID +1) - 1; // 1023

        if (workerId < 0 || workerId > maxMachineId) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", maxMachineId));
        }

        this.datacenterId = (workerId >> WORKER_ID_BITS) & MAX_DATACENTER_ID;
        this.workerId = workerId & MAX_WORKER_ID;
    }

    /**
     * 创建 ID 生成器的方式二: 使用工作机器 ID 和数据中心 ID，优点是方便分数据中心管理
     *
     * @param datacenterId 数据中心 ID (0~31)
     * @param workerId     工作机器 ID (0~31)
     */
    public Snowflake(long datacenterId, long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个 ID(该方法是线程安全的)，同一机器同一时间可产生 4096 个 ID，70 年内不生成重复的 ID
     *
     * @return long 类型的 ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;

            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L; // 时间戳改变，毫秒内序列重置
        }

        // 上次生成 ID 的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间截
     * @return 当前时间戳(毫秒)
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    /**
     * 返回当前时间，以毫秒为单位
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Snowflake idWorker = new Snowflake(1, 0);
        MyThread t1 = new MyThread(idWorker);
        MyThread t2 = new MyThread(idWorker);
        MyThread t3 = new MyThread(idWorker);
        MyThread t4 = new MyThread(idWorker);
        MyThread t5 = new MyThread(idWorker);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }

}

class MyThread extends Thread{
    private Snowflake snowflake;

    public MyThread(Snowflake snowflake){
        this.snowflake = snowflake;
    }


    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            long id = snowflake.nextId();
            System.out.println(id + " : " + Thread.currentThread().getName());
        }
    }
}
