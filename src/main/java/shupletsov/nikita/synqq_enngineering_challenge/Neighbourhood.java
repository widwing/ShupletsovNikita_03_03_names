package shupletsov.nikita.synqq_enngineering_challenge;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.*;

class Neighbourhood {

    private List<List<String>> sentenceNames;

    Neighbourhood(List<String> words) {
        sentenceNames = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < 2; j++) {
                if (i + j + 1 > words.size()) {
                    break;
                }
                sentenceNames.add(words.subList(i, i + j + 1));
            }
        }
    }

    List<Pair<String, String>> correctNames(List<List<String>> context) {
        List<CompareResult> compareResults = compareWithContext(context);
        Map<List<String>, CompareResult> matches = matchResults(compareResults);
        Map<List<String>, CompareResult> correctedMatches = correctByNeighbors(matches);
        return makeCorrectionPairs(correctedMatches);
    }

    private List<CompareResult> compareWithContext(List<List<String>> context) {
        List<CompareResult> results = new ArrayList<>();
        for (List<String> contextPart : context) {
            for (List<String> sentenceName : sentenceNames) {
                if (contextPart.size() == 1) {
                    if (sentenceName.size() == 1) {
                        results.add(new CompareResult(sentenceName, contextPart));
                    }
                    continue;
                }
                if (sentenceName.size() == 1) {
                    results.add(new CompareResult(Arrays.asList(sentenceName.get(0), null), contextPart));
                    results.add(new CompareResult(Arrays.asList(null, sentenceName.get(0)), contextPart));
                } else {
                    results.add(new CompareResult(sentenceName, contextPart));
                }
            }
        }
        return results;
    }

    private Map<List<String>, CompareResult> matchResults(List<CompareResult> compareResults) {
        Map<List<String>, CompareResult> result = new HashMap<>();
        for (List<String> sentenceName : sentenceNames) {
            for (CompareResult compareResult : compareResults) {
                if (!compareResult.sentenceName.containsAll(sentenceName)) {
                    continue;
                }
                CompareResult q = result.get(sentenceName);
                if (q == null) {
                    result.put(sentenceName, compareResult);
                    continue;
                }
                if (q.getFirstResult() + q.getSecondResult() < compareResult.getFirstResult() + compareResult.getSecondResult()) {
                    result.put(sentenceName, compareResult);
                }
            }
        }
        return result;
    }

    private Map<List<String>, CompareResult> correctByNeighbors(Map<List<String>, CompareResult> matches) {
        // TODO implementation!
        Map<List<String>, CompareResult> result = new HashMap<>();
        for (List<String> sentenceName : sentenceNames) {
            CompareResult q = matches.get(sentenceName);
            if (sentenceName.size() == 2) {
                result.put(sentenceName, q);
                continue;
            }
            boolean contains = false;
            for (List<String> sentenceName2 : sentenceNames) {
                if (sentenceName2.size() != 2) {
                    continue;
                }
                if (!sentenceName2.containsAll(sentenceName)) {
                    continue;
                }
                CompareResult q2 = matches.get(sentenceName2);
                if (!q.contextName.equals(q2.contextName)) {
                    continue;
                }
                result.put(sentenceName2, q);
                contains = true;
                break;
            }
            if (!contains) {
                result.put(sentenceName, q);
            }
        }
        return result;
    }

    private List<Pair<String, String>> makeCorrectionPairs(Map<List<String>, CompareResult> matches) {
        List<Pair<String, String>> result = new ArrayList<>();
        for (List<String> sentenceName : sentenceNames) {
            CompareResult q = matches.get(sentenceName);
            if (q == null) {
                continue;
            }
            if (sentenceName.size() == 1) {
                result.add(Pair.of(sentenceName.get(0), q.contextName.get(q.contextName.size() == 1 || q.result.get(0) > 0. ? 0 : 1)));
            } else {
                result.add(Pair.of(StringUtils.join(sentenceName, " "), StringUtils.join(q.contextName, " ")));
            }
        }
        return result;
    }

    private static class CompareResult {

        List<String> sentenceName;

        List<String> contextName;

        List<Double> result;

        CompareResult(List<String> sentenceName, List<String> contextName) {
            this.sentenceName = sentenceName;
            this.contextName = contextName;

            JaroWinklerDistance distance = new JaroWinklerDistance();
            result = new ArrayList<>();
            for (int i = 0; i < contextName.size(); i++) {
                if (sentenceName.get(i) == null) {
                    result.add(i, 0.);
                } else {
                    result.add(i, distance.apply(sentenceName.get(i), contextName.get(i)));
                }
            }
        }

        Double getFirstResult() {
            return result.get(0);
        }

        Double getSecondResult() {
            return result.size() == 1 ? 0. : result.get(1);
        }

        @Override
        public String toString() {
            return "CompareResult{" +
                    "contextName=" + contextName +
                    ", sentenceName=" + sentenceName +
                    ", result=" + result +
                    "}";
        }
    }
}
