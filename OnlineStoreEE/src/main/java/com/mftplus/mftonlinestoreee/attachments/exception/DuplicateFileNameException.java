package com.mftplus.mftonlinestoreee.attachments.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateFileNameException extends BusinessException {

    public DuplicateFileNameException(String fileName) {
        super("This owner already has an attachment with file name '" + fileName + "'");
    }
}
