#!/bin/sh

echo 'Copie des fichiers pour le GUI en java dans /opt/visualboyadvancelink/'
sleep 3
cp -dR opt /
chmod 777 -R /opt/visualboyadvancelink/

echo 'Copie du fichier desktop dans /usr/share/applications/'
sleep 3
cp -dR /opt/visualboyadvancelink/bin/vbal.desktop /usr/share/applications/

echo 'Installation de VisualBoyAdvance Link avec le support Link'
sleep 3
./configure --prefix=/opt/visualboyadvancelink/ && make && make install

echo 'Programme installé'
sleep 3

chmod 777 -R /opt/visualboyadvancelink/
rm /opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg
