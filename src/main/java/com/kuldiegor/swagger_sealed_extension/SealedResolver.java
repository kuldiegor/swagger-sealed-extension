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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SealedResolver extends ModelResolver {
    public SealedResolver(ObjectMapper mapper, TypeNameResolver typeNameResolver) {
        super(mapper, typeNameResolver);
    }
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (type.isSchemaProperty()) {
            JavaType _type = Json.mapper().constructType(type.getType());
            if (_type != null) {
                Class<?> cls = _type.getRawClass();
                if (cls.isSealed()){
                    Schema resolved = new Schema<>().type("object");
                    super.resolveSchemaMembers(resolved,type);
                    List<Class<?>> permittedClassesList = new ArrayList<>();
                    findAllPermittedClasses(permittedClassesList,cls);
                    List<Schema> schemaList = permittedClassesList.stream()
                            .map(permittedCls -> context.resolve(new AnnotatedType().type(permittedCls).jsonViewAnnotation(type.getJsonViewAnnotation())))
                            .collect(Collectors.toList());

                    resolved.oneOf(schemaList);
                    return resolved;
                }
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }

    }

    public void findAllPermittedClasses(List<Class<?>> classList,Class<?> cls){
        Class<?>[] permittedSubclasses = cls.getPermittedSubclasses();
        if (permittedSubclasses==null){
            return;
        }
        for (Class<?> permittedSubclass : permittedSubclasses) {
            classList.add(permittedSubclass);
            findAllPermittedClasses(classList,permittedSubclass);
        }
    }

}
