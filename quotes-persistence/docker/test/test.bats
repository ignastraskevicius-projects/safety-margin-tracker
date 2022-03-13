#!/usr/bin/env bats

setup() {
  load 'test_helper/bats-support/load'
  load 'test_helper/bats-assert/load'
  load 'test_helper/bats-file/load'
  DIR="$( cd "$( dirname "$BATS_TEST_FILENAME" )" >/dev/null 2>&1 && pwd )"
  PATH="$DIR/../src:$PATH"

  mkdir -p /tmp/flyway
}

teardown() {
  rm -rf /tmp/flyway
}

@test "should require scripts directory to be supplied" {
  run makeScriptsUseQuotesSchema.sh 

  assert_output "Missing argument: directory containing flyway scripts"
}

@test "should fail if supplied directory does not exist" {
  run makeScriptsUseQuotesSchema.sh /tmp/nonExistentDirectory

  assert_output "Invalid argument: supplied directory does not exist"
}

@test "should add schema usage statement to an empty script" {
  touch /tmp/flyway/V1__empty_script.sql
  makeScriptsUseQuotesSchema.sh /tmp/flyway

  run cat /tmp/flyway/V1__empty_script.sql

  assert_output "USE quotes;"
}

@test "should prepend schema usage statement to a non-empty script" {
  echo "someScript" > /tmp/flyway/V1__empty_script.sql
  makeScriptsUseQuotesSchema.sh /tmp/flyway

  run cat /tmp/flyway/V1__empty_script.sql

  assert_output --regexp "^USE quotes;.someScript$"
}

@test "should add schema usage statement to multiple scripts" {
  touch /tmp/flyway/V1__script1.sql
  touch /tmp/flyway/V2__script2.sql
  makeScriptsUseQuotesSchema.sh /tmp/flyway

  run cat /tmp/flyway/V1__script1.sql
  assert_output "USE quotes;"

  run cat /tmp/flyway/V2__script2.sql
  assert_output "USE quotes;"
}

@test "should not create extra files" {
  echo "someScript" > /tmp/flyway/V1__empty_script.sql
  makeScriptsUseQuotesSchema.sh /tmp/flyway

  run count_lines_in_flyway_dir

  assert_output "4"
}

count_lines_in_flyway_dir() {
  ls -la /tmp/flyway | wc -l
}

