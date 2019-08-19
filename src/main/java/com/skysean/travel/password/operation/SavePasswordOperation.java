package com.skysean.travel.password.operation;

import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.mysql.model.Password;
import com.skysean.travel.password.mysql.service.PasswordService;
import com.skysean.travel.password.opt.GetOptUtil;
import java.util.Date;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：保存密码命令
 * @author skysean
 */
@Service
public class SavePasswordOperation implements ApplicationOperation{

  @Autowired
  private PasswordService passwordService;

  @Override
  public String getCommand() {
    return "save";
  }

  @Override
  public String getCommandDescription() {
    return "保存密码，-i 指定密码id，表示当前操作是一个更新操作. "
        + "-P 指定保存密码参数，用 | 隔开，格式：名称|用户名|密码";
  }

  @Override
  public void execute(String args) {

    Map<Character, Object> paramMap = GetOptUtil.loadOpt(args, "i:P:");
    int id = MapUtils.getIntValue(paramMap, 'i', 0);
    String params = MapUtils.getString(paramMap, 'P');

    if(null == params){
      throw new ApplicationOperationException("-P 参数值为空! 范例: 名称|用户名|密码");
    }

    String[] paramArray = params.split("\\|");
    if(paramArray.length != 3){
      throw new ApplicationOperationException("-P 参数值不合法! 范例: 名称|用户名|密码");
    }

    String name = paramArray[0];
    String username = paramArray[1].trim();
    String password = paramArray[2].trim();

    Password inputPassword = new Password();
    if(id > 0){
      inputPassword.setId(id);
    }else{
      inputPassword.setCreateTime(new Date());
    }
    inputPassword.setName(name);
    inputPassword.setUsername(username);
    inputPassword.setPassword(password);
    inputPassword.setUpdateTime(new Date());

    passwordService.save(inputPassword);
    System.out.println("保存密码成功!");
  }
}
