#!/usr/bin/env bats

setup() {
  load '/opt/test_helper/bats-support/load'
  load '/opt/test_helper/bats-assert/load'
  load '/opt/test_helper/bats-file/load'
  DIR="$( cd "$( dirname "$BATS_TEST_FILENAME" )" >/dev/null 2>&1 && pwd )"
}

@test "bats libraries should be accessible to tests" {
  run echo "test"

  assert_output "test"
}