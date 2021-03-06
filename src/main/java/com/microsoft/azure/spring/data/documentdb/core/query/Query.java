/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

import java.util.LinkedHashMap;
import java.util.Map;

public class Query {

    private final Map<String, Object> criteria = new LinkedHashMap<>();

    public static Query query(CriteriaDefinition criteriaDefinition) {
        return new Query(criteriaDefinition);
    }

    public Query() {
    }

    public Query(CriteriaDefinition criteriaDefinition) {
        addCriteria(criteriaDefinition);
    }

    public Query addCriteria(CriteriaDefinition criteriaDefinition) {
        final Object existing = this.criteria.get(criteriaDefinition.getKey());

        if (existing == null) {
            this.criteria.put(criteriaDefinition.getKey(), criteriaDefinition.getCriteriaObject());
        } else {
            throw new RuntimeException("invalid criteriaDefinition!");
        }
        return this;
    }

    public Map<String, Object> getCriteria() {
        return this.criteria;
    }
}


