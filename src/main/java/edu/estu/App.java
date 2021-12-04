package edu.estu;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) {
        Options bean = new Options();
        CmdLineParser parser = new CmdLineParser(bean);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");

            parser.printUsage(System.err);
            System.err.println();
            return;
        }

        List<String> filenames = bean.filenames;
        ArrayList<Path> paths = new ArrayList<>();

        for (String filename : filenames) {
            paths.add(Paths.get(filename));
        }

        List<String> allLines = new ArrayList<>();
        for (Path path : paths) {
            if (Files.notExists(path)) {
                System.out.println("The file you entered does not exist!");
            }
            List<String> lines;

            try {
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            allLines.addAll(lines);
        }

        List<String> allWords = prepare(allLines);

        if (bean.unique) {
            allWords = unique(allWords);
        }
        if (bean.reverse) {
            reverse(allWords);
        }

        switch (bean.task) {
            case "NumOfTokens":
                System.out.println("Number of Tokens: " + numberOfTokens(allWords));
                break;
            case "FrequentTerms":
                if (bean.reverse) {
                    print(reverse(frequentTerms(allWords)), bean.topN);
                } else {
                    print(frequentTerms(allWords), bean.topN);
                }
                break;
            case "TermLengthStats":
                System.out.println(termLengthStats(allWords));
                break;
            case "TermsStartWith":
                if (bean.reverse){
                    print(termsStartWith(reverse(unique(allWords)), bean.startsWith), bean.topN);
                }
                else {
                    print(termsStartWith(unique(allWords), bean.startsWith), bean.topN);
                }
                break;
            default:
        }
    }

    public static List<String> prepare(List<String> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();

        for (String string : list) {
            StringTokenizer stringTokenizer = new StringTokenizer(string);

            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();
                token = token.toLowerCase(Locale.ROOT);
                token = token.trim();

                String[] tempList = token.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

                ArrayList<String> resultList = new ArrayList<>();

                for (String s : tempList) {
                    if (!s.equals("")) {
                        resultList.add(s);
                    }
                }

                stringArrayList.addAll(resultList);
            }
        }

        Collections.sort(stringArrayList);

        return stringArrayList;
    }

    public static int numberOfTokens(List<String> words) {
        return words.size();
    }

    public static Map<String, Integer> frequentTerms(List<String> words) {
        Map<String, Integer> frequentTermsMap = new HashMap<>();

        for (String word : unique(words)) {
            int frequency = 0;

            for (String word2 : words) {
                if (word.equals(word2)) {
                    frequency += 1;
                }
            }
            frequentTermsMap.put(word, frequency);
        }
        return sort(frequentTermsMap, false);
    }

    public static HashMap<String, Integer> sort(Map<String, Integer> map, final boolean isReverse) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (!isReverse) {
                    if (!o2.getValue().equals(o1.getValue())) {
                        return (o2.getValue()).compareTo(o1.getValue());
                    } else {
                        return (o1.getKey().compareTo(o2.getKey()));
                    }
                } else {
                    if (!o2.getValue().equals(o1.getValue())) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    } else {
                        return (o1.getKey().compareTo(o2.getKey()));
                    }
                }
            }
        });

        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static List<String> unique(List<String> words) {
        List<String> result = new ArrayList<>(new HashSet<>(words));
        Collections.sort(result);
        return result;
    }

    public static List<String> reverse(List<String> words) {
        Collections.reverse(words);
        return words;
    }

    public static Map<String, Integer> reverse(Map<String, Integer> wordsWithCountValues) {
        return sort(wordsWithCountValues, true);
    }

    public static void print(Map<String, Integer> words, int times) {
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " " + value);

            times--;

            if (times <= 0) {
                break;
            }
        }
    }

    public static void print(List<String> words, int times) {
        for (String word : words) {
            System.out.println(word);

            times--;

            if (times <= 0) {
                break;
            }
        }
    }

    public static String termLengthStats(List<String> words) {
        int maxLength = words.get(0).length();
        int minLength = words.get(0).length();
        double sumOfLengths = 0;
        double averageLength;

        for (String word : words) {
            int length = word.length();

            if (length > maxLength) {
                maxLength = length;
            }
            if (length < minLength) {
                minLength = length;
            }

            sumOfLengths += length;
        }

        averageLength = sumOfLengths / words.size();

        return String.format("Max Token Length in Character: %d, Min Token Length: %d, Average Token Length: %f%n", maxLength, minLength, averageLength);
    }

    //unique words list must be given
    public static List<String> termsStartWith(List<String> words, String startsWith) {
        List<String> resultList = new ArrayList<>();

        for (String word : words) {
            if (word.startsWith(startsWith)) {
                resultList.add(word);
            }
        }
        return resultList;
    }

}


