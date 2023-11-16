package br.com.bda.machinesnack.money;

import java.util.HashMap;
import java.util.Map;

public class ValidadorNotas {

	private static final Map<Integer, Double> validNotes = new HashMap<>();

    static {
        validNotes.put(1, 1.0);
        validNotes.put(2, 2.0);
        validNotes.put(5, 5.0);
        validNotes.put(10, 10.0);
        validNotes.put(20, 20.0);
        validNotes.put(50, 50.0);
        validNotes.put(100, 100.0);
    }

    public static boolean isValidNote(int note) {
        return validNotes.containsKey(note);
    }

    public static double getNoteValue(int note) {
        return validNotes.getOrDefault(note, 0.0);
    }
}