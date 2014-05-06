import sbt._
import sbt.Keys._
import com.typesafe.sbt.pom.{PomBuild, SbtPomKeys}
import net.virtualvoid.sbt.graph.Plugin.graphSettings


object SparkBuild extends PomBuild {
  val SPARK_VERSION = "1.0-SNAPSHOT"
  override val profiles = Seq("yarn", "hadoop-2.2")

  def sharedSettings = Seq(
    SbtPomKeys.profiles := profiles
  )

  override def settings = {
    super.settings ++ sharedSettings
  }

  override def projectDefinitions(baseDirectory: File): Seq[Project] = {
    super.projectDefinitions(baseDirectory).map { x =>
      x.settings(graphSettings: _*)
    }
  }
}
