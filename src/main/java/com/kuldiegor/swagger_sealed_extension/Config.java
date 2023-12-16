/*
    Copyright 2023 Dmitrij Kulabuhov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.kuldiegor.swagger_sealed_extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    private void configureModules(SealedResolver sealedResolver) {
        if (sealedResolver==null){
            ObjectMapper objectMapper = Json.mapper();
            sealedResolver = new SealedResolver(objectMapper,new QualifiedTypeNameResolver());
        }
        ModelConverters modelConverters = ModelConverters.getInstance();
        modelConverters.addConverter(sealedResolver);
    }

    @Bean
    @ConditionalOnBean(value = {OpenAPI.class, SealedResolver.class})
    public EmptyBean configureModulesWithOpenAPIAndSealedResolver(SealedResolver sealedResolver){
        configureModules(sealedResolver);
        return EmptyBean.OpenAPIAndSealedResolver;
    }

    @Bean
    @ConditionalOnBean(value = {OpenAPI.class})
    @ConditionalOnMissingBean(value = {SealedResolver.class})
    public EmptyBean configureModulesWithOpenAPIAndWithoutSealedResolver(){
        configureModules(null);
        return EmptyBean.OpenAPIAndWithoutSealedResolver;
    }

    @Bean
    @ConditionalOnBean(value = {SealedResolver.class})
    @ConditionalOnMissingBean(value = {OpenAPI.class})
    public EmptyBean configureModulesWithSealedResolverAndWithoutOpenAPI(SealedResolver sealedResolver){
        configureModules(sealedResolver);
        return EmptyBean.SealedResolverAndWithoutOpenAPI;
    }

    @Bean
    @ConditionalOnMissingBean(value = {OpenAPI.class, SealedResolver.class})
    public EmptyBean configureModulesWithoutOpenAPIAndSealedResolver(){
        configureModules(null);
        return EmptyBean.WithoutOpenAPIAndSealedResolver;
    }

}
