package shupletsov.nikita.synqq_enngineering_challenge;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO comments
class NamesCorrector {

    private static final String I = "I";

    private List<List<String>> context;

    NamesCorrector(List<String> context) {
        this.context = processContext(context);
    }

    List<Pair<String, String>> correctNames(String sentence) {
        List<Neighbourhood> neighbourhoods = processSentence(sentence);
        List<Pair<String, String>> result = new ArrayList<>();
        for (Neighbourhood neighbourhood : neighbourhoods) {
            result.addAll(neighbourhood.correctNames(context));
        }
        return result;
    }

    private List<List<String>> processContext(List<String> unpreparedContext) {
        List<List<String>> result = new ArrayList<>();
        for (String name : unpreparedContext) {
            result.add(Arrays.asList(name.split(" ")));
        }
        return result;
    }

    private List<Neighbourhood> processSentence(String sentence) {
        List<Neighbourhood> sentenceNames = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        for (String part : sentence.replaceAll("[.,]", "").split(" ")) {
            if (!Character.isLowerCase(part.charAt(0)) && !part.equals(I)) {
                buffer.add(part);
                continue;
            }
            if (buffer.size() > 0) {
                sentenceNames.add(new Neighbourhood(buffer));
                buffer = new ArrayList<>();
            }
        }
        if (buffer.size() > 0) {
            sentenceNames.add(new Neighbourhood(buffer));
        }
        return sentenceNames;
    }
}
