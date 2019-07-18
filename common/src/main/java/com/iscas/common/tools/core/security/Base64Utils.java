
package com.iscas.common.tools.core.security;


import com.Ostermiller.util.Base64;

import java.io.*;

/**
 *<p>
 *     调用了com.Ostermiller.util.Base64，将所有函数作了中文注释<br/>
 *     encode 和decode方法 ，encodeToXX,decodeToXX都可以用，有些功能是重复的
 *<p/>

 * @author zhuquanwen
 * @version 1.0
 * @since jdk1.8
 **/
public final class Base64Utils {


	private Base64Utils() {
	}

	/**
	 * 对一个字符串进行编码，使用默认的本地编码格式
	 * 	 					，不插入换行符或其他空格
	 * @author zhuquanwen
	 * @date 2018/7/13
	 * @param string 待编码的字符串
	 * @return java.lang.String 编码后的字符串
	 */
	public static String encode(String string) {
		return Base64.encode(string);
	}

	/**
	 *
	 *  对一个字符串进行编码，使用默认的本地编码格式
	 * 	 				，不插入换行符或其他空格
	 *@author zhuquanwen
	 *@date 2018/7/13
	 *@param string 待编码的字符串
	 *@param enc 编码格式(utf-8等)
	 *@throws UnsupportedEncodingException 不支持的编码格式
	 *@return java.lang.String 编码后的字符串
	 */
	public static String encode(String string, String enc) throws UnsupportedEncodingException {
		return Base64.encode(string, enc);
	}

	/**
	 * 将一个字节数组编码为字符串，不插入换行空格等字符
	 *@author zhuquanwen
	 *@date 2018/7/13
	 *@param bytes 待编码的字节数组
	 *@return java.lang.String 编码后的字符串
	 */
	public static String encodeToString(byte[] bytes) {
		return Base64.encodeToString(bytes);
	}

	/**
	 * 将字节数组编码为字符串
	 *@date 2018/7/13
	 *@param bytes 待编码的字节数组
	 *@param lineBreaks 是否在输出中每隔76个字符插入换行符
	 *@return java.lang.String 编码结果字符串
	 */
	public static String encodeToString(byte[] bytes, boolean lineBreaks) {
		return Base64.encodeToString(bytes, lineBreaks);
	}
	/**
	 * 对字节数组编码
	 * @date 2018/7/13
	 * @param bytes 待编码字节数组
	 * @return byte[] 编码结果
	 */
	public static byte[] encode(byte[] bytes) {
		return Base64.encode(bytes);
	}

	/**
	 * 对字节数组进行base64编码
	 * @date 2018/7/13
	 * @param bytes 待编码字节数组
	 * @param lineBreaks 是否每76字符插入换行符
	 * @return byte[] 编码结果
	 */
	public static byte[] encode(byte[] bytes, boolean lineBreaks) {
		return Base64.encode(bytes, lineBreaks);
	}

	/**
	 * 对文件进行base64编码，默认每隔76字符插入换行符
	 * @date 2018/7/13
	 * @param fIn 文件对象{@link File}，默认覆盖原来文件
	 * @throws IOException IO异常
	 * @see #encode(File, boolean)
	 */
	public static void encode(File fIn) throws IOException {
		Base64.encode(fIn);
	}

	/**
	 * 对文件进行base64编码
	 * @date 2018/7/13
	 * @param fIn 文件对象{@link File}，默认覆盖原来文件
	 * @param lineBreaks 是狗每隔76字符插入换行符
	 * @throws IOException IO异常
	 */
	public static void encode(File fIn, boolean lineBreaks) throws IOException {
		Base64.encode(fIn, lineBreaks);
	}


	/**
	 *  对文件编码，输入另一个文件，默认不插入换行符
	 * @date 2018/7/13
	 * @param fIn {@link File} 输入文件
	 * @param fOut {@link File} 输出文件
	 * @see #encode(File, File, boolean)
	 */
	public static void encode(File fIn, File fOut) throws IOException {
		Base64.encode(fIn, fOut);
	}

	/**
	 * 对文件编码，输入另一个文件，默认不插入换行符
	 * @date 2018/7/13
	 * @param fIn {@link File} 输入文件
	 * @param fOut {@link File} 输出文件
	 * @param lineBreaks 是否每隔76字符插入换行符
	 * @see #encode(File, File, boolean)
	 */
	public static void encode(File fIn, File fOut, boolean lineBreaks) throws IOException {
		Base64.encode(fIn, fOut, lineBreaks);
	}

	/**
	 * @对文件编码，输入另一个文件流，输入一个文件流，默认不插入换行符
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @param out {@link OutputStream} 输出流
	 * @see #encode(InputStream, OutputStream, boolean)
	 */
	public static void encode(InputStream in, OutputStream out) throws IOException {
		Base64.encode(in, out);
	}

	/**
	 * 对文件编码，输入另一个文件流，输入一个文件流，可以插入换行符
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @param out {@link OutputStream} 输出流
	 * @param lineBreaks 是否每隔76字符插入换行符
	 */
	public static void encode(InputStream in, OutputStream out, boolean lineBreaks) throws IOException {
		Base64.encode(in, out, lineBreaks);
	}

	/**
	 * @base64 解码，默认使用本地编码格式，忽略不在编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @return java.lang.String 解码结果字符串
	 * @see #decode(String, String)
	 */
	public static String decode(String string) {
		return Base64.decode(string);
	}

	/**
	 * base64 解码，指定编码格式，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param enc 编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return java.lang.String 解码结果字符串
	 * @see #decode(String, String, String)
	 */
	public static String decode(String string, String enc) throws UnsupportedEncodingException {
		return Base64.decode(string, enc);
	}

	/**
	 * base64 解码，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param encIn 输入编码格式
	 * @param encIn 输出编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return java.lang.String 解码结果字符串
	 */
	public static String decode(String string, String encIn, String encOut) throws UnsupportedEncodingException {
		return Base64.decode(string, encIn, encOut);
	}

	/**
	 * base64 解码，默认使用本地编码格式，忽略不在编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @return java.lang.String 解码结果字符串
	 */
	public static String decodeToString(String string) {
		return Base64.decodeToString(string);
	}

	/**
	 * base64 解码，指定编码格式，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param enc 编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return java.lang.String 解码结果字符串
	 */
	public static String decodeToString(String string, String enc) throws UnsupportedEncodingException {
		return Base64.decodeToString(string, enc);
	}

	/**
	 * base64 解码，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param encIn 输入编码格式
	 * @param encIn 输出编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return java.lang.String 解码结果字符串
	 */
	public static String decodeToString(String string, String encIn, String encOut) throws UnsupportedEncodingException {
		return Base64.decodeToString(string, encIn, encOut);
	}

	/**
	 * base64 解码，默认使用本地编码格式，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param out {@link OutputStream} 输出流
	 * @throws IOException IO异常
	 */
	public static void decodeToStream(String string, OutputStream out) throws IOException {
		Base64.decodeToStream(string, out);
	}

	/**
	 * base64 解码，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param enc 编码格式
	 * @param out {@link OutputStream} 输出流
	 * @throws IOException IO异常
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 */
	public static void decodeToStream(String string, String enc, OutputStream out) throws UnsupportedEncodingException, IOException {
		Base64.decodeToStream(string, enc, out);
	}

	/**
	 * base64 解码为字节数组，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @return byte[] 结果字节数组
	 */
	public static byte[] decodeToBytes(String string) {
		return Base64.decodeToBytes(string);
	}

	/**
	 * base64 解码为字节数组，忽略不再编码表的字符，默认使用本地编码格式
	 * @date 2018/7/13
	 * @param string 待解码字符串
	 * @param enc 编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return byte[] 结果字节数组
	 */
	public static byte[] decodeToBytes(String string, String enc) throws UnsupportedEncodingException {
		return Base64.decodeToBytes(string, enc);
	}

	/**
	 *  base64 解码为字符串，忽略不再编码表的字符，默认使用本地编码格式
	 * @date 2018/7/13
	 * @param bytes 待解码的字节数组
	 * @return String 结果字符串
	 */
	public static String decodeToString(byte[] bytes) {
		return Base64.decodeToString(bytes);
	}

	/**
	 * base64 解码为字符串，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param bytes 待解码字符数组
	 * @param enc 编码格式
	 * @throws UnsupportedEncodingException 不支持的编码格式
	 * @return String 结果字符串
	 */
	public static String decodeToString(byte[] bytes, String enc) throws UnsupportedEncodingException {
		return Base64.decodeToString(bytes, enc);
	}

	/**
	 *  base64 解码为字节数组，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param bytes 待解码的字节数组
	 * @return byte[] 结果字节数组
	 */
	public static byte[] decodeToBytes(byte[] bytes) {
		return Base64.decodeToBytes(bytes);
	}

	/**
	 *  base64 解码为字节数组，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param bytes 待解码的字节数组
	 * @return byte[] 结果字节数组
	 */
	public static byte[] decode(byte[] bytes) {
		return Base64.decode(bytes);
	}

	/**
	 *  base64 解码为输出流，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param bytes 待解码的字节数组
	 * @param out {@link OutputStream} 输出流
	 * @throws IOException IO异常
	 */
	public static void decode(byte[] bytes, OutputStream out) throws IOException {
		Base64.decode(bytes, out);
	}

	/**
	 * base64 解码为输出流，忽略不再编码表的字符
	 * @date 2018/7/13
	 * @param bytes 待解码的字节数组
	 * @param out {@link OutputStream} 输出流
	 * @throws IOException IO异常
	 */
	public static void decodeToStream(byte[] bytes, OutputStream out) throws IOException {
		Base64.decodeToStream(bytes, out);
	}

	/**
	 *  base64 解码文件，默认覆盖自己
	 * @date 2018/7/13
	 * @param fIn {@link File} 待解码的文件
	 * @throws IOException IO异常
	 */
	public static void decode(File fIn) throws IOException {
		Base64.decode(fIn);
	}

	/**
	 *  base64 解码文件，默认覆盖自己
	 * @date 2018/7/13
	 * @param fIn {@link File} 待解码的文件
	 * @param throwExceptions  出现异常是否抛出
	 * @throws IOException IO异常
	 */
	public static void decode(File fIn, boolean throwExceptions) throws IOException {
		Base64.decode(fIn, throwExceptions);
	}

	/**
	 *  base64 解码文件
	 * @date 2018/7/13
	 * @param fIn {@link File} 待解码的文件
	 * @param fOut {@link File} 输出文件
	 * @throws IOException IO异常
	 */
	public static void decode(File fIn, File fOut) throws IOException {
		Base64.decode(fIn, fOut);
	}

	/**
	 *  base64 解码文件
	 * @date 2018/7/13
	 * @param fIn {@link File} 待解码的文件
	 * @param fOut {@link File} 输出文件
	 * @param throwExceptions 出现异常是否抛出
	 * @throws IOException IO异常
	 */
	public static void decode(File fIn, File fOut, boolean throwExceptions) throws IOException {
		Base64.decode(fIn, fOut, throwExceptions);
	}


	/**
	 * 从输入流解码
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @throws IOException IO异常
	 * @return byte[] 结果字节数组
	 */
	public static byte[] decodeToBytes(InputStream in) throws IOException {
		return Base64.decodeToBytes(in);
	}

	/**
	 * 从输入流解码，默认编码格式
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @throws IOException IO异常
	 * @return String 结果字符串
	 */
	public static String decodeToString(InputStream in) throws IOException {
		return Base64.decodeToString(in);
	}

	/**
	 *  从输入流解码
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @param enc 编码格式
	 * @throws IOException IO异常
	 * @return String 结果字符串
	 */
	public static String decodeToString(InputStream in, String enc) throws IOException {
		return Base64.decodeToString(in, enc);
	}

	/**
	 *  从输入流解码为输出流
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @param out {@link OutputStream} 输入流
	 * @throws IOException IO异常
	 */
	public static void decode(InputStream in, OutputStream out) throws IOException {
		Base64.decode(in, out);
	}

	/**
	 *  从输入流解码为输出流
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入流
	 * @param out {@link OutputStream} 输入流
	 * @param throwExceptions 出现异常是否抛出
	 * @throws IOException IO异常
	 */
	public static void decode(InputStream in, OutputStream out, boolean throwExceptions) throws IOException {
		 Base64.decode(in, out, throwExceptions);

	}

	/**
	 *  bytes 数组是否为base64编码格式
	 * @date 2018/7/13
	 * @param bytes 输入的判断字节数组
	 * @return boolean
	 */
	public static boolean isBase64(byte[] bytes) {
		return Base64.isBase64(bytes);
	}

	/**
	 *  bytes 数组是否为base64编码格式
	 * @date 2018/7/13
	 * @param string 输入的判断字符串
	 * @return boolean
	 */
	public static boolean isBase64(String string) {
		return Base64.isBase64(string);
	}

	/**
	 *  bytes 数组是否为base64编码格式， 指定编码格式
	 * @date 2018/7/13
	 * @param string 输入的判断字符串
	 * @param enc 编码格式
	 * @return boolean
	 */
	public static boolean isBase64(String string, String enc) throws UnsupportedEncodingException {
		return Base64.isBase64(string, enc);
	}

	/**
	 *  文件是否为base64编码格式
	 * @date 2018/7/13
	 * @param fIn {@link File} 输入的判断文件
	 * @throws IOException IO异常
	 * @return boolean
	 */
	public static boolean isBase64(File fIn) throws IOException {
		return Base64.isBase64(fIn);
	}

	/**
	 *  输入流是否为base64编码格式
	 * @date 2018/7/13
	 * @param in {@link InputStream} 输入的判断输入流
	 * @throws IOException IO异常
	 * @return boolean
	 */
	public static boolean isBase64(InputStream in) throws IOException {
		return Base64.isBase64(in);
	}
}

