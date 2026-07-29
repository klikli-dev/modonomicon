/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.book.error;

public class BookErrorInfo {
    private final String errorMessage;
    private final Exception exception;
    private final String context;
    private final boolean blocksOpening;

    public BookErrorInfo(String errorMessage, Exception exception, String context) {
        this(errorMessage, exception, context, true);
    }

    public BookErrorInfo(String errorMessage, Exception exception, String context, boolean blocksOpening) {
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.context = context;
        this.blocksOpening = blocksOpening;
    }

    public boolean blocksOpening() {
        return this.blocksOpening;
    }

    @Override
    public String toString() {
        var errorMessage = this.errorMessage == null ? "" : this.errorMessage;
        var context = this.context == null ? "" : this.context;
        var exception = this.exception == null ? "" : this.exception.toString();
        return "BookErrorInfo{ " +
                "\nerrorMessage='" + errorMessage + "'" +
                ", \ncontext='" + context + "'" +
                ", \nexception='" + exception + "'" +
                ", \nblocksOpening='" + this.blocksOpening + "'" +
                "\n}";
    }
}
