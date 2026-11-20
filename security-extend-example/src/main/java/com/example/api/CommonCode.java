package com.example.api;

import java.io.Serializable;


/**
 * @author yangpan
 */


public interface CommonCode extends Serializable {

    /**
    * @Description: 获取返回码
    * @Param: []
    * @return: int
    * @Author: mal
    * @Date: 2021/9/27
    */
    int getCode();

    /**
    * @Description: 获取返回信息
    * @Param: []
    * @return: java.lang.String
    * @Author: mal
    * @Date: 2021/9/27
    */
    String getMessage();
}
