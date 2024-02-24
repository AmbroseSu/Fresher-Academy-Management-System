package com.example.fams.converter;


import org.modelmapper.config.Configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class GenericConverter<T> {


    private final ModelMapper modelMapper;

    @Autowired
    public GenericConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.getConfiguration().setFieldMatchingEnabled(true) // Enable field matching -> MATCH những field có tên giống nhau
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE) // Set field access level
                .setAmbiguityIgnored(true) // Ignore ambiguity in property mapping
                .setSkipNullEnabled(false) // Allow mapping null values

        ;

    }


    /*
     * Hàm để updateEntity bằng Object DTO, và trả về loại Object của entity
     *Object DTO: Thông tin mà user muốn thay đổi
     *T entity: kiểu dữ liệu của Entity muốn trả về
     * */
    public T updateEntity(Object dto, T entity) {
        modelMapper.map(dto, entity);
        return entity;
    }

    /* Hàm để convert entity sang DTO
     *Class<T> dtoClass: kiểu dữ liệu của DTO muốn trả về
     *Object entity: Object chứa thông tin của entity để convert sang DTO
     * */
    public T toDTO(Object entity, Class<T> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    /* Hàm để convert  DTO sang entity
     *Class<T> entityClass: kiểu dữ liệu của entity muốn trả về
     *Object dto: Object chứa thông tin của DTO để convert sang entity
     * */
    public T toEntity(Object dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }


}
