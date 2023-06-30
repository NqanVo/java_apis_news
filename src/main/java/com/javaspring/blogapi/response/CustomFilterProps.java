package com.javaspring.blogapi.response;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustomFilterProps {
    public CustomFilterProps() {
    }

    // * Custom lại đầu ra các props, không bị cố định
    public MappingJacksonValue mappingJacksonValue(Object rawProps, Set<String> props, String jsonFilter, Boolean returnFullProps) {
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(rawProps);
        // * Mặc định lấy tất cả props
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
        // * Nếu lấy props theo yêu cầu
        if (!returnFullProps) {
            filter = SimpleBeanPropertyFilter.filterOutAllExcept(props);
        }
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(jsonFilter, filter);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }
}
