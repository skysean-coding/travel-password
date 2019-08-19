package com.skysean.travel.password.operation;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.mysql.model.CipherText;
import com.skysean.travel.password.mysql.service.PasswordService;
import com.skysean.travel.password.opt.GetOptUtil;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述：密码持久化命令
 * 保存密码到本地，从本地恢复密码到服务器
 * @author skysean
 */
@Service
public class PersistPasswordOperation implements ApplicationOperation{

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistPasswordOperation.class);

    private static final String DEFAULT_PASSWORD_FILE = "password";

    @Autowired
    private PasswordService passwordService;

    @Override
    public String getCommand() {
        return "persist";
    }

    @Override
    public String getCommandDescription() {
        return "持久化服务器密码到本地, -r 表示当前操作是从本地的恢复密码到服务器 -p 指定密码文件的本地路径(默认为程序同级目录,名称为password) ";
    }

    @Override
    public void execute(String args) {

        Map<Character, Object> paramMap = GetOptUtil.loadOpt(args, "p:r");

        boolean isRecover = paramMap.containsKey('r');
        String path = MapUtils.getString(paramMap, 'p', DEFAULT_PASSWORD_FILE);

        File file = new File(path);
        if(isRecover){
            recover(file);
            return;
        }

        persist(file);
    }

    private void persist(File file){
        List<CipherText> cipherTexts = passwordService.listAllCipherText();
        if(CollectionUtils.isEmpty(cipherTexts)){
            throw new ApplicationOperationException("数据库尚未保存密码, 不需要持久化");
        }
        StringBuilder passwordStrBuilder = new StringBuilder();
        for(CipherText cipherText : cipherTexts){
            passwordStrBuilder.append(JSON.toJSONString(cipherText) + "\n");
        }

        try {
            Files.write(passwordStrBuilder.toString().getBytes(), file);
        } catch (Exception e) {
            LOGGER.error("持久化到本地失败, 文件地址： {}", file.getAbsolutePath(), e);
            throw new ApplicationOperationException("持久化到本地失败, 请检查日志文件查看详细错误");
        }

        System.out.println("持久化密码到本地成功, 本地文件地址：" + file.getAbsolutePath());
    }

    private void recover(File file){

        if(!file.exists()){
            throw new ApplicationOperationException("通过 -r 恢复密码失败, 本地密码文件不存在, 文件地址：" + file.getAbsolutePath());
        }

        if(!file.isFile()){
            throw new ApplicationOperationException("通过 -r 恢复密码时, -p 参数后跟随的必须是已经存在的文件");
        }

        List<String> cipherTextJsonList = readFile(file);
        if(CollectionUtils.isEmpty(cipherTextJsonList)){
            throw new ApplicationOperationException("密码文件为空, 无法继续执行恢复操作, 文件地址：" + file.getAbsolutePath());
        }

        List<CipherText> cipherTexts = new ArrayList<>();
        int index = 1;
        for(String cipherTextJson : cipherTextJsonList){
            try{
                CipherText cipherText = JSON.parseObject(cipherTextJson, CipherText.class);
                cipherTexts.add(cipherText);
            }catch (Exception e){
                //考虑到一般来说不会有人去改这个文件，所以这里直接抛出异常，保证数据的完整性
                throw new ApplicationOperationException("密码文件解析失败, 行：" + index);
            }
            index ++;
        }

        try{
            passwordService.recover(cipherTexts);
        }catch (Exception e){
            LOGGER.error("恢复密码失败, 文件地址： {}", file.getAbsolutePath(), e);
            throw new ApplicationOperationException("恢复密码失败, 请检查日志文件查看详细错误");
        }

        System.out.println("恢复密码成功, 文件地址：" + file.getAbsolutePath());
    }

    private List<String> readFile(File file){
        try {
            return Files.readLines(file, Charset.forName("utf-8"));
        } catch (Exception e) {
            throw new ApplicationOperationException("读取本地密码文件失败, 文件地址：" + file.getAbsolutePath());
        }
    }

}
