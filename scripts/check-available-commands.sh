function checkCommandsAvailable() {
  for var in "$@"
  do
    if ! [ -x "$(command -v "$var")" ]; then
      echo "🔥 ${var} is not installed." >&2
      exit 1
    else
      echo "🏄 $var is installed..."
    fi
  done
}
