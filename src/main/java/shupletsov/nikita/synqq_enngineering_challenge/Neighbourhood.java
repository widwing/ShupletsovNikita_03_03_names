package shupletsov.nikita.synqq_enngineering_challenge;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.util.*;

// TODO comments
class Neighbourhood {

    private List<SentenceName> sentenceNames;

    Neighbourhood(List<String> words) {
        sentenceNames = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < 2; j++) {
                if (i + j + 1 > words.size()) {
                    break;
                }
                sentenceNames.add(new SentenceName(words.subList(i, i + j + 1), i));
            }
        }
    }

    List<Pair<String, String>> correctNames(List<List<String>> context) {
        List<CompareResult> compareResults = compareWithContext(context);
        Map<SentenceName, CompareResult> filtered = filter(compareResults);
        return makeCorrectionPairs(filtered);
    }

    private List<CompareResult> compareWithContext(List<List<String>> context) {
        List<CompareResult> results = new ArrayList<>();
        for (List<String> contextPart : context) {
            for (SentenceName sentenceName : sentenceNames) {
                if (contextPart.size() == 1) {
                    if (sentenceName.size() == 1) {
                        results.add(new CompareResult(sentenceName, contextPart, computeDistance(sentenceName.words, contextPart)));
                    }
                    continue;
                }
                if (sentenceName.size() == 1) {
                    results.add(new CompareResult(sentenceName, contextPart, computeDistance(Arrays.asList(sentenceName.get(0), null), contextPart)));
                    results.add(new CompareResult(sentenceName, contextPart, computeDistance(Arrays.asList(null, sentenceName.get(0)), contextPart)));
                } else {
                    results.add(new CompareResult(sentenceName, contextPart, computeDistance(sentenceName.words, contextPart)));
                }
            }
        }
        return results;
    }

    private Map<SentenceName, CompareResult> filter(List<CompareResult> compareResults) {
        // First step: find the best result for each SentenceName from sentenceNames
        Map<SentenceName, CompareResult> result = new HashMap<>();
        for (SentenceName sentenceName : sentenceNames) {
            for (CompareResult compareResult1 : compareResults) {
                if (!compareResult1.sentenceName.equals(sentenceName)) {
                    continue;
                }
                CompareResult compareResult2 = result.get(sentenceName);
                if (compareResult2 == null) {
                    result.put(sentenceName, compareResult1);
                    continue;
                }
                if (compareResult2.getDistance() < compareResult1.getDistance()) {
                    result.put(sentenceName, compareResult1);
                }
            }
        }

        // Second step: check dual SentenceName to exclude worse neighbours
        for (SentenceName sentenceName1 : sentenceNames) {
            if (sentenceName1.size() != 2) {
                continue;
            }
            if (result.get(sentenceName1) == null) {
                continue;
            }
            for (SentenceName sentenceName2 : sentenceNames) {
                if (sentenceName2.size() != 2) {
                    continue;
                }
                if (result.get(sentenceName2) == null) {
                    continue;
                }
                if (sentenceName1.position + 1 != sentenceName2.position) {
                    continue;
                }

                result.remove(result.get(sentenceName1).getDistance() > result.get(sentenceName2).getDistance() ? sentenceName2 : sentenceName1);
                break;
            }
        }

        // Third step: remove single SentenceName's if it already contains in duals with checking it's neighborliness
        for (SentenceName sentenceName1 : sentenceNames) {
            if (sentenceName1.size() != 1) {
                continue;
            }
            for (SentenceName sentenceName2 : sentenceNames) {
                if (sentenceName2.size() != 2) {
                    continue;
                }
                if (result.get(sentenceName2) == null) {
                    continue;
                }
                if (sentenceName1.position != sentenceName2.position && sentenceName1.position != sentenceName2.position + 1) {
                    continue;
                }

                result.remove(sentenceName1);
                break;
            }
        }
        return result;
    }

    private List<Pair<String, String>> makeCorrectionPairs(Map<SentenceName, CompareResult> matches) {
        List<Pair<String, String>> result = new ArrayList<>();
        for (SentenceName sentenceName : sentenceNames) {
            CompareResult compareResult = matches.get(sentenceName);
            if (compareResult == null) {
                continue;
            }
            result.add(Pair.of(StringUtils.join(sentenceName.words, " "), StringUtils.join(compareResult.contextName, " ")));
        }
        return result;
    }

    private List<Double> computeDistance(List<String> first, List<String> second) {
        JaroWinklerDistance distance = new JaroWinklerDistance();
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            if (first.get(i) == null) {
                result.add(i, 0.);
            } else {
                result.add(i, distance.apply(first.get(i), second.get(i)));
            }
        }
        return result;
    }

    private static class SentenceName {

        List<String> words;

        int position;

        SentenceName(List<String> words, int position) {
            this.words = words;
            this.position = position;
        }

        int size() {
            return words.size();
        }

        String get(int index) {
            return words.get(index);
        }


        @Override
        public int hashCode() {
            int result = words.hashCode();
            result = 31 * result + position;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SentenceName that = (SentenceName) o;

            if (position != that.position) return false;
            return words.equals(that.words);
        }

        @Override
        public String toString() {
            return "\nSentenceName{" +
                    "words=" + words +
                    ", position=" + position +
                    '}';
        }
    }

    private static class CompareResult {

        SentenceName sentenceName;

        List<String> contextName;

        List<Double> distances;

        CompareResult(SentenceName sentenceName, List<String> contextName, List<Double> distances) {
            this.sentenceName = sentenceName;
            this.contextName = contextName;
            this.distances = distances;
        }

        double getDistance() {
            return distances.get(0) + (distances.size() == 1 ? 0. : distances.get(1));
        }

        @Override
        public String toString() {
            return "CompareResult{" +
                    "contextName=" + contextName +
                    ", sentenceName=" + sentenceName +
                    ", result=" + distances +
                    "}";
        }
    }
}
