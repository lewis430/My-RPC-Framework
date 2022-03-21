package top.liuyisong.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试用api的实体
 */
@Data//get和set的方法
@NoArgsConstructor//没有参数的构造器
@AllArgsConstructor//全参数的构造器
public class HelloObject implements Serializable {

    private Integer id;
    private String message;

}
