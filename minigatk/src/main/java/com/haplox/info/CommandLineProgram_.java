package com.haplox.info;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;
import java.util.*;

public class CommandLineProgram_ {

    public static void main(String[] args){
	System.out.println("Hello CommandLineProgram! ");
	new CommandLineProgram_();
    }
    
    public ParsingEngine_ parser = new ParsingEngine_();

    @Argument(fullName = "logging_level", shortName = "l", doc = "Set the minimum level of logging", required = false)
    protected String logging_level = "INFO";

    @Output(fullName = "log_to_file", shortName = "log", doc = "Set the logging location", required = false)
    protected String toFile = null;

    @Argument(fullName = "help", shortName = "h", doc = "Generate the help message", required = false)
    public Boolean help = false;

    @Argument(fullName = "version", shortName = "version", doc ="Output version information", required = false)
    public Boolean version = false;

    private static final String patternString = "%-5p %d{HH:mm:ss,SSS} %C{1} - %m %n";

    static {

        forceJVMLocaleToUSEnglish();
        // setup a basic log configuration                                                              
        CommandLineUtils.configureConsoleLogging();
    }


    protected static void forceJVMLocaleToUSEnglish() {
	Locale.setDefault(Locale.US);
    }



}
