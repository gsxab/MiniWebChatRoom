/**
 * 
 */
package log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志用类.
 * 日志用类，默认情况下打印至控制台和指定日志文件，在发布时禁用指定文件，控制台在CATALINA_HOME/logs/catalina.out。
 * 使用此类时，使用其getInstance()函数获得实例，通过该实例调用有关方法。
 * @author gong
 */
public final class Log {
	PrintStream out;
	
	/**
	 * 默认构造，打开文件
	 */
	private Log() {
		//try {
			//out = new PrintStream(new BufferedOutputStream(new FileOutputStream("F:\\chatroomlog.txt", true)));
		//} catch (FileNotFoundException e) {
		//	System.out.println("[WARN]日志文件没有找到，日志将只从控制台输出");
			out = System.out;
		//}
	}
	
	/**
	 * 不加修饰地输出日志
	 * @param content 日志内容
	 * @return
	 */
	private Log logRaw(String content) {
		out.println(content);
		out.flush();
		return this;
	}
	
	/**
	 * 带类型地输出日志.
	 * @param type 日志的类型/级别（没有按级别的设置，只是用来看的）
	 * @param str 要打印的信息
	 * @return
	 */
	private Log logType(String type, String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String content = sdf.format(new Date()) + "[" + type + "]" + str;
		return logRaw(content);
	}
	
	/**
	 * 以INFO级别打印日志.
	 * @param str 要打印的信息
	 * @return
	 */
	public Log log(String str) {
		return logType("INFO", str);
	}
	/**
	 * 以WARN级别打印日志.
	 * @param str 要打印的信息
	 * @return
	 */
	public Log logWarning(String str) {
		return logType("WARN", str);
	}
	/**
	 * 以ERROR级别打印日志
	 * @param str 要打印的信息
	 * @return
	 */
	public Log logError(String str) {
		return logType("ERROR", str);
	}
	/**
	 * 在日志中打印异常块.
	 * 打印全部调用栈
	 * @param e 异常
	 * @return
	 */
	public Log logErrorOnException(Exception e) {
		logError(e.getClass().getName() + ":");
		logRaw("\t\t[Error Block]");
		do {
			logRaw("Exception:" + e.getMessage());
			for(StackTraceElement ste : e.getStackTrace()) {
				logRaw("Method " + ste.getMethodName() + " in " + ste.getClassName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
			}
		} while ((e = (Exception) e.getCause()) != null);
		logRaw("\t\t[End Error Block]");
		return this;
	}

	/**
	 * 实例
	 */
	private static final Log instance = new Log();
	/**
	 * 获得一个实例.
	 * @return
	 */
	public static Log getInstance() {
		return instance;
	}
}
