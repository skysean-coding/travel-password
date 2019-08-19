package com.skysean.travel.password.opt;

import com.google.common.base.Splitter;
import gnu.getopt.Getopt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;

/**
 * 描述：GUN opt命令工具类
 * @author skysean
 */
public class GetOptUtil {

  public static Map<Character, Object> loadOpt(String argStr, String opts){

    Map<Character, Object> params = new HashMap<>();

    if(null == argStr){
      return params;
    }

    String[] args = convertToArray(argStr);
    if(null == args || args.length == 0){
      return params;
    }

    Getopt getOpt = new Getopt("get-opt", args, opts);
    int c;
    while ((c = getOpt.getopt()) != -1) {
      params.put((char)c, getOpt.getOptarg());
    }

    return params;
  }

  private static String [] convertToArray(String argStr){
    List<String> strings = Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(argStr);
    if(CollectionUtils.isEmpty(strings)){
      return new String[0];
    }

    return strings.toArray(new String[strings.size()]);
  }

    public static void main(String [] args){
    String str = "-a asdfa -b 111 -d 222 333             -c 1237 -e bdaasdfas                -v";
      Map<Character, Object> characterObjectMap = GetOptUtil.loadOpt(str, "a:b:c:d:e:v");

      for(Map.Entry<Character, Object> characterObjectEntry : characterObjectMap.entrySet()){
        System.out.println("key: " + characterObjectEntry.getKey() + ", value: " + characterObjectEntry.getValue());
      }
    }
}
