package shupletsov.nikita.synqq_enngineering_challenge;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.*;

// TODO comments
// TODO replace q, w, etc to adequate names
class NamesResolver {

    private static final String I = "I";

    private List<List<String>> context;

    NamesResolver(List<String> context) {
        processContext(context);
    }

    List<Pair<String, String>> correctNames(String sentence) {
        List<List<String>> sentenceNames = processSentence(sentence);
        List<CompareResult> results = new ArrayList<>();
        for (List<String> contextPart : context) {
            for (List<String> sentenceName : sentenceNames) {
                if (contextPart.size() == 1) {
                    if (sentenceName.size() == 1) {
                        results.add(new CompareResult(contextPart, sentenceName));
                    }
                    continue;
                }
                if (sentenceName.size() == 1) {
                    results.add(new CompareResult(contextPart, Arrays.asList(sentenceName.get(0), null)));
                    results.add(new CompareResult(contextPart, Arrays.asList(null, sentenceName.get(0))));
                } else {
                    results.add(new CompareResult(contextPart, sentenceName));
                }
            }
        }
        Map<List<String>, CompareResult> a = new HashMap<>();
        for (List<String> sentenceName : sentenceNames) {
            for (CompareResult compareResult : results) {
                if (!compareResult.sentenceName.containsAll(sentenceName)) {
                    continue;
                }
                CompareResult q = a.get(sentenceName);
                if (q == null) {
                    a.put(sentenceName, compareResult);
                    continue;
                }
//                    System.out.println("compare: " + q + " with " + compareResult + " ====> 2");
                if (ObjectUtils.compare(q.getFirstResult(), compareResult.getFirstResult()) < 1 && ObjectUtils.compare(q.getSecondResult(), compareResult.getSecondResult()) < 1) {
                    a.put(sentenceName, compareResult);
                }
            }
        }
        List<Pair<String, String>> e = new ArrayList<>();
        for (List<String> sentenceName : sentenceNames) {
            CompareResult q = a.get(sentenceName);
            e.add(Pair.of(StringUtils.join(sentenceName, " "), StringUtils.join(q.contextName, " ")));
        }
        // TODO lots of garbage. need to clean up
        return e;
    }

    private void processContext(List<String> unpreparedContext) {
        context = new ArrayList<>();
        for (String name : unpreparedContext) {
            context.add(Arrays.asList(name.split(" ")));
        }
    }

    private List<List<String>> processSentence(String sentence) {
        List<List<String>> sentenceNames = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        for (String part : sentence.replaceAll("[.,]", "").split(" ")) {
            if (!Character.isLowerCase(part.charAt(0)) && !part.equals(I)) {
                buffer.add(part);
                continue;
            }
            sentenceNames.addAll(makeNames(buffer));
            buffer = new ArrayList<>();
        }
        sentenceNames.addAll(makeNames(buffer));
        return sentenceNames;
    }

    private List<List<String>> makeNames(List<String> buffer) {
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            for (int j = 0; j < 2; j++) {
                if (i + j + 1 > buffer.size()) {
                    continue;
                }
                result.add(buffer.subList(i, i + j + 1));
            }
        }
        return result;
    }

    private static class CompareResult {
        List<String> contextName;

        List<String> sentenceName;

        List<Double> result;

        CompareResult(List<String> contextName, List<String> sentenceName) {
            this.contextName = contextName;
            this.sentenceName = sentenceName;
            JaroWinklerDistance distance = new JaroWinklerDistance();
            result = new ArrayList<>();
            for (int i = 0; i < contextName.size(); i++) {
                if (sentenceName.get(i) == null) {
                    result.add(i, null);
                } else {
                    result.add(i, distance.apply(contextName.get(i), sentenceName.get(i)));
                }
            }
        }

        Double getFirstResult() {
            return result.get(0);
        }

        Double getSecondResult() {
            return result.size() == 1 ? null : result.get(1);
        }

        @Override
        public String toString() {
            return "CompareResult{" +
                    "contextName=" + contextName +
//                    ", sentenceName=" + sentenceName +
                    ", result=" + result +
                    "}";
        }
    }
}
