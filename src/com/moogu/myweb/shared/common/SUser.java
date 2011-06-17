package com.moogu.myweb.shared.common;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SUser implements Serializable, IsSerializable {

    private static final long serialVersionUID = 2479144520529183137L;

    public static final String ALL_USER_CODE = "ALL";

    private Integer id;

    private String code;

    private String name;

    public SUser() {
    }

    public SUser(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code + " : " + this.name;
    }

}