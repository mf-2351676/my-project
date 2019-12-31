import com.utils.RedisUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: menfeng
 * @Date: 2019/12/31 9:46
 * @Version 1.0
 */

public class test {

    @Test
    public void testRedis(){
        RedisUtils.getRedisUtil().set("aaaa","bbbb");
        Map<String, String> map = new HashMap<>();
        map.put("aa","1111");
        RedisUtils.getRedisUtil().hmset("123",map);
    }
}
