/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the Apache 2.0 License.
 * See the accompanying LICENSE file for terms.
 */
package com.yahoo.squidb.sql;

import com.yahoo.squidb.data.ViewModel;

import java.util.List;

/**
 * A table represented by a subquery
 */
public class SubqueryTable extends QueryTable {

    private SubqueryTable(Class<? extends ViewModel> modelClass, Property<?>[] properties, String name, Query query) {
        super(modelClass, properties, name, query);
    }

    /**
     * Construct a new SubqueryTable backed by the given Query and aliased to the given name
     *
     * @param query the backing Query
     * @param name the name for the table
     * @return a new SubqueryTable
     */
    public static SubqueryTable fromQuery(Query query, String name) {
        return new SubqueryTable(null, null, name, query);
    }

    /**
     * Construct a new SubqueryTable backed by the given Query and aliased to the given name and associate it with the
     * given ViewModel
     *
     * @param query the backing Query
     * @param name the name for the table
     * @param modelClass the ViewModel to associate
     * @return a new SubqueryTable
     */
    public static SubqueryTable fromQuery(Query query, String name, Class<? extends ViewModel> modelClass,
            Property<?>[] properties) {
        return new SubqueryTable(modelClass, properties, name, query);
    }

    @Override
    void appendCompiledStringWithArguments(StringBuilder sql, List<Object> selectionArgsBuilder) {
        appendCompiledStringWithArguments(sql, selectionArgsBuilder, false);
    }

    void appendCompiledStringWithArguments(StringBuilder sql, List<Object> selectionArgsBuilder,
            boolean withValidation) {
        sql.append("(");
        query.appendCompiledStringWithArguments(sql, selectionArgsBuilder, withValidation);
        sql.append(") AS ").append(getName());
    }

}
