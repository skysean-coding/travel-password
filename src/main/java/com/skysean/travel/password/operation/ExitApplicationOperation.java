package com.skysean.travel.password.operation;

import org.springframework.stereotype.Service;

/**
 * 描述：退出命令
 * @author skysean
 */
@Service
public class ExitApplicationOperation implements ApplicationOperation{

  @Override
  public String getCommand() {
    return "exit";
  }

  @Override
  public String getCommandDescription() {
    return "退出程序";
  }

  @Override
  public void execute(String args) {
    System.out.println("bye!");
    System.exit(0);
  }
}
