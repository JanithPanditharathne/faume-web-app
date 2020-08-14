#!/bin/bash

# ----------------------------------------------
# Package structure
#
# faume-web-app-[VERSION].zip/tar.gz
#     |- faume-web-app-[VERSION]
#           |- web-app.sh
#           |- bin
#           |   |- faume-web-app.jar
#           |- conf
#               |- application.properties
#               |- logback.xml
#
# ---------------------------------------------

# Script arguments (start, stop, restart, status, forcekill)
COMMAND=$1

JAR_NAME=bin/faume-web-app.jar
APP_PROPERTIES_FILE_PATH="conf/application.properties"
APP_DIR="."
ADDITIONAL_ARGS=""

# =======================
# Helper functions begins
# =======================
echoProgress()
{
    setColor 6
    printf "%-70s" "$1..."
    resetColor
    return 0
}

echoError()
{
    setColor 1
    printf "ERROR"
    if [ ! -z "$1" ]
    then
            resetColor
            printf "%s" " [$1]"
    fi
    printf "\n"
    resetColor
    return 0
}

echoOK()
{
    setColor 2
    printf "OK"
    if [ ! -z "$1" ]
    then
            resetColor
            printf "%s" " [$1]"
    fi
    printf "\n"
    resetColor
    return 0
}

checkResult()
{
    if [ "$1" -ne 0 ]
    then
            echoError "$2"
            exit 1
    fi
}

setColor()
{
    tput setaf "$1" 2>/dev/null
}

resetColor()
{
    tput sgr0 2>/dev/null
}

# ====================
# Helper functions end
# ====================


# =====================
# Core functions begins
# =====================
# Checks if RUNNING_PID file exists and whether the process is really running.
checkPidFile()
{
    if [ -f $PID_FILE ]
    then
            if ps -p $(cat "$PID_FILE") > /dev/null
            then
                    SAFE_CHECK=$(ps "$(cat $PID_FILE)" | grep "$JAR_NAME")

                    if [ -z "$SAFE_CHECK" ]
                    then
                            # Process ID doesn't belong to expected application!
                            return 3
                    else
                            # The file exists and the process is running
                            return 1
                    fi

            else

                    SAFE_CHECK=$(ps "$(cat $PID_FILE)" | grep "$JAR_NAME")

                    if [ -z "$SAFE_CHECK" ]
                    then
                            # Process ID doesn't belong to expected application!
                            return 3
                    else
                            # The file exists, but the process is dead
                            return 2
                    fi
            fi
    fi

    # The file doesn't exist
    return 0
}

# Gently kill the given process
kill_softly()
{
    # Checks whether the application is running and the process is related to the given application.
    SAFE_CHECK=$(ps "$@" | grep "$JAR_NAME")
    if [ -z "$SAFE_CHECK" ]
    then
            # Process ID doesn't belong to expected application! Don't kill it!
            return 1
    else
            # Send termination signals one by one
            for sig in TERM HUP INT QUIT PIPE KILL; do
                    if ! kill -$sig "$@" > /dev/null 2>&1 ;
                    then
                            break
                    fi
                    sleep 2
            done
            rm $PID_FILE

    fi
}

# Get process ID from RUNNING_PID file and print it
printPid()
{
    PID=$(cat "$PID_FILE")
    printf "%s" "PID=$PID"
}

# Check port input argument
checkPort()
{
    if [ -z "$PORT" ]
    then
            echoError "Port not set!"
            return 1
    fi
}

# Check input arguments
checkArgs()
{
    # Check command
    case "$COMMAND" in
            start | stop | restart | status | forcekill) ;;
            *)
                    echoError "Unknown command"
                    return 1
            ;;
    esac

    # Check application name
    if [ -z "$JAR_NAME" ]
    then
            echoError "Jar name not set!"
            return 1
    fi

    # Check application directory
    if [ -z "$APP_DIR" ]
    then
            echoError "Application installation directory not set!"
            return 1
    fi

    # Check port
    case "$COMMAND" in
            start | restart)
                    checkPort
                    if [ $? != 0 ]
                    then
                            return 1
                    fi
            ;;
    esac
}

# Check property files path and existence
checkPropertyFiles() {
  if [ ! -z "$APP_PROPERTIES_FILE_PATH" ]
    then
        if [ ! -f $APP_PROPERTIES_FILE_PATH ]
        then
            echoError "Application properties file not found!"
            return 1
        fi
    else
        echoError "Application properties file path not mentioned in script."
        return 1
    fi
}

checkAppStarted()
{
    # Wait a bit
    sleep 10

    # Check if RUNNING_PID file exists and if process is really running
    checkPidFile
    if [ $? != 1 ]
    then
            echoError
            cat "$TMP_LOG" 1>&2

            # If the app is not started correctly, remove the PID file.
            if [ -f $PID_FILE ]
            then
              rm $PID_FILE
            fi

            exit 1
    fi

}

# ==================
# Main script begins
# ==================

# Check property files
checkPropertyFiles
if [ $? != 0 ]
then
    exit 1
fi

# Prepare additonal arguments related to properties and port
ADDITIONAL_ARGS="--spring.config.location=${APP_PROPERTIES_FILE_PATH} ${ADDITIONAL_ARGS}"

PORT=$(cat "$APP_PROPERTIES_FILE_PATH" | grep "server.port" | grep -o "[0-9].*")
echo "[INFO] Port set to $PORT in $APP_PROPERTIES_FILE_PATH"

APP_ARGS="${ADDITIONAL_ARGS}"
PID_FILE=$APP_DIR/RUNNING_PID

# Check input arguments
checkArgs
if [ $? != 0 ]
then
        echo "Usage: $0 {start|stop|status|restart|forcekill}"
        exit 1
fi

case "${COMMAND}" in
        start)
                echoProgress "Starting $JAR_NAME at port $PORT"

                checkPidFile
                case $? in
                        1)      echoOK "$(printPid) already started"
                                exit ;;
                        2)      # Delete the RUNNING_PID FILE
                                rm $PID_FILE ;;
                        3)      echoError "RUNNING_PID doesn't belong to the application. RUNNING_PID file corrupted. Application state unknown."
                                exit ;;

                esac

                # * * * Run the Spring boot application * * *
                TMP_LOG=$(mktemp)
                PID=$(java -jar $APP_DIR/$JAR_NAME $APP_ARGS> /dev/null 2>"$TMP_LOG" & echo $!)

                # Check if successfully started
                if [ $? != 0 ]
                then
                        echoError
                        exit 1
                else
                        echo $PID > $PID_FILE
                        checkAppStarted
                        echoOK "PID=$PID"
                fi
        ;;
        status)
                echoProgress "Checking $JAR_NAME at port $PORT"
                checkPidFile
                case $? in
                        0)      echoOK "not running" ;;
                        1)      echoOK "$(printPid) running" ;;
                        2)      echoError "process dead but RUNNING_PID file exists" ;;
                        3)      echoError "RUNNING_PID doesn't belong to the application. RUNNING_PID file corrupted. Application state unknown." ;;
                esac
        ;;
        stop)
                echoProgress "Stopping $JAR_NAME"
                checkPidFile
                case $? in
                        0)      echoOK "wasn't running" ;;
                        1)      PRINTED_PID=$(printPid)
                                kill_softly $(cat "$PID_FILE")
                                if [ $? != 0 ]
                                then
                                        echoError "$PRINTED_PID doesn't belong to $JAR_NAME! Human intervention is required."
                                        exit 1
                                else
                                        echoOK "$PRINTED_PID stopped"
                                fi ;;
                        2)      echoError "RUNNING_PID exists but process is already dead" ;;
                        3)      echoError "RUNNING_PID doesn't belong to the application. RUNNING_PID file corrupted. Application state unknown." ;;
                esac
        ;;

        restart)
                $0 stop $JAR_NAME $PORT
                if [ $? == 0 ]
                then
                        $0 start $JAR_NAME $PORT
                        if [ $? == 0 ]
                        then
                                # Success
                                exit
                        fi
                fi
                exit 1
        ;;

        forcekill)
                ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}' | xargs kill
                if [ -f $PID_FILE ]
                then
                    rm $PID_FILE
                fi
        ;;
esac
# ===============
# Main script end
# ===============