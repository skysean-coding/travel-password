package com.skysean.travel.password.mysql.model;

import javax.persistence.*;

/**
 * 描述：加密文本实体
 * @author skysean
 */
@Entity
@Table(name = "cipher_text")
public class CipherText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
