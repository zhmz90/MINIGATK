java_library(
    name = "utils",
    srcs = glob(["**/*.java","exceptions:exceptions"]) + 
    ["//miniutils/src/main/java/org/broadinstitute/gatk/utils/commandline:cmdl",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/exceptions:excp",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/activeregion:activeregion",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/jna/clibrary:clib",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/pairhmm:pairhmm",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/io:io",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/progressmeter:progressmeter"],

    deps = ["@htsjdk//jar", 
            "@reflections//jar","@cofoja//jar","@log4j//jar","@commons_io//jar","@colt//jar",
            "@gson//jar","@commons_lang//jar","@freemarker//jar","@fastutil//jar",
            "//lib:javadoc","@httpcomponents//jar","@httpcore//jar","@commons_math//jar",
            "@jna//jar",
            ],
)
