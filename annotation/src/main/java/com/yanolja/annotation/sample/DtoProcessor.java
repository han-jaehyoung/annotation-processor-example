package com.yanolja.annotation.sample;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class DtoProcessor extends AbstractProcessor
{
    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> set = new HashSet<>();
        set.add(Dto.class.getName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<? extends Element> elemenets = roundEnv.getElementsAnnotatedWith(Dto.class);
        List<FieldSpec> fieldSpecList = new ArrayList<>();
        List<MethodSpec> methodSpecList = new ArrayList<>();

        for (Element element : elemenets)
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "dto class make... " + element.getSimpleName() + "Dto.java");

            TypeElement typeElement = (TypeElement) element;

            for (Element field : typeElement.getEnclosedElements())
            {
                if (field.getKind() == ElementKind.FIELD)
                {
                    String fieldNm = field.getSimpleName().toString();
                    TypeName fieldTypeName = TypeName.get(field.asType());

                    FieldSpec fieldSpec = FieldSpec.builder(fieldTypeName, fieldNm)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                    fieldSpecList.add(fieldSpec);

                    String methodNm = String.format("get%s", StringUtils.capitalize(fieldNm));

                    MethodSpec methodSpec = MethodSpec.methodBuilder(methodNm)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(fieldTypeName)
                        .addStatement("return " + fieldNm)
                        .build();

                    methodSpecList.add(methodSpec);
                }
            }
            ClassName className = ClassName.get(typeElement);
            String dtoClassName = String.format("%sDto", className.simpleName());

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(makeCode(fieldSpecList))
                    .addParameters(toParameterSpec(fieldSpecList))
                    .build();
            methodSpecList.add(constructor);

            TypeSpec dtoClass = TypeSpec.classBuilder(dtoClassName)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecList)
                .addMethods(methodSpecList)
                .build();

            try
            {
                JavaFile.builder(className.packageName(), dtoClass)
                    .build()
                    .writeTo(processingEnv.getFiler());
            }
            catch (IOException e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR : " + e);
            }
        }

        return true;
    }

    private List<ParameterSpec> toParameterSpec(List<FieldSpec> fieldSpecs) {
        return fieldSpecs.stream().map(field -> ParameterSpec.builder(field.type, field.name).build()).collect(Collectors.toList());
    }

    private CodeBlock makeCode(List<FieldSpec> fieldSpecs) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        fieldSpecs.forEach(fieldSpec -> codeBlockBuilder.addStatement("this."+ fieldSpec.name + " = " + fieldSpec.name));
        return codeBlockBuilder.build();
    }
}
