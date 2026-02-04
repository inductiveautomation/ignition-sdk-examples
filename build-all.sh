#!/bin/bash

# Build script for all Ignition SDK example projects
# Automatically detects Maven (pom.xml) or Gradle (build.gradle/build.gradle.kts) projects

set -e  # Exit on any error

# Projects to ignore (temporarily broken or excluded from builds)
IGNORE_LIST=()

# Color output for better readability
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Track overall success
overall_success=0
successful_builds=()
failed_builds=()

# Get list of directories (excluding hidden and special directories)
directories=$(find . -maxdepth 1 -type d ! -name "." ! -name "..*" ! -name ".git" ! -name ".github" ! -name ".idea" | sort)

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Building All Ignition SDK Examples${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

for dir in $directories; do
    project_name=$(basename "$dir")

    # Check if project is in ignore list
    if [[ " ${IGNORE_LIST[@]} " =~ " ${project_name} " ]]; then
        echo -e "${YELLOW}⚠ Skipping ${project_name} (in ignore list)${NC}"
        echo ""
        continue
    fi

    echo -e "${YELLOW}>>> Building: ${project_name}${NC}"

    cd "$dir"

    # Detect project type and build accordingly
    if [ -f "pom.xml" ]; then
        echo -e "${BLUE}Detected Maven project${NC}"
        if mvn clean package -B -q; then
            echo -e "${GREEN}✓ ${project_name} built successfully${NC}"
            successful_builds+=("$project_name")
        else
            echo -e "${RED}✗ ${project_name} build failed${NC}"
            failed_builds+=("$project_name")
            overall_success=1
        fi
    elif [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
        echo -e "${BLUE}Detected Gradle project${NC}"
        if ./gradlew clean build -q --console=plain; then
            echo -e "${GREEN}✓ ${project_name} built successfully${NC}"
            successful_builds+=("$project_name")
        else
            echo -e "${RED}✗ ${project_name} build failed${NC}"
            failed_builds+=("$project_name")
            overall_success=1
        fi
    else
        echo -e "${YELLOW}⚠ No build file found (pom.xml, build.gradle, or build.gradle.kts)${NC}"
        echo -e "${YELLOW}⚠ Skipping ${project_name}${NC}"
    fi

    cd ..
    echo ""
done

# Print summary
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Build Summary${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Successful builds: ${#successful_builds[@]}${NC}"
for project in "${successful_builds[@]}"; do
    echo -e "  ${GREEN}✓${NC} $project"
done

if [ ${#failed_builds[@]} -gt 0 ]; then
    echo ""
    echo -e "${RED}Failed builds: ${#failed_builds[@]}${NC}"
    for project in "${failed_builds[@]}"; do
        echo -e "  ${RED}✗${NC} $project"
    done
fi

echo ""
if [ $overall_success -eq 0 ]; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}All builds completed successfully!${NC}"
    echo -e "${GREEN}========================================${NC}"
else
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}Some builds failed!${NC}"
    echo -e "${RED}========================================${NC}"
fi

exit $overall_success
