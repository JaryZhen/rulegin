#!/bin/bash

CONF_FOLDER=${pkg.installFolder}/conf
configfile=${pkg.name}.conf
jarfile=${pkg.installFolder}/bin/${pkg.name}.jar
# installDir=${pkg.installFolder}/data

source "${CONF_FOLDER}/${configfile}"

run_user=${pkg.name}

su -s /bin/sh -c "java -cp ${jarfile} $JAVA_OPTS org.springframework.boot.loader.PropertiesLauncher" "$run_user"


if [ $? -ne 0 ]; then
    echo "neurule installation failed!"
else
    echo "neurule installed successfully!"
fi

exit $?
