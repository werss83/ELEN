import com.typesafe.sbt.packager.MappingsHelper._
import sbt.Keys.watchSources

// Project information
name := """elen"""
description := "Elen"
startYear := Some(2018)
organization := "com.elen"
organizationHomepage := Some(url("http://www.elen.com"))


// Project base
lazy val root = (project in file("."))
    .enablePlugins(PlayJava, PlayEbean, BuildInfoPlugin)
    .settings(
        watchSources ++= (baseDirectory.value / "public/ui" ** "*").get,
        sources in(Compile, doc) := Seq.empty,
        buildInfoKeys := BuildInfoKey.ofN(name, version, javaVersion, scalaVersion, sbtVersion),
        buildInfoPackage := "sbtbuildinfo"
    )

// Version of java
val javaVersion = settingKey[String]("The version of Java used for building.")
javaVersion := System.getProperty("java.version")

// Version of scala
scalaVersion := "2.12.11"

buildInfoOptions += BuildInfoOption.BuildTime

// Add resolvers
resolvers += "jitpack" at "https://jitpack.io"


// Library dependencies for java 9 and later
val java9AndSupLibraryDependencies: Seq[sbt.ModuleID] =
    if (!javaVersion.toString.startsWith("1.8")) {
        Seq(
            "com.sun.activation" % "javax.activation" % "1.2.0",
            "com.sun.xml.bind" % "jaxb-core" % "2.3.0",
            "com.sun.xml.bind" % "jaxb-impl" % "2.3.1",
            "javax.jws" % "javax.jws-api" % "1.1",
            "javax.xml.bind" % "jaxb-api" % "2.3.0",
            "javax.xml.ws" % "jaxws-api" % "2.3.1"
        )
    } else {
        Seq.empty
    }

val playPac4jVersion = "10.0.1"
val pac4jVersion = "4.0.1"
val playVersion = "2.8.2"

// Library dependencies
libraryDependencies ++= Seq(
    guice,
    javaWs,
    cacheApi,

    // Database driver
    "net.postgis" % "postgis-jdbc" % "2.5.0",
    "org.postgresql" % "postgresql" % "42.2.12",

    // Ebean Postgis
    "io.ebean" % "ebean-postgis" % "12.2.1",

    "joda-time" % "joda-time" % "2.10.6",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.10.4",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.4",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.10.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.10.4",
    "commons-codec" % "commons-codec" % "1.14",
    "org.apache.commons" % "commons-lang3" % "3.10",
    "org.apache.shiro" % "shiro-core" % "1.5.3",
    //"com.github.play-rconf" % "play-rconf" % "release~18.12",
    //"com.github.play-rconf" % "play-rconf-etcd" % "release~18.12",

    // Authentication
    "org.pac4j" %% "play-pac4j" % playPac4jVersion,
    "org.pac4j" % "pac4j-http" % pac4jVersion,
    "org.pac4j" % "pac4j-openid" % pac4jVersion exclude("xml-apis", "xml-apis"),
    "org.pac4j" % "pac4j-oauth" % pac4jVersion,
    "org.pac4j" % "pac4j-sql" % pac4jVersion,
    "org.pac4j" % "pac4j-oidc" % pac4jVersion exclude("commons-io", "commons-io"),
    "commons-io" % "commons-io" % "2.7",
    "be.objectify" %% "deadbolt-java" % "2.8.1",

    // Cache Implementation
    "com.github.karelcemus" %% "play-redis" % "2.6.1",

    // Mailing
    "com.sendgrid" % "sendgrid-java" % "4.5.0",

    // DataTables for Play
    "com.github.PierreAdam" % "play-ebean-datatables" % "release~20.08u1",

    // Binder for Joda
    "com.github.PierreAdam" % "PlayForm-JodaDataBinder" % "release~20.08",

    // Logging
    "io.logz.logback" % "logzio-logback-appender" % "1.0.24",

    // Redis
    "com.github.PierreAdam" % "play-redis-module" % "release~20.06u1",

    // Testing libraries for dealing with CompletionStage...
    "org.assertj" % "assertj-core" % "3.6.2" % Test,
    "org.awaitility" % "awaitility" % "2.0.0" % Test
) ++ java9AndSupLibraryDependencies

dependencyOverrides ++= Seq(
    // NEED TO DOWNGRADE JACKSON DUE TO INCOMPATIBILITIES...
    "com.fasterxml.jackson.core" % "jackson-core" % "2.10.4",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.4",

    "io.ebean" % "ebean" % "12.3.2",
    "io.ebean" % "ebean-agent" % "12.3.2",
    "io.ebean" % "ebean-migration" % "12.1.4",
)

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Disable the play enhancer
playEnhancerEnabled := false

// Scala compiler options
scalacOptions in ThisBuild ++= Seq(
    "-deprecation",
    "-unchecked"
)

// Java compiler options
javacOptions in Compile ++= Seq(
    "-Xlint:cast",
    "-Xlint:deprecation",
    "-Xlint:divzero",
    "-Xlint:empty",
    "-Xlint:fallthrough",
    "-Xlint:finally",
    "-Xlint:unchecked",
    "-s", (managedSourceDirectories in Compile).value.head.getAbsolutePath
)

mappings in Universal ++= directory(baseDirectory.value / "public")
