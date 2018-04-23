package com.liuyang.filesystem.memery;

import java.io.InputStreamReader;

public class MemeryFileReader extends InputStreamReader {
	
	public MemeryFileReader(String filepath) 
			throws MemeryFileNotCreatedException, MemeryFileNotFoundException 
	{
		super(MemeryFileSystem.get(filepath).open());
	}
	
	public MemeryFileReader(MemeryFile file) 
			throws MemeryFileNotCreatedException 
	{
		super(file.open());
	}
}
