/*
 * LGPL-3.0
 *
 * Copyright (C) 2022 klikli-dev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.klikli_dev.modonomicon.book.error;

public class BookErrorInfo {
    private final String errorMessage;
    private final Exception exception;
    private final String context;

    public BookErrorInfo(String errorMessage, Exception exception, String context) {
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.context = context;
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
                "\n}";
    }
}
