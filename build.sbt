organization := "org.charleso"

name := "property-testing"

version in ThisBuild := "0.1.0"

scalaVersion := "2.11.4"

scalacOptions := Seq(
//  "-deprecation",
  "-unchecked",
  "-optimise",
  "-Xlint",
  "-Ywarn-value-discard",
  "-Yno-adapted-args",
//  "-Xfatal-warnings",
  "-Yinline-warnings",
  "-feature",
  "-language:_"
)

libraryDependencies ++= Seq(
   "org.scalaz"              %% "scalaz-core"                  % "7.1.0"
 , "org.scalaz"              %% "scalaz-scalacheck-binding"    % "7.1.0"   % "test"
 , "io.argonaut"             %% "argonaut"                     % "6.1-M4"
 , "joda-time"               %  "joda-time"                    % "2.1"
 , "org.joda"                %  "joda-convert"                 % "1.2"
 , "org.specs2"              %% "specs2-core"                  % "2.4.8"   % "test"
 , "org.specs2"              %% "specs2-scalacheck"            % "2.4.8"   % "test"
 , "org.scalacheck"          %% "scalacheck"                   % "1.11.3"  % "test"
 , "com.ambiata"             %% "disorder"                     % "0.0.1-20141222035214-f1cb255" % "test"
 , "com.googlecode.json-simple"     % "json-simple"            % "1.1"
 , "com.eclipsesource.minimal-json" % "minimal-json"           % "0.9.2"
 , "com.googlecode.jcsv"            % "jcsv"                   % "1.4.0"
 , "commons-codec"                  % "commons-codec"          % "1.6"
 , "org.apache.commons"             % "commons-csv"            % "1.1"
)
