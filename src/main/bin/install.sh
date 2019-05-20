#!/bin/bash

while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    --loadDemo)
    LOAD_DEMO=true
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

if [ "$LOAD_DEMO" == "true" ]; then
    loadDemo=true
else
    loadDemo=false
fi

CONF_FOLDER=${pkg.installFolder}/conf
configfile=${pkg.name}.conf
jarfile=${pkg.installFolder}/bin/${pkg.name}.jar
# installDir=${pkg.installFolder}/data

source "${CONF_FOLDER}/${configfile}"

run_user=${pkg.name}

su -s /bin/sh -c "java -cp ${jarfile} $JAVA_OPTS -Dloader.main=com.github.rulegin.InsApplication \
                    -Dinstall.data_dir=${installDir} \
                    -Dinstall.load_demo=${loadDemo} \
                    -Dspring.jpa.hibernate.ddl-auto=none \
                    -Dinstall.upgrade=false \
                    -Dlogging.config=${pkg.installFolder}/bin/install/logback.xml \
                    org.springframework.boot.loader.PropertiesLauncher" "$run_user"

if [ $? -ne 0 ]; then
    echo "rulegin installation failed!"
else
    echo "rulegin installed successfully!"
fi

exit $?
