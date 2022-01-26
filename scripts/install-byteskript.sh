#!/bin/bash

current_dir=$(pwd)
script_dir=$(dirname "$0")

cd $script_dir
if [[ ! -e bin ]]; then
  mkdir "bin"
  cd "bin"

  echo $'#!/bin/bash\njava -jar '$script_dir'/ByteSkript.jar $@' > "bsk"
fi

if [[ ! -e /usr/local/bin/bsk ]]; then
  rm /usr/local/bin/bsk
fi

ln -s $script_dir"/bin/bsk" "/usr/local/bin/bsk"

chmod a+x $script_dir"/bin/bsk"
chmod a+x /usr/local/bin/bsk
