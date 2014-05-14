/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import sbt.Classpaths.publishTask
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import scala.util.Properties
import org.scalastyle.sbt.ScalastylePlugin.{Settings => ScalaStyleSettings}
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact
import com.typesafe.sbt.pom.{PomBuild, SbtPomKeys}
import net.virtualvoid.sbt.graph.Plugin.graphSettings

object SparkBuild extends PomBuild {

  val sparkVersion = "1.0.0-SNAPSHOT"

  val buildLocation = (file(".").getAbsoluteFile.getParentFile)

  override val profiles = Seq()

  lazy val sharedSettings = graphSettings ++ ScalaStyleSettings ++ MimaBuild.mimaSettings(file(sparkHome))

  override def settings = {
    super.settings ++ Seq(SbtPomKeys.profiles := profiles)
  }

  def applySettings(projects: Seq[String], settings: Seq[Setting[_]]) = {

  }

  override def projectDefinitions(baseDirectory: File): Seq[Project] = {
    super.projectDefinitions(baseDirectory).map { x =>
      if (x.id.contains("assembly") || x.id.contains("examples") || x.id.contains("tools") )
        x.settings(assemblySettings ++ extraAssemblySettings ++ sharedSettings: _*)
      else
        x.settings(sharedSettings: _*)
    }
  }
  
  val sparkHome = System.getProperty("user.dir")

  lazy val extraAssemblySettings = Seq(
    test in assembly := {},
    mergeStrategy in assembly := {
      case PathList("org", "datanucleus", xs @ _*)             => MergeStrategy.discard
      case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
      case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
      case "log4j.properties"                                  => MergeStrategy.discard
      case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
      case "reference.conf"                                    => MergeStrategy.concat
      case _                                                   => MergeStrategy.first
    }
  )
}
