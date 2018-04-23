package com.liuyang.filesystem.memery;

import java.io.OutputStreamWriter;

import com.liuyang.filesystem.memery.MemeryFile.Mode;

public class MemeryFileWriter extends OutputStreamWriter {

	public MemeryFileWriter(String file, Mode mode) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		super(MemeryFileSystem.create(file, mode));
	}
	
	public MemeryFileWriter(String file, Mode mode, int size) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		super(MemeryFileSystem.create(file, mode, size));
	}
	
	public MemeryFileWriter(MemeryFile file, Mode mode) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		super(file.create(mode));
	}
	
	public MemeryFileWriter(MemeryFile file, Mode mode, int size) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		super(file.create(mode, size));
	}

}
