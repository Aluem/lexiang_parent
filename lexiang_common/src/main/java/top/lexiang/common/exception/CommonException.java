package top.lexiang.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.lexiang.common.entity.ResultCode;

/**
 * 自定义异常
 */
@Setter
@Getter
@NoArgsConstructor
public class CommonException extends Exception  {

    private ResultCode resultCode;

    public CommonException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
