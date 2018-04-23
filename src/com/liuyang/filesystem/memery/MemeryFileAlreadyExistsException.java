package com.liuyang.filesystem.memery;

public class MemeryFileAlreadyExistsException extends MemeryFileSystemException{
	private static final long serialVersionUID = 420283040695071415L;

	/**
     * Constructs an instance of this class.
     *
     * @param   file
     *          a string identifying the file or {@code null} if not known
     */
	public MemeryFileAlreadyExistsException(String file) {
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
    public MemeryFileAlreadyExistsException(String file, String other, String reason) {
        super(file, other, reason);
    }
}
