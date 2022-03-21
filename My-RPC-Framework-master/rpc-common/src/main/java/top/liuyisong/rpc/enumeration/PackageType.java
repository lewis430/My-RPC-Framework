package top.liuyisong.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 在自定义协议中标识是请求包还是响应包
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
