package shupletsov.nikita.synqq_enngineering_challenge;

import java.util.Arrays;
import java.util.List;

/**
 * Main class
 */
public class Main {

    public static void main(String[] args) {
        List<String> context = Arrays.asList(
            "John Wayne",
            "Tom Hanks",
            "Tom Cruise",
            "Clint Eastwood",
            "Jon Hamm",
            "John Nolan",
            "William",
            "Fitcher"
        );
        System.out.println("context: " + context);
        NamesCorrector namesCorrector = new NamesCorrector(context);
        String sentence = "tomorrow I have a meeting with Tim Hanks Tom Crus and Eastwud";
        System.out.println("sentence: " + sentence);
        System.out.println("result: " + namesCorrector.correctNames(sentence));
        sentence = "Michael likes movies with Jon Way and Client East";
        System.out.println("sentence: " + sentence);
        System.out.println("result: " + namesCorrector.correctNames(sentence));
        sentence = "Jonn invited me Jon Ham and Jon Wane, over for a lunch";
        System.out.println("sentence: " + sentence);
        System.out.println("result: " + namesCorrector.correctNames(sentence));
    }
}
