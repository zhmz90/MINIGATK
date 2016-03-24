java_binary(
    name = "myApp",
    srcs = glob(["**/*App.java"]),
    main_class = "com.haplox.info.App",
    deps = [":greeter"],
)

java_binary(
    name = "mini",
    srcs = glob(["**/*.java"]),
    main_class = "com.haplox.info.CommandLineGATK.java",
    deps = [":gatk"]
)

java_import(
    name = "gatk",
    jars = ["deps/GenomeAnalysisTK.jar"]
)

java_import(
    name = "gatk-utils",
    jars = ["deps/gatk-utils-3.5.jar"]
)

java_import(
    name = "gatk-engine",
    jars = ["deps/gatk-engine-3.5.jar"]
)

maven_server(
    name = "junit",
    artifact="junit:junit-3.8.2.jar"
)


java_library(
    name = "greeter",
    srcs = ["src/main/java/com/haplox/info/Greeting.java"],
    visibility = ["//src/main/java/com/haplox/info/cmdline:__pkg__"]
)
