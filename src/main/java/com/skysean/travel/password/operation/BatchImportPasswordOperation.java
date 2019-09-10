package com.skysean.travel.password.operation;

import com.google.common.io.Files;
import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.mysql.model.Password;
import com.skysean.travel.password.mysql.service.PasswordService;
import com.skysean.travel.password.opt.GetOptUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：批量导入密码
 * @author skysean
 */
@Service
public class BatchImportPasswordOperation implements ApplicationOperation{

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchImportPasswordOperation.class);

  @Autowired
  private PasswordService passwordService;

  @Override
  public String getCommand() {
    return "import";
  }

  @Override
  public String getCommandDescription() {
    return "批量导入密码，-p 指定密码的文件地址. 格式：名称|用户名|密码";
  }

  @Override
  public void execute(String args) {

    Map<Character, Object> paramMap = GetOptUtil.loadOpt(args, "p:");
    String path = MapUtils.getString(paramMap, 'p');
    if(null == path){
      throw new ApplicationOperationException("批量导入密码时，请使用 -p <path> 指定密码文件地址");
    }

    List<String> passwordStrList = null;
    try {
      passwordStrList = Files.readLines(new File(path), Charset.forName("utf-8"));
    } catch (IOException e) {
      LOGGER.error("导入密码读取文件失败，文件地址：{}", path, e);
      throw new ApplicationOperationException("导入密码读取文件失败，请检查！文件地址：" + path);
    }

    if(CollectionUtils.isEmpty(passwordStrList)){
      throw new ApplicationOperationException("待导入的密码为空，文件地址：" + path);
    }

    int index = 0;
    List<Password> passwords = new ArrayList<>();
    for(String passwordStr : passwordStrList){

      String[] paramArray = passwordStr.split("\\|");
      if(paramArray.length != 3){
        throw new ApplicationOperationException("数据不合法! 范例: 名称|用户名|密码， 行号：" + (index + 1));
      }

      String name = paramArray[0];
      String username = paramArray[1].trim();
      String password = paramArray[2].trim();

      Password inputPassword = new Password();
      inputPassword.setCreateTime(new Date());
      inputPassword.setName(name);
      inputPassword.setUsername(username);
      inputPassword.setPassword(password);
      inputPassword.setUpdateTime(new Date());

      passwords.add(inputPassword);
      index ++;
    }

    passwordService.batchInsert(passwords);

    System.out.println("批量保存密码成功，数量：" + passwords.size());
  }
}
