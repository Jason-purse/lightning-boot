package com.jianyue.lightning.boot.starter.generic.crud.service.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class ClassPathScanningCandidateComponentScanner extends ClassPathScanningCandidateComponentProvider  {
    private Set<String> basePackages;

    private TypeFilter typeFilter;

    public ClassPathScanningCandidateComponentScanner(@NotNull Set<String> basePackages, @NotNull Class<?> assignClazz, @Nullable Class<? extends Annotation> annotationClazz) {
        super(false);
        Assert.notNull(basePackages,"basePackages must not be null");
        Assert.notNull(assignClazz,"assign clazz must not be null");
        this.basePackages = basePackages;
        this.typeFilter = new CustomForParamTypeFilter(assignClazz,annotationClazz);
    }

    public void setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
    }

    public void setTypeFilter(TypeFilter typeFilter) {
        Assert.notNull(typeFilter,"typeFilter must not be null");
        this.typeFilter = typeFilter;
    }

    @Override
    protected boolean isCandidateComponent(@NotNull MetadataReader metadataReader) throws IOException {
        return typeFilter.match(metadataReader,getMetadataReaderFactory());
    }



    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
       return  !beanDefinition.isAbstract() && !beanDefinition.getMetadata().isInterface();
    }

    class CustomForParamTypeFilter implements TypeFilter {
        private final AssignableTypeFilter assignableTypeFilter ;
        private final TypeFilter annotationTypeFilter;

        public CustomForParamTypeFilter(Class<?> clazz) {
            this(clazz,null);
        }

        public CustomForParamTypeFilter(Class<?> clazz,@Nullable Class<? extends Annotation> annotationClazz) {
            this.assignableTypeFilter = new AssignableTypeFilter(clazz);
            if(annotationClazz != null) {
                this.annotationTypeFilter = new AnnotationTypeFilter(annotationClazz,true);
            }
            else {
                this.annotationTypeFilter = (metadataReader, metadataReaderFactory) -> true;
            }
        }

        @Override
        public boolean match(@NotNull MetadataReader metadataReader, @NotNull MetadataReaderFactory metadataReaderFactory) throws IOException {
            var state = false;
            for (String packageName:  basePackages) {
                String className = metadataReader.getClassMetadata().getClassName();
                if (className.startsWith(packageName + ".model.params")) {
                    state = true;
                    break;
                }
            }

            if(state) {
                if (!assignableTypeFilter.match(metadataReader,metadataReaderFactory)) {
                    return false;
                }

                if(!annotationTypeFilter.match(metadataReader,metadataReaderFactory)) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}
