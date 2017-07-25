/**
 * 
 */
package log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gong
 *
 */
public final class Log {
	PrintStream out;
	/**
	 * 
	 */
	private Log() {
		try {
			out = new PrintStream(new BufferedOutputStream(new FileOutputStream("F:\\chatroomlog.txt", true)));
		} catch (FileNotFoundException e) {
			System.out.println("[WARN]日志文件没有找到，日志将只从控制台输出");
			out = System.out;
		}
	}
	private Log logRaw(String content) {
		out.println(content);
		System.out.println(content);
		out.flush();
		System.out.flush();
		return this;
	}
	private Log logType(String type, String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String content = sdf.format(new Date()) + "[" + type + "]" + str;
		return logRaw(content);
	}
	public Log log(String str) {
		return logType("INFO", str);
	}
	public Log logWarning(String str) {
		return logType("WARN", str);
	}
	public Log logError(String str) {
		return logType("ERROR", str);
	}
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

	private static final Log instance = new Log();
	public static Log getInstance() {
		return instance;
	}
}
