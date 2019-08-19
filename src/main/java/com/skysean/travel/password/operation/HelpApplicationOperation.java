package com.skysean.travel.password.operation;

import com.skysean.travel.password.table.ConsoleTable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 描述：帮助命令
 * @author skysean
 */
@Service
public class HelpApplicationOperation implements ApplicationOperation{

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public String getCommand() {
    return "help";
  }

  @Override
  public String getCommandDescription() {
    return null;
  }

  @Override
  public void execute(String args) {

    Map<String, ApplicationOperation> beans = applicationContext
        .getBeansOfType(ApplicationOperation.class);

    ConsoleTable.ConsoleTableBuilder consoleTableBuilder = new ConsoleTable.ConsoleTableBuilder()
        .addHeaders("命令", "描述");
    for(ApplicationOperation bean : beans.values()){
      if(bean instanceof HelpApplicationOperation){
        continue;
      }
      consoleTableBuilder.addRow(bean.getCommand(), bean.getCommandDescription());
    }

    consoleTableBuilder.verticalSep("").joinSep("").build().print();
  }

}
