package system;

/**
 * BuildInfo. Provides abstraction to access Scala
 * auto-generated object from Java.
 *
 * @author Pierre Adam
 * @since 19.05.21
 */
public final class BuildInfo {

    /**
     * Get the UTC date when the project was built.
     *
     * @return The UTC build date
     */
    public static String getBuildDate() {
        return BuildInfo.getBuildDate(true, true);
    }

    /**
     * Get the UTC date when the project was built.
     *
     * @return The UTC build date
     */
    public static String getBuildDate(final Boolean withSeconds, final Boolean jsonFormat) {
        String buildDate;
        if (withSeconds) {
            buildDate = sbtbuildinfo.BuildInfo.builtAtString()
                .substring(0, sbtbuildinfo.BuildInfo.builtAtString()
                    .indexOf("."));
        } else {
            buildDate = sbtbuildinfo.BuildInfo.builtAtString()
                .substring(0, sbtbuildinfo.BuildInfo.builtAtString()
                    .indexOf(".") - 3);
        }
        if (jsonFormat) {
            buildDate = buildDate.replace(' ', 'T');
        }
        return buildDate;
    }

    /**
     * Get the project version.
     *
     * @return The project version
     */
    public static String getProjectVersion() {
        return sbtbuildinfo.BuildInfo.version();
    }

    /**
     * Get the project name.
     *
     * @return The project name
     */
    public static String getProjectName() {
        return sbtbuildinfo.BuildInfo.name();
    }

    /**
     * Get the version of Java used to compile project.
     *
     * @return Java version
     */
    public static String getJavaVersion() {
        return sbtbuildinfo.BuildInfo.javaVersion();
    }

    /**
     * Get the version of Scala used to compile project.
     *
     * @return Scala version
     */
    public static String getScalaVersion() {
        return sbtbuildinfo.BuildInfo.scalaVersion();
    }

    /**
     * Get the version of SBT used to compile project.
     *
     * @return SBT version
     */
    public static String getSbtVersion() {
        return sbtbuildinfo.BuildInfo.sbtVersion();
    }
}
