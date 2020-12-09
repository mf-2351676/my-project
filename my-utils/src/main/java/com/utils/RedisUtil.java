package com.utils;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@Slf4j
public class RedisUtil {

    private RedisUtil() {
    }

    protected final static ThreadLocal<Jedis> threadLocalJedis = new ThreadLocal<Jedis>();

    //Redis服务器IP
    private static String ADDR_ARRAY = ReadProperties.ReadProperties("redis.properties").getString("redis.host");

    //Redis的端口号
    private static int PORT = ReadProperties.ReadProperties("redis.properties").getInt("redis.port");

    //访问密码
    private static String AUTH = ReadProperties.ReadProperties("redis.properties").getString("redis.password");

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = ReadProperties.ReadProperties("redis.properties").getInt("redis.maxTotal");

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = ReadProperties.ReadProperties("redis.properties").getInt("redis.maxIdle");

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static Long MAX_WAIT = ReadProperties.ReadProperties("redis.properties").getLong("redis.maxWaitMillis");

    //超时时间
    private static int TIMEOUT = ReadProperties.ReadProperties("redis.properties").getInt("redis.timeOut");

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = ReadProperties.ReadProperties("redis.properties").getBoolean("redis.testOnBorrow");

    private static JedisPool jedisPool;

    /**
     * 初始化Redis连接池,注意一定要在使用前初始化一次,一般在项目启动时初始化就行了
     */
    private static JedisPool initialPool() {
        JedisPool jp = null;
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            //config.setMaxTotal(ReadProperties.ReadProperties("redis.properties").getInt("redis.maxTotal"));
            config.setMaxTotal(MAX_ACTIVE);
            //config.setMaxIdle(ReadProperties.ReadProperties("redis.properties").getInt("redis.maxIdle"));
            config.setMaxIdle(MAX_IDLE);
            // config.setMaxWaitMillis(ReadProperties.ReadProperties("redis.properties").getLong("redis.maxWaitMillis"));
            config.setMaxWaitMillis(MAX_WAIT);
            //config.setTestOnBorrow(ReadProperties.ReadProperties("redis.properties").getBoolean("redis.testOnBorrow"));
            config.setTestOnBorrow(TEST_ON_BORROW);
            //if(!StrUtil.isBlank(ReadProperties.ReadProperties("redis.properties").getString("redis.password"))){
            if (!StrUtil.isBlank(AUTH)) {
                //jp = new JedisPool(config, ReadProperties.ReadProperties("redis.properties").getString("redis.host"), ReadProperties.ReadProperties("redis.properties").getInt("redis.port"), ReadProperties.ReadProperties("redis.properties").getInt("redis.timeOut"), ReadProperties.ReadProperties("redis.properties").getString("redis.password"));
                jp = new JedisPool(config, ADDR_ARRAY, PORT, TIMEOUT, AUTH);
            } else {
                //jp = new JedisPool(config, ReadProperties.ReadProperties(redis.properties").getString("redis.host"), ReadProperties.ReadProperties("redis.properties").getInt("redis.port"), ReadProperties.ReadProperties("redis.properties").getInt("redis.timeOut"));
                jp = new JedisPool(config, ADDR_ARRAY, PORT, TIMEOUT);
            }
            jedisPool = jp;
            //threadLocalJedis =  new ThreadLocal<Jedis>();
            threadLocalJedis.set(getJedis());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("redis服务器异常", e);

        }
        return jp;
    }


    public static void close(Jedis jedis) {
        if (threadLocalJedis.get() == null && jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取Jedis实例,一定先初始化
     *
     * @return Jedis
     */
    private static Jedis getJedis() {
        boolean success = false;
        Jedis jedis = null;
        int i = 0;
        while (!success) {
            i++;
            try {
                if (jedisPool != null) {
                    jedis = threadLocalJedis.get();
                    if (jedis == null) {
                        jedis = jedisPool.getResource();
                    } else {
                        if (!jedis.isConnected() && !jedis.getClient().isBroken()) {
                            threadLocalJedis.set(null);
                            jedis = jedisPool.getResource();
                        }
                        return jedis;
                    }
                } else {
                    throw new RuntimeException("redis连接池初始化失败");
                }
            } catch (Exception e) {

                System.out.println(Thread.currentThread().getName() + ":第" + i + "次获取失败!!!");
                success = false;
                e.printStackTrace();
                log.error("redis服务器异常", e);
            }
            if (jedis != null) {
                success = true;
            }

            if (i >= 10 && i < 20) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (i >= 20 && i < 30) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (i >= 30 && i < 40) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (i >= 40) {
                System.out.println("redis彻底连不上了~~~~(>_<)~~~~");
                return null;
            }

        }
        if (threadLocalJedis.get() == null) {
            threadLocalJedis.set(jedis);
        }
        return jedis;
    }


    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public static String setString(String key, String value) {
        Jedis jo = null;
        try {
            value = StrUtil.isBlank(value) ? "" : value;
            jo = getJedis();
            return jo.set(key, value);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis服务器异常");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }
    }

    /**
     * 设置 过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     * @param value
     */
    public static String setString(String key, int seconds, String value) {
        Jedis jo = null;
        try {
            value = StrUtil.isBlank(value) ? "" : value;
            jo = getJedis();
            return jo.setex(key, seconds, value);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis服务器异常");
        } finally {
            if (jo != null) {
                close(jo);

            }
        }
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public static String getString(String key) {
        Jedis jo = null;

        try {
            jo = getJedis();
            if (jo == null || !jo.exists(key)) {
                return null;
            }
            return jo.get(key);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }


    }

    public static long incrBy(String key, long integer) {
        Jedis jo = null;
        try {
            jo = getJedis();

            return jo.incrBy(key, integer);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }

    public static long decrBy(String key, long integer) {
        Jedis jo = null;
        try {
            jo = getJedis();
            return jo.decrBy(key, integer);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }

    //删除多个key
    public static long delKeys(String[] keys) {
        Jedis jo = null;
        try {
            jo = getJedis();
            return jo.del(keys);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }

    //删除单个key
    public static long delKey(String key) {
        Jedis jo = null;
        try {
            jo = getJedis();
            return jo.del(key);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }

    //添加到队列尾
    public static long rpush(String key, String node) {
        Jedis jo = null;
        try {
            jo = getJedis();
            return jo.rpush(key, node);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }


    //删除list元素
    public static long delListNode(String key, int count, String value) {
        Jedis jo = null;
        try {
            jo = getJedis();
            return jo.lrem(key, count, value);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }

    }


    //获取所有list

    public static List getListAll(String key) {
        Jedis jo = null;
        List list = null;
        try {
            jo = getJedis();
            list = jo.lrange(key, 0, -1);
        } catch (Exception e) {
            threadLocalJedis.set(null);
            e.printStackTrace();
            log.error("redis服务器异常", e);
            throw new RuntimeException("redis操作错误");
        } finally {
            if (jo != null) {
                close(jo);
            }
        }
        return list;
    }

    //清理缓存redis
    public static void cleanLoacl(Jedis jo) {
        threadLocalJedis.set(null);
        close(jo);
    }

    static {
        initialPool();
    }
}
