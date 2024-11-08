package com.dg.store.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringToListIntegerConverter implements Converter<String, List<Integer>> {
    @Override
    public List<Integer> convert(String source) {
        // 假设source是"[1,2,3]"这样的字符串
        source = source.trim();
        if (source.startsWith("[") && source.endsWith("]")) {
            source = source.substring(1, source.length() - 1); // 移除首尾的[]
        }
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}

