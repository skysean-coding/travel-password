package com.skysean.travel.password.mysql.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.skysean.travel.password.encrypt.CipherService;
import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.mysql.model.CipherText;
import com.skysean.travel.password.mysql.model.Password;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skysean.travel.password.mysql.repository.CipherTextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

/**
 * 描述：密码服务类
 * @author skysean
 */
@Service
public class PasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordService.class);

    @Autowired
    private CipherService cipherService;

    @Autowired
    private CipherTextRepository cipherTextRepository;

    private Map<Integer, Password> cachedPasswords = new HashMap<>();

    @PostConstruct
    public void init(){
        refresh();
    }

    private void refresh(){
        Map<Integer, Password> cachePasswordMap = new HashMap<>();
        Iterable<CipherText> cipherTexts = cipherTextRepository.findAll();
        cipherTexts.forEach(cipherText -> {
            String content = cipherText.getContent();
            String decrypt = null;
            try {
                decrypt = cipherService.decrypt(content);
            } catch (Exception e) {
                LOGGER.error("刷新缓存密码失败, 原因：解密失败. 加密内容：{}", content, e);
            }

            if(null != decrypt){
                try {
                    Password password = JSON.parseObject(decrypt, Password.class);
                    password.setId(cipherText.getId());
                    cachePasswordMap.put(password.getId(), password);
                }catch (Exception e){
                    LOGGER.error("刷新缓存密码失败, 原因：反序列化失败. 加密内容：{}", content, e);
                }
            }
        });

        cachedPasswords = cachePasswordMap;
    }

    public List<CipherText> listAllCipherText(){
        return Lists.newArrayList(cipherTextRepository.findAll());
    }

    public List<Password> listPassword(String name){

        if(CollectionUtils.isEmpty(cachedPasswords)){
            return new ArrayList<>();
        }

        List<Password> results = new ArrayList<>();
        for(Password password : cachedPasswords.values()){
            if(null == name || "".equals(name.trim()) || containKeyword(name, password)){
                results.add(password);
            }
        }
        return results;
    }

    private boolean containKeyword(String name, Password password){
        if(null != password.getName() && password.getName().contains(name)){
            return true;
        }
        if(null != password.getUsername() && password.getUsername().contains(name)){
            return true;
        }
        if(null != password.getPassword() && password.getPassword().contains(name)){
            return true;
        }
        return false;
    }

    public void save(Password password){

        CipherText cipherText = buildCipherText(password);

        CipherText saved = cipherTextRepository.save(cipherText);

        cachedPasswords.put(saved.getId(), password);
    }

    private CipherText buildCipherText(Password password){
        String content = null;
        try {
            content = cipherService.encrypt(JSON.toJSONString(password));
        } catch (Exception e) {
            LOGGER.error("密码加密失败, password: {}", JSON.toJSONString(password), e);
            throw new ApplicationOperationException("密码加密失败");
        }

        if(null == content){
            throw new ApplicationOperationException("密码加密失败");
        }

        CipherText cipherText = new CipherText();
        cipherText.setId(password.getId());
        cipherText.setContent(content);

        return cipherText;
    }

    public void recover(List<CipherText> cipherTexts){
        cipherTextRepository.saveAll(cipherTexts);
        refresh();
    }

    public void remove(int id){
        cipherTextRepository.deleteById(id);
        cachedPasswords.remove(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<Password> passwords){

        List<CipherText> cipherTextList = new ArrayList<>();
        for(Password password : passwords){
            CipherText cipherText = buildCipherText(password);
            cipherTextList.add(cipherText);
        }

        Iterable<CipherText> savedCipherTexts = cipherTextRepository.saveAll(cipherTextList);

        Map<Integer, Password> savedPasswordMap = new HashMap<>();
        savedCipherTexts.forEach((ct)->{
            try {
                String decrypt = cipherService.decrypt(ct.getContent());
                Password password = JSON.parseObject(decrypt, Password.class);
                password.setId(ct.getId());
                savedPasswordMap.put(ct.getId(), password);
            }catch (Exception e){
                LOGGER.error("密码反序列化失败， 密码Id：{}，content: {}", ct.getId(), ct.getContent(), e);
                throw new ApplicationOperationException("密码反序列化失败！密码Id：" + ct.getId());
            }
        });

        cachedPasswords.putAll(savedPasswordMap);
    }
}
