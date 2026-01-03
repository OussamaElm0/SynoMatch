package com.example.synomatch.quiz;

import com.example.synomatch.data.SynonymsRepository;
import java.text.Normalizer;
import java.util.*;

public class QuizEngine {
    private final SynonymsRepository repo;
    private final Random random = new Random();

    public QuizEngine(SynonymsRepository repo) {
        this.repo = repo;
    }

    public List<QuizItem> generate(int count) {
        List<String> lemmas = new ArrayList<>(repo.getAllLemmaDisplay());
        Collections.shuffle(lemmas, random);

        List<QuizItem> items = new ArrayList<>();
        int i = 0;

        while (items.size() < count && i < lemmas.size()) {
            String base = lemmas.get(i);

            // 1. Find a correct synonym
            List<String> synDisplay = repo.getSynonymsDisplay(base);
            if (!synDisplay.isEmpty()) {
                String correct = pickOneDisplay(synDisplay);

                // 2. Find 3 unique incorrect answers (distractors)
                List<String> distractors = pickIncorrects(base, lemmas, 3);

                // Only create the question if we found enough wrong answers
                if (distractors.size() == 3) {
                    List<String> options = new ArrayList<>();
                    options.add(correct);
                    options.addAll(distractors);
                    Collections.shuffle(options, random); // Shuffle so correct answer isn't always first

                    items.add(new QuizItem(base, correct, options));
                }
            }
            i++;
        }
        return items;
    }

    private String pickOneDisplay(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    private List<String> pickIncorrects(String baseDisplay, List<String> allLemmas, int count) {
        List<String> results = new ArrayList<>();
        String baseNorm = normalize(baseDisplay);

        for (int tries = 0; tries < 200 && results.size() < count; tries++) {
            String pick = allLemmas.get(random.nextInt(allLemmas.size()));
            String pickNorm = normalize(pick);

            if (!pickNorm.equals(baseNorm)
                    && !results.contains(pick)
                    && !repo.areSynonyms(baseDisplay, pick)) {
                results.add(pick);
            }
        }
        return results;
    }

    private static String normalize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.FRENCH).trim();
    }
}
