#!/usr/bin/env bash
# Author : Pierre Adam

set -e

BOOTSTRAP_NAME=$(basename "$0")
VERSION="20.06"
COMMAND_PARAMETERS=()
COMMAND_PARAMETERS+=("-Dpidfile.path=/dev/null")
DEBUG_MODE=false
BINARY=""

function usage() {
    echo "$BOOTSTRAP_NAME [FLAGS] Binary [Arguments [...]]" >&2
    echo "" >&2
    echo "  Binary     Path to the play framework binary to run" >&2
    echo "  Arguments  Explicit arguments you want to give directly to the binary" >&2
    echo "" >&2
    echo "Flags:" >&2
    echo "  -d  --debug     Enable the script debug. The script will output the command it would normally run" >&2
    echo "  -h  --help      Show this help" >&2
    echo "      --version   Show the version of this script" >&2
    echo "" >&2
    echo "ENVIRONMENT VARIABLES" >&2
    echo "=====================" >&2
    echo "Logging" >&2
    echo "  LOGGER_RESOURCE             Specify a logback file to be loaded from the classpath (inside the conf folder)" >&2
    echo "  LOGGER_FILE                 Specify a logback file to be loaded from the system (a linux path is required)" >&2
    echo "  LOGGER_DEBUG                Enable the debugging on logback" >&2
}

while [[ $# -gt 0 ]]; do
    case "$1" in
    "-d" | "--debug")
        DEBUG_MODE=true
        ;;
    "--version")
        echo "PlayFramework Bootstrap $VERSION"
        exit 0
        ;;
    "-h" | "--help")
        usage
        exit 0
        ;;
    *)
        if [[ -z "$BINARY" ]]; then
            BINARY="$1"
        else
            COMMAND_PARAMETERS+=("$1")
        fi
        ;;
    esac
    shift
done

if [[ -z "$BINARY" ]]; then
    echo "Binary is not specified" >&2
    usage
    exit 1
fi

if [[ ! -z "${LOGGER_RESOURCE}" ]]; then
    COMMAND_PARAMETERS+=("-Dlogger.resource=${LOGGER_RESOURCE}")
elif [[ ! -z "${LOGGER_FILE}" ]]; then
    COMMAND_PARAMETERS+=("-Dlogger.file=${LOGGER_FILE}")
fi

if [[ ! -z "${LOGGER_DEBUG}" ]]; then
    COMMAND_PARAMETERS+=("-Dlogback.debug=true")
fi

if $DEBUG_MODE; then
    echo "$BINARY" "${COMMAND_PARAMETERS[@]}"
else
    echo "Starting using PlayFramework Bootstrap version ${VERSION}"
    "$BINARY" "${COMMAND_PARAMETERS[@]}"
fi
