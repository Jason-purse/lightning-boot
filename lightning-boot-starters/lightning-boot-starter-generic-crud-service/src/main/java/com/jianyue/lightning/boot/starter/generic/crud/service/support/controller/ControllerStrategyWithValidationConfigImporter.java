package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller;

import com.jianyue.lightning.boot.starter.generic.crud.service.config.ControllerValidationAopAspectConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;
/**
 * @author FLJ
 * @date 2023/3/2
 * @time 11:28
 * @Description 导入ControllerValidationAopAspectConfiguration
 */
public class ControllerStrategyWithValidationConfigImporter implements DeferredImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{ControllerValidationAopAspectConfiguration.class.getName()};
    }
}
