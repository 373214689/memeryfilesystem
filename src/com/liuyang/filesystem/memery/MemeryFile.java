package com.liuyang.filesystem.memery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MemeryFile {
    private String path;
    private byte[] data;
    
    private boolean writing = false;
    
    public MemeryFile(String filepath) {
    	this.path = filepath;
    	this.data = null;
    }
    
    protected void finalize() {
    	path = null;
    	data = null;
    }
    
    public OutputStream create(Mode mode) 
    		throws  MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException
    {
    	if (writing) throw new MemeryFileAccessDeniedException(path + " access denied.");
    	return new MemeryFileOutputStream(mode);
    }
    
    public OutputStream create(Mode mode, int size) 
    		throws  MemeryFileAccessDeniedException, MemeryFileAlreadyExistsException
    {
    	if (writing) throw new MemeryFileAccessDeniedException(path + " access denied.");
    	return new MemeryFileOutputStream(mode, size);
    }
    
    public InputStream open() 
    		throws MemeryFileNotCreatedException
    {
    	if (data == null) throw new MemeryFileNotCreatedException(path + " not created.");
    	//return new ByteArrayInputStream(data);
    	return new MemeryFileInputStream();
    }
    
    
    public boolean delete() {
    	data = new byte[] {};
    	path = null;
    	return true;
    }
    
    public boolean exists() {
    	return data != null;
    }
    
    public long length() {
    	if (data == null) return -1;
    	return data.length;
    }
    
    public enum Mode {
    	APPEND,
    	CREATE,
    	OVERWRITE
    }
    
    
    
    private class MemeryFileOutputStream extends OutputStream {
        /**
         * The number of valid bytes in the buffer.
         */
        protected int count;
        
        /**
         * The maximum size of array to allocate.
         * Some VMs reserve some header words in an array.
         * Attempts to allocate larger arrays may result in
         * OutOfMemoryError: Requested array size exceeds VM limit
         */
        private final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
        
    	public MemeryFileOutputStream(Mode mode) 
    			throws MemeryFileAlreadyExistsException 
    	{
    		this(mode, 32);
    	}
    	
        public MemeryFileOutputStream(Mode mode, int size) 
        		throws MemeryFileAlreadyExistsException
        {
        	
            if (size < 0) {
                throw new IllegalArgumentException("Negative initial size: "
                                                   + size);
            }
            switch (mode) {
	        	case APPEND: 
	        		if (data != null) {
	        			count = data.length;
	        		} else {
	        			data = new byte[size];
	        		}
	        		writing = true;
	        		break;
	        	case CREATE: 
	        		if (data != null) {
	        		    throw new MemeryFileAlreadyExistsException(path + " is already exists"); 
	        	    } else {
	        	    	writing = true;
	        	    	data = new byte[size];
	        	    }
	        		break;
	        	case OVERWRITE: 
	        		writing = true;
	        		data = new byte[size];
	        		break;
        	}
        }

        private synchronized void ensureCapacity(int minCapacity) {
            // overflow-conscious code
            if (minCapacity - data.length > 0)
                grow(minCapacity);
        }
        
        /**
         * Increases the capacity to ensure that it can hold at least the
         * number of elements specified by the minimum capacity argument.
         *
         * @param minCapacity the desired minimum capacity
         */
        private synchronized void grow(int minCapacity) {
            // overflow-conscious code
            int oldCapacity = data.length;
            int newCapacity = oldCapacity << 1;
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                newCapacity = hugeCapacity(minCapacity);
            data = Arrays.copyOf(data, newCapacity);
        }
        
        private int hugeCapacity(int minCapacity) {
            if (minCapacity < 0) // overflow
                throw new OutOfMemoryError();
            return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
        }
        
        /**
         * Writes the specified byte to this byte array output stream.
         *
         * @param   b   the byte to be written.
         */
        @Override
        public synchronized void write(int b) {
        	synchronized(data) {
                ensureCapacity(count + 1);
                data[count] = (byte) b;
                count += 1;
        	}
        }
        

        /**
         * Writes <code>len</code> bytes from the specified byte array
         * starting at offset <code>off</code> to this byte array output stream.
         *
         * @param   b     the data.
         * @param   off   the start offset in the data.
         * @param   len   the number of bytes to write.
         */
        @Override
        public synchronized void write(byte b[], int off, int len) {
            if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
                throw new IndexOutOfBoundsException();
            }
            ensureCapacity(count + len);
            System.arraycopy(b, off, data, count, len);
            count += len;
        }
        
        /**
         * Writes the complete contents of this byte array output stream to
         * the specified output stream argument, as if by calling the output
         * stream's write method using <code>out.write(buf, 0, count)</code>.
         *
         * @param      out   the output stream to which to write the data.
         * @exception  IOException  if an I/O error occurs.
         */
        public synchronized void writeTo(OutputStream out) throws IOException {
            out.write(data, 0, count);
        }
        
        /**
         * Resets the <code>count</code> field of this byte array output
         * stream to zero, so that all currently accumulated output in the
         * output stream is discarded. The output stream can be used again,
         * reusing the already allocated buffer space.
         *
         * @see     java.io.ByteArrayInputStream#count
         */
        public synchronized void reset() {
            count = 0;
        }
        
        /**
         * Returns the current size of the buffer.
         *
         * @return  the value of the <code>count</code> field, which is the number
         *          of valid bytes in this output stream.
         * @see     java.io.ByteArrayOutputStream#count
         */
        public synchronized int size() {
            return count;
        }
        
        /**
         * Converts the buffer's contents into a string decoding bytes using the
         * platform's default character set. The length of the new <tt>String</tt>
         * is a function of the character set, and hence may not be equal to the
         * size of the buffer.
         *
         * <p> This method always replaces malformed-input and unmappable-character
         * sequences with the default replacement string for the platform's
         * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
         * class should be used when more control over the decoding process is
         * required.
         *
         * @return String decoded from the buffer's contents.
         * @since  JDK1.1
         */
        public synchronized String toString() {
            return new String(data, 0, count);
        }
        
        /**
         * Converts the buffer's contents into a string by decoding the bytes using
         * the named {@link java.nio.charset.Charset charset}. The length of the new
         * <tt>String</tt> is a function of the charset, and hence may not be equal
         * to the length of the byte array.
         *
         * <p> This method always replaces malformed-input and unmappable-character
         * sequences with this charset's default replacement string. The {@link
         * java.nio.charset.CharsetDecoder} class should be used when more control
         * over the decoding process is required.
         *
         * @param      charsetName  the name of a supported
         *             {@link java.nio.charset.Charset charset}
         * @return     String decoded from the buffer's contents.
         * @exception  UnsupportedEncodingException
         *             If the named charset is not supported
         * @since      JDK1.1
         */
        public synchronized String toString(String charsetName)
            throws UnsupportedEncodingException
        {
            return new String(data, 0, count, charsetName);
        }
        
        /**
         * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
         * this class can be called after the stream has been closed without
         * generating an <tt>IOException</tt>.
         */
        public void close() throws IOException {
        	writing = false;
        }
    }
    
    private class MemeryFileInputStream extends InputStream {

        /**
         * The index of the next character to read from the input stream buffer.
         * This value should always be nonnegative
         * and not larger than the value of <code>count</code>.
         * The next byte to be read from the input stream buffer
         * will be <code>buf[pos]</code>.
         */
        protected int pos;
        
        /**
         * The currently marked position in the stream.
         * ByteArrayInputStream objects are marked at position zero by
         * default when constructed.  They may be marked at another
         * position within the buffer by the <code>mark()</code> method.
         * The current buffer position is set to this point by the
         * <code>reset()</code> method.
         * <p>
         * If no mark has been set, then the value of mark is the offset
         * passed to the constructor (or 0 if the offset was not supplied).
         *
         * @since   JDK1.1
         */
        protected int mark = 0;
        
        /**
         * The index one greater than the last valid character in the input
         * stream buffer.
         * This value should always be nonnegative
         * and not larger than the length of <code>buf</code>.
         * It  is one greater than the position of
         * the last byte within <code>buf</code> that
         * can ever be read  from the input stream buffer.
         */
        protected int count;
        
        /**
         * Creates a <code>MemeryFileInputStream</code>
         * so that it  uses <code>buf</code> as its
         * buffer array.
         * The buffer array is not copied.
         * The initial value of <code>pos</code>
         * is <code>0</code> and the initial value
         * of  <code>count</code> is the length of
         * <code>buf</code>.
         *
         * @param   buf   the input buffer.
         */
        public MemeryFileInputStream() {
            this.pos = 0;
            this.count = data.length;
        }
        
        /**
         * Creates <code>MemeryFileInputStream</code>
         * that uses <code>buf</code> as its
         * buffer array. The initial value of <code>pos</code>
         * is <code>offset</code> and the initial value
         * of <code>count</code> is the minimum of <code>offset+length</code>
         * and <code>buf.length</code>.
         * The buffer array is not copied. The buffer's mark is
         * set to the specified offset.
         *
         * @param   buf      the input buffer.
         * @param   offset   the offset in the buffer of the first byte to read.
         * @param   length   the maximum number of bytes to read from the buffer.
         */
        public MemeryFileInputStream(int offset, int length) {
            this.pos = offset;
            this.count = Math.min(offset + length, data.length);
            this.mark = offset;
        }
        
        /**
         * Reads the next byte of data from this input stream. The value
         * byte is returned as an <code>int</code> in the range
         * <code>0</code> to <code>255</code>. If no byte is available
         * because the end of the stream has been reached, the value
         * <code>-1</code> is returned.
         * <p>
         * This <code>read</code> method
         * cannot block.
         *
         * @return  the next byte of data, or <code>-1</code> if the end of the
         *          stream has been reached.
         */
        public synchronized int read() {
            return (pos < count) ? (data[pos++] & 0xff) : -1;
        }
    	
        /**
         * Reads up to <code>len</code> bytes of data into an array of bytes
         * from this input stream.
         * If <code>pos</code> equals <code>count</code>,
         * then <code>-1</code> is returned to indicate
         * end of file. Otherwise, the  number <code>k</code>
         * of bytes read is equal to the smaller of
         * <code>len</code> and <code>count-pos</code>.
         * If <code>k</code> is positive, then bytes
         * <code>buf[pos]</code> through <code>buf[pos+k-1]</code>
         * are copied into <code>b[off]</code>  through
         * <code>b[off+k-1]</code> in the manner performed
         * by <code>System.arraycopy</code>. The
         * value <code>k</code> is added into <code>pos</code>
         * and <code>k</code> is returned.
         * <p>
         * This <code>read</code> method cannot block.
         *
         * @param   b     the buffer into which the data is read.
         * @param   off   the start offset in the destination array <code>b</code>
         * @param   len   the maximum number of bytes read.
         * @return  the total number of bytes read into the buffer, or
         *          <code>-1</code> if there is no more data because the end of
         *          the stream has been reached.
         * @exception  NullPointerException If <code>b</code> is <code>null</code>.
         * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
         * <code>len</code> is negative, or <code>len</code> is greater than
         * <code>b.length - off</code>
         */
        public synchronized int read(byte b[], int off, int len) {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }

            if (pos >= count) {
                return -1;
            }

            int avail = count - pos;
            if (len > avail) {
                len = avail;
            }
            if (len <= 0) {
                return 0;
            }
            System.arraycopy(data, pos, b, off, len);
            pos += len;
            return len;
        }
        
        /**
         * Skips <code>n</code> bytes of input from this input stream. Fewer
         * bytes might be skipped if the end of the input stream is reached.
         * The actual number <code>k</code>
         * of bytes to be skipped is equal to the smaller
         * of <code>n</code> and  <code>count-pos</code>.
         * The value <code>k</code> is added into <code>pos</code>
         * and <code>k</code> is returned.
         *
         * @param   n   the number of bytes to be skipped.
         * @return  the actual number of bytes skipped.
         */
        public synchronized long skip(long n) {
            long k = count - pos;
            if (n < k) {
                k = n < 0 ? 0 : n;
            }

            pos += k;
            return k;
        }
        
        /**
         * Returns the number of remaining bytes that can be read (or skipped over)
         * from this input stream.
         * <p>
         * The value returned is <code>count&nbsp;- pos</code>,
         * which is the number of bytes remaining to be read from the input buffer.
         *
         * @return  the number of remaining bytes that can be read (or skipped
         *          over) from this input stream without blocking.
         */
        public synchronized int available() {
            return count - pos;
        }

        /**
         * Tests if this <code>InputStream</code> supports mark/reset. The
         * <code>markSupported</code> method of <code>ByteArrayInputStream</code>
         * always returns <code>true</code>.
         *
         * @since   JDK1.1
         */
        public boolean markSupported() {
            return true;
        }

        /**
         * Set the current marked position in the stream.
         * ByteArrayInputStream objects are marked at position zero by
         * default when constructed.  They may be marked at another
         * position within the buffer by this method.
         * <p>
         * If no mark has been set, then the value of the mark is the
         * offset passed to the constructor (or 0 if the offset was not
         * supplied).
         *
         * <p> Note: The <code>readAheadLimit</code> for this class
         *  has no meaning.
         *
         * @since   JDK1.1
         */
        public void mark(int readAheadLimit) {
            mark = pos;
        }

        /**
         * Resets the buffer to the marked position.  The marked position
         * is 0 unless another position was marked or an offset was specified
         * in the constructor.
         */
        public synchronized void reset() {
            pos = mark;
        }

        /**
         * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
         * this class can be called after the stream has been closed without
         * generating an <tt>IOException</tt>.
         */
        public void close() throws IOException {
        }
    }
}
