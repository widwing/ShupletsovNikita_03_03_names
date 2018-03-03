package shupletsov.nikita.synqq_enngineering_challenge;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.*;

// TODO comments
// TODO replace q, w, etc to adequate names
class NamesResolver {

    private static final String I = "I";

    private List<List<String>> context;

    private List<List<String>> sentenceNames;

    private List<String> sentenceSingleWordNames;

    NamesResolver(List<String> context, String sentence) {
        processContext(context);
        processSentence(sentence);
    }

    List<Pair<String, String>> resolveNames() {
        List<CompareResult> results = new ArrayList<>();
        JaroWinklerDistance distance = new JaroWinklerDistance();
        for (List<String> contextPart : context) {
            for (List<String> sentenceName : sentenceNames) {
                if (contextPart.size() == 1) {
                    if (sentenceName.size() == 1) {
                        results.add(new CompareResult(contextPart, sentenceName, Collections.singletonList(distance.apply(contextPart.get(0), sentenceName.get(0)))));
                    }
                    continue;
                }
                // TODO check second word result
                if (sentenceName.size() == 1) {
                    results.add(new CompareResult(contextPart, Arrays.asList(sentenceName.get(0), null), Arrays.asList(distance.apply(contextPart.get(0), sentenceName.get(0)), 0.d)));
                    results.add(new CompareResult(contextPart, Arrays.asList(null, sentenceName.get(0)), Arrays.asList(0.d, distance.apply(contextPart.get(1), sentenceName.get(0)))));
                }
            }
        }
        Map<String, Pair<String, Double>> a = new HashMap<>();
        for (String singleWordName : sentenceSingleWordNames) {
            for (CompareResult compareResult : results) {
                if (!compareResult.sentenceName.contains(singleWordName)) {
                    continue;
                }
                Pair<String, Double> w;
                if (singleWordName.equals(compareResult.sentenceName.get(0))) {
                    w = Pair.of(compareResult.contextName.get(0), compareResult.result.get(0));
                } else {
                    w = Pair.of(compareResult.contextName.get(1), compareResult.result.get(1));
                }
                Pair<String, Double> q = a.get(singleWordName);
                if (q == null || q.getRight() < w.getRight()) {
                    a.put(singleWordName, w);
                }
            }
        }
        List<Pair<String, String>> e = new ArrayList<>();
        for (String singleWordName : sentenceSingleWordNames) {
            Pair<String, Double> q = a.get(singleWordName);
            if (q.getRight() < 1.) {
                e.add(Pair.of(singleWordName, q.getLeft()));
            }
        }
        return e;
    }

    private void processContext(List<String> unpreparedContext) {
        context = new ArrayList<>();
        for (String name : unpreparedContext) {
            context.add(Arrays.asList(name.split(" ")));
        }
    }

    private void processSentence(String sentence) {
        sentenceNames = new ArrayList<>();
        sentenceSingleWordNames = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        for (String part : sentence.replaceAll("[.,]", "").split(" ")) {
            if (!Character.isLowerCase(part.charAt(0)) && !part.equals(I)) {
                buffer.add(part);
                sentenceSingleWordNames.add(part);
                continue;
            }
            makeNames(buffer);
            buffer = new ArrayList<>();
        }
        makeNames(buffer);
    }

    private void makeNames(List<String> buffer) {
        for (int i = 0; i < buffer.size(); i++) {
            for (int j = 0; j < 2; j++) {
                if (i + j + 1 > buffer.size()) {
                    continue;
                }
                sentenceNames.add(buffer.subList(i, i + j + 1));
            }
        }
    }

    private static class CompareResult {
        List<String> contextName;

        List<String> sentenceName;

        List<Double> result;

        CompareResult(List<String> contextName, List<String> sentenceName, List<Double> result) {
            this.contextName = contextName;
            this.sentenceName = sentenceName;
            this.result = result;
        }

        @Override
        public String toString() {
            return "\nCompareResult{" +
                    "contextName=" + contextName +
                    ", sentenceName=" + sentenceName +
                    ", result=" + result +
                    "}";
        }
    }
}
