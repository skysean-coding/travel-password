package com.skysean.travel.password;

import com.skysean.travel.password.exception.ApplicationOperationException;
import com.skysean.travel.password.operation.ApplicationOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 描述：spring boot启动类
 * @author skysean
 */
@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        //加载容器
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        //根据类型加载所有spring容器中命令
        Map<String, ApplicationOperation> applicationOperations = context
            .getBeansOfType(ApplicationOperation.class);

        //根据命令名称分类
        Map<String, ApplicationOperation> commandOperationMapping = new HashMap<>();
        for(ApplicationOperation applicationOperation : applicationOperations.values()){
            if(commandOperationMapping.containsKey(applicationOperation.getCommand())){
                throw new ApplicationOperationException("command: "+applicationOperation.getCommand()+" is exists");
            }
            commandOperationMapping.put(applicationOperation.getCommand(), applicationOperation);
        }

        //监听输入并执行命令
        Scanner scanner = new Scanner(System.in);

        commandOperationMapping.get("help").execute(null);
        printShell();

        while (scanner.hasNextLine()){

            String cmd = scanner.nextLine();
            String[] commandAndArgs = splitCommandAndArgs(cmd);
            ApplicationOperation applicationOperation = commandOperationMapping.get(commandAndArgs[0]);
            if(null != applicationOperation){
                try{
                    applicationOperation.execute(commandAndArgs[1]);
                }catch (ApplicationOperationException e){
                    System.err.println(e.getMessage());
                } catch (Exception e){
                    LOGGER.error("命令执行失败, 完整命令：{}", cmd, e);
                }
            }

            printShell();
        }
    }

    private static void printShell(){
        System.out.println();
        System.out.print("_>");
    }

    private static String [] splitCommandAndArgs(String cmd){
        int spaceIndex = cmd.indexOf(" ");
        if (-1 == spaceIndex) {
            return new String[]{cmd, null};
        }

        return new String[] {cmd.substring(0, spaceIndex), cmd.substring(spaceIndex)};
    }

}
