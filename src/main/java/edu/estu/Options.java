package edu.estu;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.util.List;

public class Options {
    @Argument(required = true,handler = StringArrayOptionHandler.class, usage = "name of the file that you want to sort")
    List<String> filenames;

    @Option(name = "-task", required = true)
    String task;

    @Option(name = "-r",usage = "refers to reverse order")
    boolean reverse;

    @Option(name = "-u",usage = "refers to unique values")
    boolean unique;

    @Option(name = "-topN")
    int topN = 5;// default value = 5

    @Option(name = "-start")
    String startsWith;
}
