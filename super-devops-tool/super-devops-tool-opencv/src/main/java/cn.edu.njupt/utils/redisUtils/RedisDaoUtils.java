package cn.edu.njupt.utils.redisUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDaoUtils {
    //日志
    private static final Logger logger = (Logger) LoggerFactory.getLogger(RedisDaoUtils.class);

    private final JedisPool jedisPool;

    private final String IP = "127.0.0.1";
    private final int PORT = 6379;

    public RedisDaoUtils(){
        jedisPool = new JedisPool(IP , PORT);
    }

    public RedisDaoUtils(String ip , int port){
        jedisPool = new JedisPool(ip , port);
    }

    /**
     * 获取ocr识别数据
     * @param key
     * @return
     */
    public String getOCRData(String key){
        String result = "";
        try{
            Jedis jedis = jedisPool.getResource();
            try{
                result = jedis.get(key);
            }catch (Exception e){
                logger.error("Jedis获取数据发生异常: " + e);
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error("JedisPool中获取连接发生异常: " + e);
        }
        return result;
    }

    public static void main(String[] args) {
        String ip = "10.166.33.86";
        int port = 6379;
        RedisDaoUtils utils = new RedisDaoUtils(ip , port);
        String data = utils.getOCRData("RegResult:p4");
        System.out.println(data);
    }
}
