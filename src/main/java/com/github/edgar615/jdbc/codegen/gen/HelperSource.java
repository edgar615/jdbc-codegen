package com.github.edgar615.jdbc.codegen.gen;

import com.github.edgar615.jdbc.codegen.db.Column;
import com.github.jknack.handlebars.Options;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelperSource {

    public CharSequence eq(Object v1, Object v2, Options options) throws IOException {
        if (v1 == null) {
            return options.inverse(this);
        }
        if (v2 == null) {
            return options.inverse(this);
        }
//    Object v2 = options.hash("v");
        if (v1.toString().equals(v2.toString())) {
            return options.fn(this);
        }
        return options.inverse(this);
    }

    public CharSequence parameterType(Object v1, Object v2, Options options) throws IOException {
        if (v1 == null) {
            return options.inverse(this);
        }
        if (v2 == null) {
            return options.inverse(this);
        }
        List<Column> columns = (List<Column>) v2;
        Optional<String> optional = columns.stream()
                .filter(c -> v1.toString().equals(c.getLowerCamelName()))
                .map(c -> c.getParameterType().getName())
                .findFirst();
        return optional.orElse("");
    }

    public CharSequence columnWithComma(Object v1, Options options) throws IOException {
        if (v1 == null) {
            return options.inverse(this);
        }
        List<Column> columns = (List<Column>) v1;
        List<String> columnWithComma = columns.stream().map(c -> c.getName()).collect(Collectors.toList());
        return Joiner.on(",").join(columnWithComma);
    }

    public CharSequence columnAnnotation(Object v1, Options options) throws IOException {
        if (v1 == null) {
            return options.inverse(this);
        }
        Column column = (Column) v1;
        List<String> columnAnnotations = new ArrayList<>();
        if (column.isPrimary()) {
            columnAnnotations.add("@PrimaryKey");
        }
        if (column.isVersion()) {
            columnAnnotations.add("@VersionKey");
        }
        if (column.isGenColumn()) {
            columnAnnotations.add("@VirtualKey");
        }
        return Joiner.on("\n").join(columnAnnotations);
    }

}