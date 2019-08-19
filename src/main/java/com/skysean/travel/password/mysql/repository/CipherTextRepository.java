package com.skysean.travel.password.mysql.repository;

import com.skysean.travel.password.mysql.model.CipherText;
import org.springframework.data.repository.CrudRepository;

/**
 * 描述：加密文本数据库访问
 * @author skysean
 */
public interface CipherTextRepository extends CrudRepository<CipherText, Integer> {

}
