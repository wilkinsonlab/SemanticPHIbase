package es.upm.cbgp.semphi.logic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class TimeMeasure {

	private Map<String, LinkedList<IteracionYTiempo>> tiempos;

	private boolean enabled;

	public TimeMeasure(boolean ena) {
		this.enabled = ena;
		if (enabled) {
			tiempos = new HashMap<String, LinkedList<IteracionYTiempo>>();
		}
	}

	public void calculateTime(long tS, String str) {
		if (enabled) {
			long tE = System.currentTimeMillis();
			long millis = tE - tS;

			if (tiempos.containsKey(str)) {
				LinkedList<IteracionYTiempo> lst = tiempos.get(str);
				lst.add(new IteracionYTiempo(lst.size() + 1, millis));
				tiempos.put(str, lst);
			} else {
				LinkedList<IteracionYTiempo> lst = new LinkedList<IteracionYTiempo>();
				lst.add(new IteracionYTiempo(1, millis));
				tiempos.put(str, lst);
			}
		}
	}

	public String format(long millis) {
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
		return sb.toString();
	}

	public void saveResults() {
		if (enabled) {
			System.out.print("Saving results of time measurements .. ");
			try {
				BufferedWriter bW = new BufferedWriter(new FileWriter(
						"tiempos.txt", false));
				Iterator<Entry<String, LinkedList<IteracionYTiempo>>> it = tiempos
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, LinkedList<IteracionYTiempo>> ent = it.next();
					String key = ent.getKey();
					LinkedList<IteracionYTiempo> lstItYT = ent.getValue();
					bW.write(key.replace("\t", ""));
					bW.newLine();
					bW.newLine();
					for (int i = 0; i < lstItYT.size(); i++) {
						IteracionYTiempo iyt = lstItYT.get(i);
						bW.write(iyt.getIteraction() + " - "
								+ format(iyt.getTiempo()));
						bW.newLine();
					}
					bW.newLine();
					bW.newLine();
				}
				bW.close();
				System.out.println("done!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class IteracionYTiempo {
	private int iteracion;
	private long tiempo;

	public IteracionYTiempo(int i, long t) {
		iteracion = i;
		tiempo = t;
	}

	public int getIteraction() {
		return iteracion;
	}

	public long getTiempo() {
		return tiempo;
	}
}
