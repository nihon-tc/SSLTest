export CLASSPATH=`pwd`/bcprov-jdk15on-1.56.jar
CERTSTORE=mystore.bks
if [ -a $CERTSTORE ]; then
    rm $CERTSTORE || exit 1
fi

keytool \
      -import \
      -v \
      -trustcacerts \
      -alias 0 \
      -file <(openssl x509 -in mycert.pem) \
      -keystore $CERTSTORE \
      -storetype BKS \
      -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
      -providerpath `pwd`/bcprov-jdk15on-1.56.jar \
      -storepass android