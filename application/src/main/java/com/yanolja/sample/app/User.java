package com.yanolja.sample.app;

import com.yanolja.annotation.sample.Dto;
import com.yanolja.sample.app.annotation.Entity;

@Dto
@Entity
public class User {
    private long id;
    private int age;
    private String name;
    private String address;
    private String job;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", job='" + job + '\'' +
                '}';
    }
}
