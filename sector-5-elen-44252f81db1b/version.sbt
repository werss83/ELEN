import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

version in ThisBuild := sys.props
  .get("projectVersion")
  .orElse(sys.env.get("PROJECT_VERSION"))
  .getOrElse(
    s"${LocalDateTime.now().format(DateTimeFormatter.ofPattern("YY.MM"))}-SNAPSHOT"
  )
