package com.github.markojevtic.restfulapi.resource;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.Field;

@Component
public class HateaosSwaggerPatch implements ModelPropertyBuilderPlugin {

    @Override
    public boolean supports(final DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(final ModelPropertyContext context) {
        if (context.getBeanPropertyDefinition().isPresent()) {
            BeanPropertyDefinition b = context.getBeanPropertyDefinition().get();
            AnnotatedField annotatedField = b.getField();
            if (null != annotatedField) {
                Field field = (Field) annotatedField.getMember();
                if (field != null)
                    hideLinksField(context, field);
            }
        } else if (context.getAnnotatedElement().isPresent() && context.getAnnotatedElement().get() instanceof Field) {
            Field f = (Field) context.getAnnotatedElement().get();
            hideLinksField(context, f);
        }
    }

    private void hideLinksField(final ModelPropertyContext context, final Field field) {
        if (ResourceSupport.class.equals(field.getDeclaringClass()) && field.getName().contains("links")) {
            context.getBuilder().isHidden(true);
        }
    }
}
