java_library(

    name = "utils",
    srcs = glob(["**/*.java"]) + 
    ["//miniutils/src/main/java/org/broadinstitute/gatk/utils/commandline:cmdl",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/exceptions:excp",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/activeregion:activeregion",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/jna/clibrary:clib",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/pairhmm:pairhmm",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/io:io",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/progressmeter:progressmeter",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/sam:sam",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/help:help",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/pileup:pileup",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/smithwaterman:smithwaterman",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/locusiterator:locusiterator",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/downsampling:downsampling",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/recalibration:recalibration",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/haplotype:haplotype",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/genotyper:genotyper",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/variant:variant",
     "//miniutils/src/main/java/org/broadinstitute/gatk/utils/clipping:clipping",
     "//miniutils/src/main/java/htsjdk/samtools:samtools",
],

    deps = ["@htsjdk//jar", 
            "@reflections//jar","@cofoja//jar","@log4j//jar","@commons_io//jar","@colt//jar",
            "@gson//jar","@commons_lang//jar","@freemarker//jar","@fastutil//jar",
            "//lib:javadoc","@httpcomponents//jar","@httpcore//jar","@commons_math//jar",
            "@jna//jar","@slf4j//jar","@commons_collections//jar",
            #"//miniutils/src/main/java/htsjdk/lib:htsjdk",
            ],

)
