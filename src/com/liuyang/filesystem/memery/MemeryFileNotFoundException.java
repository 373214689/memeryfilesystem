/*
 * Copyright (c) 2007, 2009, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.liuyang.filesystem.memery;

public class MemeryFileNotFoundException extends MemeryFileSystemException{
	private static final long serialVersionUID = 420283040695071415L;

	/**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     */
	public MemeryFileNotFoundException(String file) {
		super(file);
	}

    /**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     * @param   other
     *          a string identifying the other file or {@code null} if not known
     * @param   reason
     *          a reason message with additional information or {@code null}
     */
    public MemeryFileNotFoundException(String file, String other, String reason) {
        super(file, other, reason);
    }
}
