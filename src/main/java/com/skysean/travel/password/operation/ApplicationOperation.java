package com.skysean.travel.password.operation;

/**
 * 描述：应用操作接口
 * @author skysean
 */
public interface ApplicationOperation {

  /**
   * 获取命令
   * @return
   */
  String getCommand();

  /**
   * 命令描述
   * @return
   */
  String getCommandDescription();

  /**
   * 执行命令
   * @param args 命令后带的参数
   */
  void execute(String args);

}
