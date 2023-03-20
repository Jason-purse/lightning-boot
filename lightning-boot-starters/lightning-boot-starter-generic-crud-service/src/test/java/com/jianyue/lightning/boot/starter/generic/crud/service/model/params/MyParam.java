package com.jianyue.lightning.boot.starter.generic.crud.service.model.params;

import com.jianyue.lightning.boot.starter.generic.crud.service.annotations.ParamStrategy;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import lombok.Data;

@Data
@ParamStrategy
public class MyParam implements Param {
    private String username;

    private String password;
}
