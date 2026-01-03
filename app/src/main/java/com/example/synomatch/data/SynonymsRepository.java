package com.example.synomatch.data;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.*;

public class SynonymsRepository {

    public static class Word {
        @SerializedName("id") public int id;
        @SerializedName("lemma") public String lemma;
        @SerializedName("pos") public String pos;
        @SerializedName("synonyms") public List<String> synonyms;
    }

    public static class Root {
        @SerializedName("words") public List<Word> words;
    }

    private static class LemmaData {
        String displayLemma;              // original lemma for UI
        final Set<String> synNorm = new HashSet<>();     // normalized synonyms for matching
        final List<String> synDisplay = new ArrayList<>(); // original synonyms for UI
    }

    private final Map<String, LemmaData> dataByLemmaNorm = new HashMap<>();
    private final List<String> allLemmaDisplay = new ArrayList<>();

    public SynonymsRepository(Context context) {
        Root root = loadFromAssets(context, "synonyms.json");
        if (root != null && root.words != null) {
            for (Word w : root.words) {
                String lemmaNorm = normalize(w.lemma);
                if (lemmaNorm.isEmpty()) continue;

                LemmaData data = dataByLemmaNorm.get(lemmaNorm);
                if (data == null) {
                    data = new LemmaData();
                    data.displayLemma = safeTrim(w.lemma);
                    dataByLemmaNorm.put(lemmaNorm, data);
                    allLemmaDisplay.add(data.displayLemma);
                }

                if (w.synonyms != null) {
                    for (String s : w.synonyms) {
                        String synDisplay = safeTrim(s);
                        String synNorm = normalize(synDisplay);
                        if (!synNorm.isEmpty()) {
                            if (data.synNorm.add(synNorm)) {
                                data.synDisplay.add(synDisplay);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean areSynonyms(String lemma, String candidate) {
        LemmaData data = dataByLemmaNorm.get(normalize(lemma));
        if (data == null) return false;
        return data.synNorm.contains(normalize(candidate));
    }

    public List<String> getSynonymsDisplay(String lemma) {
        LemmaData data = dataByLemmaNorm.get(normalize(lemma));
        return data != null ? Collections.unmodifiableList(data.synDisplay) : Collections.emptyList();
    }

    public Set<String> getSynonymsNorm(String lemma) {
        LemmaData data = dataByLemmaNorm.get(normalize(lemma));
        return data != null ? Collections.unmodifiableSet(data.synNorm) : Collections.emptySet();
    }

    public List<String> getAllLemmaDisplay() {
        return Collections.unmodifiableList(allLemmaDisplay);
    }

    // Helpers

    private Root loadFromAssets(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return new Gson().fromJson(sb.toString(), Root.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.FRENCH).trim();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}