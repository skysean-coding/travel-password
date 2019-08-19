package com.skysean.travel.password.encrypt;

/**
 * 描述：加密解密服务接口
 * @author skysean
 */
public interface CipherService {

    /**
     * 解密
     * @param encrypted 已加密文本
     * @return
     */
    String decrypt(String encrypted) throws Exception;

    /**
     * 加密
     * @param content 待加密文本
     * @return
     */
    String encrypt(String content) throws Exception;
}
