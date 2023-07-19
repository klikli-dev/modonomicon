/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.error;

import java.util.ArrayList;
import java.util.List;

public class BookErrorHolder {

    private final List<BookErrorInfo> errors = new ArrayList<>();

    public void addError(BookErrorInfo error) {
        this.errors.add(error);
    }

    public List<BookErrorInfo> getErrors() {
        return this.errors;
    }
}
