package com.skysean.travel.password.operation;

import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.opt.GetOptUtil;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

/**
 * 描述：生成随机密码命令
 *
 * @author skysean
 */
@Service
public class GeneratePasswordOperation implements ApplicationOperation {

    private static final char[] NORMAL_CHAR_ARRAY = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] SPECIFIC_SYMBOL_ARRAY = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ@#!$%*&/".toCharArray();
    private static final int DEFAULT_RANDOM_LEN = 12;

    public String randomPassword(int len, boolean includeSpecificSymbol) {

        char[] chars = includeSpecificSymbol ? SPECIFIC_SYMBOL_ARRAY : NORMAL_CHAR_ARRAY;
        StringBuilder passwordBuilder = new StringBuilder();
        int randomBound = chars.length;
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < len; i++) {
            passwordBuilder.append(chars[secureRandom.nextInt(randomBound)]);
        }

        return passwordBuilder.toString();
    }

    @Override
    public String getCommand() {
        return "gen";
    }

    @Override
    public String getCommandDescription() {
        return "生成随机密码, -n 指定密码长度（默认12位） -s 指定是否包含特殊字符（默认不包含）";
    }

    @Override
    public void execute(String args) {
        Map<Character, Object> paramMap = GetOptUtil.loadOpt(args, "n:s");

        int num = MapUtils.getIntValue(paramMap, 'n', DEFAULT_RANDOM_LEN);
        if (num < 1) {
            throw new ApplicationOperationException("-n value is invalid! please enter a natural number greater than 0");
        }
        boolean hasSpecific = paramMap.containsKey('s');
        String password = randomPassword(num, hasSpecific);
        System.out.println("GeneratePassword: " + password);
    }
}
