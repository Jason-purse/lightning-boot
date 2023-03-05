package com.jianyue.lightning.boot.starter.generic.crud.service.model.params;

import com.jianyue.lightning.boot.starter.generic.crud.service.annotations.ParamStrategy;

@ParamStrategy("complex")
public class ComplexOrganizationParam implements OrganizationParam {
    private String username;

    private String password;
}
