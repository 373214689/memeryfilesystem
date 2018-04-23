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

/**
 * Checked exception thrown when a file system operation is denied, typically
 * due to a file permission or other access check.
 *
 * <p> This exception is not related to the {@link
 * java.security.AccessControlException AccessControlException} or {@link
 * SecurityException} thrown by access controllers or security managers when
 * access to a file is denied.
 *
 * @since 1.7
 */
public class MemeryFileAccessDeniedException extends MemeryFileSystemException {
	private static final long serialVersionUID = 347889997704742805L;

    /**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     */
	public MemeryFileAccessDeniedException(String file) {
		super(file);
		// TODO Auto-generated constructor stub
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
    public MemeryFileAccessDeniedException(String file, String other, String reason) {
        super(file, other, reason);
    }

}
