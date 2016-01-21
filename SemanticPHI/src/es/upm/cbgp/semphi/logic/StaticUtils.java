package es.upm.cbgp.semphi.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Clase que representa ciertas utilidades de forma est�tica.
 * 
 * @author Alejandor Rodr�guez Gonz�lez - Universidad Carlos III de Madrid -
 *         Proyecto SONAR2
 * 
 */
public class StaticUtils {

	private static boolean debug = true;
	@SuppressWarnings("unused")
	private static Logger logger;
	@SuppressWarnings("unused")
	private static FileHandler fhlogger;
	@SuppressWarnings("unused")
	private static SimpleFormatter logformatter;
	private static final int FILE_NUMERIC_VALUE = 5;

	public static int longestSubstr(String first, String second) {
		if (first == null || second == null || first.length() == 0
				|| second.length() == 0) {
			return 0;
		}

		int maxLen = 0;
		int fl = first.length();
		int sl = second.length();
		int[][] table = new int[fl + 1][sl + 1];

		for (int s = 0; s <= sl; s++)
			table[0][s] = 0;
		for (int f = 0; f <= fl; f++)
			table[f][0] = 0;

		for (int i = 1; i <= fl; i++) {
			for (int j = 1; j <= sl; j++) {
				if (first.charAt(i - 1) == second.charAt(j - 1)) {
					if (i == 1 || j == 1) {
						table[i][j] = 1;
					} else {
						table[i][j] = table[i - 1][j - 1] + 1;
					}
					if (table[i][j] > maxLen) {
						maxLen = table[i][j];
					}
				}
			}
		}
		return maxLen;
	}

	/**
	 * Method to get the URL contained in a text.
	 * 
	 * @param message
	 *            Receives the text.
	 * @return Return the URL
	 */
	public static String getURLIn(String message) {
		message = message.toUpperCase();
		String parts[] = message.trim().split(" ");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].contains("HTTP")) {
				String url = parts[i].substring(parts[i].indexOf("HTTP"),
						parts[i].length());
				return url;
			}
		}
		return null;
	}

	public static String longestCommonSubstring(String S1, String S2) {
		int Start = 0;
		int Max = 0;
		for (int i = 0; i < S1.length(); i++) {
			for (int j = 0; j < S2.length(); j++) {
				int x = 0;
				while (S1.charAt(i + x) == S2.charAt(j + x)) {
					x++;
					if (((i + x) >= S1.length()) || ((j + x) >= S2.length()))
						break;
				}
				if (x > Max) {
					Max = x;
					Start = i;
				}
			}
		}
		return S1.substring(Start, (Start + Max));
	}

	public static LinkedList<String> getAllPossibleSubstringCombinations(
			String str, int maxWords) {
		LinkedList<String> ret = new LinkedList<String>();
		String partsStr[] = str.split(" ");
		for (int j = 0; j < (partsStr.length - maxWords); j++) {
			String currentStr = "";
			for (int i = 0; i < maxWords; i++) {
				currentStr += partsStr[j + i] + " ";
				ret.add(currentStr);
			}
		}
		return ret;
	}

	public static String removeOneCharacterWords(String input) {
		String output = "";
		String[] input_text = input.split("\\s+");

		boolean isSW = false;

		for (int i = 0; i < input_text.length; i++) {
			if (input_text[i].length() == 1) {
				isSW = true;
			}

			if (!isSW) {
				output = output + input_text[i] + " ";
			}
			isSW = false;
		}
		if (output.length() > 0) {
			output = output.substring(0, output.length() - 1);
			output = output.toUpperCase();
		}

		return output;
	}

	public static String removeStopWords(String input, String STOP_WORDS[]) {
		String output = "";
		String[] input_text = input.split("\\s+");

		boolean isSW = false;

		for (int i = 0; i < input_text.length; i++) {
			for (int j = 0; j < STOP_WORDS.length; j++) {
				if (input_text[i].compareToIgnoreCase(STOP_WORDS[j]) == 0) {
					isSW = true;
				}
			}
			if (!isSW) {
				output = output + input_text[i] + " ";
			}
			isSW = false;
		}
		if (output.length() > 0) {
			output = output.substring(0, output.length() - 1);
			output = output.toUpperCase();
		}

		return output;
	}

	/**
	 * M�todo para comprobar si una cadena esta vacia ("" o null)
	 * 
	 * @param str
	 *            Recibe la cadena
	 * @return Devuelve booleano.
	 */
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.trim().equals("")) || (str.equals("null")));
	}

	/**
	 * M�todo para obtener un float a partir de un String.
	 * 
	 * @param str
	 *            Recibe el String.
	 * @return Devuelve el float.
	 */
	public static float getFloatFromString(String str) {
		if (str == null)
			return 0;
		float res = 0;
		try {
			res = Float.parseFloat(str);
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	/**
	 * M�todo para saber si se est� ejecutando desde un JAR
	 * 
	 * @return Devuelve un booleano.
	 */
	public static boolean isExecutingFromJAR() {
		String className = StaticUtils.class.getName().replace('.', '/');
		String classJar = StaticUtils.class.getResource(
				"/" + className + ".class").toString();
		if (classJar.startsWith("jar:")) {
			return true;
		}
		return false;
	}

	/**
	 * M�todo para obtener el nombre del fichero JAR
	 * 
	 * @return Devuelve la cadena.
	 */
	public static String getJARFileName() {
		String className = StaticUtils.class.getName().replace('.', '/');
		String classJar = StaticUtils.class.getResource(
				"/" + className + ".class").getFile();
		classJar = classJar
				.substring(FILE_NUMERIC_VALUE, classJar.indexOf('!'));
		return classJar;
	}

	/**
	 * M�todo para obtener un long a partir de un String.
	 * 
	 * @param str
	 *            Recibe el String.
	 * @return Devuelve el long.
	 */
	public static double getDoubleFromString(String str) {
		if (str == null)
			return 0;
		double res = 0;
		try {
			res = Double.parseDouble(str);
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	/**
	 * M�todo para borrar una extensi�n en una cadena que contenga un fichero y
	 * su extensi�n.
	 * 
	 * @param name
	 *            Recibe la cadena.
	 * @return Devuelve otra cadena.
	 */
	public static String removeExtension(String name) {
		String ret = "";
		for (int i = 0; i < name.lastIndexOf('.'); i++) {
			ret += name.charAt(i);
		}
		return ret;
	}

	/**
	 * M�todo de debug.
	 * 
	 * @param string
	 *            Recibe la cadena a guardar.
	 */
	public static void debug(String string) {
		if (debug) {
			try {
				BufferedWriter bW = new BufferedWriter(new FileWriter(
						"debug_signal.txt", true));
				// bW.newLine();
				bW.write(string);
				bW.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.print("Debug: " + string);
		}

	}

	/**
	 * M�todo para obtener un int de una cadena.
	 * 
	 * @param str
	 *            Recibe la cadena.
	 * @return Devuelve el entero.
	 */
	public static int getIntFromString(String str) {
		if (str == null)
			return 0;
		int res = 0;
		try {
			res = Integer.parseInt(str);
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	/**
	 * M�todo para loguear.
	 * 
	 * @param m
	 *            Recibe el mensaje.
	 */
	public static void log(String m) {
		System.out.println(m);
		// if (logger == null) {
		// logger = Logger.getLogger("MDSS_Log");
		// }
		// if (fhlogger == null) {
		// try {
		// fhlogger = new FileHandler("MDSS.log", true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// logger.addHandler(fhlogger);
		// logger.setLevel(Level.ALL);
		// if (logformatter == null) {
		// logformatter = new SimpleFormatter();
		// }
		// fhlogger.setFormatter(logformatter);
		// logger.log(Level.WARNING, m);
		// }
	}

	/**
	 * M�todo para saber si una cadena es un entero.
	 * 
	 * @param cat
	 *            Recibe la cadena.
	 * @return Devuelve un booleano.
	 */
	public static boolean isInteger(String cat) {
		try {
			Integer.parseInt(cat);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * M�todo para saber si una cadena es un float.
	 * 
	 * @param cat
	 *            Recibe la cadena.
	 * @return Devuelve un booleano.
	 */
	public static boolean isFloat(String cat) {
		try {
			Float.parseFloat(cat);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * M�todo para saber si una cadena es un double.
	 * 
	 * @param cat
	 *            Recibe la cadena.
	 * @return Devuelve un booleano.
	 */
	public static boolean isDouble(String cat) {
		try {
			Double.parseDouble(cat);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * M�todo para saber si una cadena es un booleano.
	 * 
	 * @param c
	 *            Recibe la cadena.
	 * @return Devuelve un booleano.
	 */
	public static boolean isBoolean(String c) {
		try {
			Boolean.parseBoolean(c);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * M�todo para obtener el OS
	 * 
	 * @return Devuelve una cadena.
	 */
	public static String getOSName() {
		String osNameProperty = System.getProperty("os.name");

		if (osNameProperty == null) {
			throw new RuntimeException("os.name property is not set");
		} else {
			osNameProperty = osNameProperty.toLowerCase();
		}

		if (osNameProperty.contains("win")) {
			return "win";
		} else if (osNameProperty.contains("mac")) {
			return "mac";
		} else if (osNameProperty.contains("linux")
				|| osNameProperty.contains("nix")) {
			return "linux";
		} else {
			throw new RuntimeException("Unknown OS name: " + osNameProperty);
		}
	}

	public static void addJarToClasspath(File jarFile) {
		try {
			URL url = jarFile.toURI().toURL();
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader
					.getSystemClassLoader();
			Class<?> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL",
					new Class<?>[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static String removeChars(String str, char[] exclude) {
		String ret = str;
		for (int i = 0; i < exclude.length; i++) {
			ret = removeCharacter(ret, exclude[i]);
		}
		return ret;
	}

	public static String removeCharacter(String ret, char c) {
		String retu = "";
		for (int i = 0; i < ret.length(); i++) {
			if (ret.charAt(i) != c) {
				retu += ret.charAt(i);
			}
		}
		return retu;
	}

	public static String reverse(String s) {
		String ret = "";
		for (int i = s.length() - 1; i > 0; i--) {
			ret += s.charAt(i);
		}
		return ret;
	}

	public static String formatTime(long segundos) {
		long minutos = segundos / 60;
		segundos = segundos % 60;
		long horas = minutos / 60;
		minutos = minutos % 60;
		return horas + ":" + minutos + ":" + segundos;
	}

	public static void removeFilesFromFolder(String folder) {
		File fold = new File(folder);
		for (int i = 0; i < fold.listFiles().length; i++) {
			fold.listFiles()[i].delete();
		}
	}

	public static String getExtensionFromFile(String string) throws Exception {
		return string.substring(string.lastIndexOf('.') + 1, string.length());
	}

	public static String getFileNameFromFile(String string) {
		return string.substring(0, string.lastIndexOf('.'));
	}

	public static String getStringFromList(LinkedList<String> words) {
		String ret = "";
		for (int i = 0; i < words.size(); i++) {
			ret += words.get(i) + " ";
		}
		ret = ret.trim();
		return ret;
	}

	public static String getStringFromList(LinkedList<String> words, String sep) {
		String ret = "";
		for (int i = 0; i < words.size(); i++) {
			ret += words.get(i) + sep;
		}
		ret = ret.trim();
		return ret;
	}

	public static int getNumberOfWords(String s) {
		return s.split(" ").length;
	}

	public static Date parseFBDate(String str) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+SSS");
		try {
			return df.parse(str.replace("T", " "));
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Method to check if a given string contains in all the string the same
	 * letter.
	 * 
	 * For example: AAAAAAAAA
	 * 
	 * @param comment
	 *            The coment
	 * @return true false
	 */
	public static boolean allTheStringContainsSameLetter(String comment) {
		if (comment.length() > 1) {
			char c = comment.charAt(0);
			for (int i = 1; i < comment.length(); i++) {
				if (comment.charAt(i) != c) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static LinkedList<String> loadFileAsStrings(String f)
			throws Exception {
		LinkedList<String> content = new LinkedList<String>();
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String r = bL.readLine();
			content.add(r);
		}
		bL.close();
		return content;
	}

	public static String repeat(String s, int t) {
		String ret = "";
		for (int i = 0; i < t; i++) {
			ret += s;
		}
		return ret;
	}

	public static void write(String f, String str) {
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(f, true));
			bW.write(str);
			bW.newLine();
			bW.close();
		} catch (Exception e) {

		}

	}

	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return -1;
		}
	}

	public static String listToStringSeparatedBy(LinkedList<String> lst, char c) {
		if (lst != null) {
			String ret = "";
			if (lst.size() > 0) {
				for (int i = 0; i < lst.size(); i++) {
					ret += lst.get(i) + c;
				}
				ret = ret.substring(0, ret.length() - 1);
			}
			return ret;
		}
		return null;
	}

	public static String getStringUntilLast(String str, char c) {
		return str.substring(0, str.lastIndexOf(c));
	}

	public static boolean isURLReachable(String mapping) {
		// http://stackoverflow.com/questions/18134718/java-quickest-way-to-check-if-url-exists
		try {
			URL url = new URL(mapping);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("HEAD");
			int responseCode = huc.getResponseCode();
			if (responseCode != 200) {
				return false;
			}
		} catch (Exception e) {

		}
		return true;
		// Socket socket = null;
		// boolean reachable = false;
		// try {
		// socket = new Socket(mapping, 80);
		// reachable = true;
		// }
		// catch (Exception e) {
		// reachable = false;
		// }
		// finally {
		// if (socket != null)
		// try {
		// socket.close();
		// } catch (IOException e) {
		// }
		// }
		// return reachable;
	}

	public static void writeFile(String f, String str) {
		try {
			BufferedWriter bW = new BufferedWriter(new FileWriter(f, true));
			bW.write(str);
			bW.newLine();
			System.out.println(str);
			bW.close();
		} catch (Exception e) {

		}
	}

	public static void calculateTime(long tS, String msg) {
		long tE = System.currentTimeMillis();
		long millis = tE - tS;
		if (millis < 0) {
			throw new IllegalArgumentException(
					"Duration must be greater than zero!");
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		millis -= TimeUnit.MINUTES.toMillis(seconds);
		long miliSeconds = TimeUnit.MILLISECONDS.toMillis(millis);
		
		StringBuilder sb = new StringBuilder(128);
		sb.append(days);
		sb.append(" Days ");
		sb.append(hours);
		sb.append(" Hours ");
		sb.append(minutes);
		sb.append(" Minutes ");
		sb.append(seconds);
		sb.append(" Seconds ");
		sb.append(miliSeconds);
		sb.append(" Miliseconds");
		
		System.out.println(msg + sb.toString());

	}

	public static int readInt() {
		try {
			BufferedReader bL = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("> ");
			String rd = bL.readLine();
			bL.close();
			System.out.println();
			return Integer.parseInt(rd);
		}
		catch (Exception e) {
			return -1;
		}
	}

}