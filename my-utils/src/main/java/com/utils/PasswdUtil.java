package com.utils;

import org.apache.shiro.crypto.hash.ConfigurableHashService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.util.ByteSource;

/**
 * @Author: menfeng
 * @Date: 2020/7/8 16:26
 * @Version 1.0
 */
public class PasswdUtil {

    private static final String staticSalt = "menfeng";
    private static final String algorithmName = "MD5";

    public static String getPwd(String passwd,String userName){
        ConfigurableHashService hashService = new DefaultHashService();
        hashService.setPrivateSalt(ByteSource.Util.bytes(staticSalt));
        hashService.setHashAlgorithmName(algorithmName);
        hashService.setHashIterations(2);
        HashRequest request = new HashRequest.Builder()
                .setSalt(userName)
                .setSource(passwd)
                .build();
        String res =  hashService.computeHash(request).toHex();
        return res;
    }
}
