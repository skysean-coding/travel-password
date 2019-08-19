package com.skysean.travel.password.encrypt;

import com.skysean.travel.password.utils.AES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 描述：AES加密
 * @author skysean
 */
@Service
public class AesCipherService implements CipherService{

    @Value("${travel.password.aes.key}")
    private String aesKey;

    @Value(("${travel.password.aes.iv}"))
    private String aesIv;

    @Override
    public String decrypt(String encrypted) throws Exception {
        return AES.decrypt(encrypted, aesKey, aesIv);
    }

    @Override
    public String encrypt(String content) throws Exception {
        return AES.encrypt(content, aesKey, aesIv);
    }
}
