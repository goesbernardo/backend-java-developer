package com.cmanager.app.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Util {

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static Pageable getPageable(int page, int size, String sortField, String sortOrder) {
        final var sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return PageRequest.of(page, size, sort);
    }
}
