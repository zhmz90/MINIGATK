
java_binary(
    name = "zero",
    srcs = glob(["**/*CommandLineProgram_.java","**/*ParsingEngine_.java",
                 "**/*Argument.java","**/*Output.java"]),
    main_class = "com.haplox.info.CommandLineProgram_",
    deps = [":log4j",":commons_io",":cofoja",":commandline"]
)


java_import(
    name = "log4j",
    jars = ["deps/apache-log4j-1.2.17/log4j-1.2.17.jar"]
)
java_import(
    name = "cofoja",
    jars = ["deps/cofoja-1.3-20160207.jar"]
)

java_import(
    name = "commons_io",
    jars = ["deps/commons-io-2.4/commons-io-2.4.jar"]
)

java_import(
    name = "log4j_api",
    jars = ["deps/apache-log4j-2.5-bin/log4j-api-2.5.jar"]
)
java_import(
    name = "log4j_core",
    jars = ["deps/apache-log4j-2.5-bin/log4j-core-2.5.jar"]
)

java_library(
    name = "commandline",
    srcs = ["src/main/java/com/haplox/info/Greeting.java"],
    visibility = ["//src/main/java/com/haplox/info/cmdline:__pkg__"]
)
