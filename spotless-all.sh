#!/bin/bash

# Runs Spotless in every example project, formatting Java sources with google-java-format.
#
# Usage:
#   ./spotless-all.sh check   # report unformatted files and fail if there are any
#   ./spotless-all.sh apply   # format the files in place

set -u

case "${1:-}" in
    check) goal=check gradle_task=spotlessCheck ;;
    apply) goal=apply gradle_task=spotlessApply ;;
    *)
        echo "Usage: $0 check|apply" >&2
        exit 2
        ;;
esac

cd "$(dirname "$0")"

failed=()

for dir in */; do
    project="${dir%/}"

    if [ -f "$project/pom.xml" ]; then
        echo ">>> $project (Maven): spotless:$goal"
        (cd "$project" && mvn -B -q "spotless:$goal") || failed+=("$project")
    elif [ -f "$project/build.gradle" ] || [ -f "$project/build.gradle.kts" ]; then
        echo ">>> $project (Gradle): $gradle_task"
        (cd "$project" && ./gradlew -q --console=plain "$gradle_task") || failed+=("$project")
    fi
done

echo ""
if [ ${#failed[@]} -gt 0 ]; then
    echo "spotless $goal failed in: ${failed[*]}"
    [ "$goal" = "check" ] && echo "Run ./spotless-all.sh apply to fix formatting."
    exit 1
fi

echo "spotless $goal succeeded in all examples."
