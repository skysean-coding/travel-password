package com.skysean.travel.password.operation;

import com.skysean.travel.password.mysql.model.Password;
import com.skysean.travel.password.mysql.service.PasswordService;
import com.skysean.travel.password.opt.GetOptUtil;
import com.skysean.travel.password.table.ConsoleTable;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 描述：密码查找命令
 * @author skysean
 */
@Service
public class FindPasswordOperation implements ApplicationOperation {

  @Autowired
  private PasswordService passwordService;

  @Override
  public String getCommand() {
    return "find";
  }

  @Override
  public String getCommandDescription() {
    return "查找密码，-k 密码名称关键字（不带参数表示列出所有密码）";
  }

  @Override
  public void execute(String args) {

    Map<Character, Object> paramMap = GetOptUtil.loadOpt(args, "k:");
    String keyword = MapUtils.getString(paramMap, 'k');

    List<Password> passwords =
        passwordService.listPassword(keyword);

    printPasswords(passwords);
  }

  private static void printPasswords(List<Password> passwords){
    if(CollectionUtils.isEmpty(passwords)){
      System.out.println("未查询到数据");
      return;
    }
    ConsoleTable.ConsoleTableBuilder consoleTableBuilder = new ConsoleTable.ConsoleTableBuilder()
        .addHeaders("id", "名称", "用户名", "密码");

    for(Password password : passwords){
      consoleTableBuilder.addRow("" + password.getId(), password.getName(), password.getUsername(), password.getPassword());
    }
    consoleTableBuilder.verticalSep("").joinSep("").build().print();
  }


}
