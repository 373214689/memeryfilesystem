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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.liuyang.filesystem.memery.MemeryFile.Mode;

public class MemeryFileSystem {
	private static Map<String, MemeryFile> map = new HashMap<String, MemeryFile>();
    
	
	public static OutputStream append(String filepath) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		return MemeryFileSystem.create(filepath, Mode.APPEND);
	}
	
	public static void append(String filepath, String str) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		MemeryFileWriter mos = null;
		try {
			mos = new MemeryFileWriter(filepath, Mode.APPEND);
			mos.write(str);
			mos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mos = null;
		}
	}
	
	public static void appendToLocalFile(String localfile, String str) {
		File file = new File(localfile);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fw = null;
		}
	}
	
	public static OutputStream create(String filepath, MemeryFile.Mode mode) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		MemeryFile memFile = null;
		if (map.containsKey(filepath)) {
			memFile = map.get(filepath);
		} else {
			memFile = new MemeryFile(filepath);
			map.put(filepath, memFile);
		}
		return memFile.create(mode);
	}
	
	
	public static OutputStream create(String filepath, MemeryFile.Mode mode, int size) 
			throws MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException 
	{
		MemeryFile memFile = null;
		if (map.containsKey(filepath)) {
			memFile = map.get(filepath);
		} else {
			memFile = new MemeryFile(filepath);
			map.put(filepath, memFile);
		}
		return memFile.create(mode, size);
	}
	
	public static void loadLocalFile(String filepath, MemeryFile.Mode mode, File localpath) 
			throws FileNotFoundException 
	{
		if (!localpath.exists()) throw new FileNotFoundException(localpath + " not found.");
		int size = (int) localpath.length();
	    try {
	    	MemeryFileSystem.loadFromInputStream(filepath, mode, size,  new FileInputStream(localpath));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void loadLocalFile(String filepath, MemeryFile.Mode mode, String localpath) 
			throws FileNotFoundException 
	{
		MemeryFileSystem.loadLocalFile(filepath, mode, new File(localpath));
	}
	
	public static void loadFromInputStream(String filepath, MemeryFile.Mode mode, int size, InputStream is) {
	    byte[] buf = new byte[4096];
	    int len = 0;
	    OutputStream mos = null;
	    try {
	    	mos = MemeryFileSystem.create(filepath, mode, size);
			while((len = is.read(buf, 0, buf.length)) != -1) {
				mos.write(buf, 0, len);
			}
			mos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			buf = null;
			mos = null;
		}
	}
	
	public static InputStream open(String filepath) 
			throws MemeryFileNotFoundException, MemeryFileNotCreatedException 
    {
		if (!map.containsKey(filepath)) throw new MemeryFileNotFoundException(filepath + " not found.");
		return map.get(filepath).open();
	}
	
	public static boolean delete(String filepath) {
		if (!map.containsKey(filepath)) return false;
		return map.remove(filepath).delete();
	}
	
	public static boolean exists(String filepath) {
		return map.containsKey(filepath);
	}
	
	public static long length(String filepath) 
			throws MemeryFileNotFoundException 
	{
		if (!map.containsKey(filepath)) throw new MemeryFileNotFoundException(filepath + " not found.");
		return map.get(filepath).length();
	}
	
	public static MemeryFile get(String filepath) throws MemeryFileNotFoundException {
		if (!map.containsKey(filepath)) throw new MemeryFileNotFoundException(filepath + " not found.");
		return  map.get(filepath);
	}
}
