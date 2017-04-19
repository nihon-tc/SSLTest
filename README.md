# SSLTest

see 
   http://exception-think.hatenablog.com/entry/20170217/1487341800


## BKS key create

   http://blog.crazybob.org/2010/02/android-trusting-ssl-certificates.html

   valid_ca.pem under google test data
   		https://android.googlesource.com/platform/cts/+/master/tests/tests/networksecurityconfig/networksecurityconfig-downloadmanager/res/xml/network_security_config.xml?autodive=0%2F%2F%2F%2F%2F

>   ./create_bks.sh


## BKS-v1 key create(support old bks key)

> java -jar portecle-1.9/portecle.jar

Open your bks file with the password and portecle

Do Tools>>Change Keystore Type>>BKS-v1

Save As the file mystore-v1.bks


## cer key create

	http://qiita.com/tkc24@github/items/eb9cdb978e143284de7c


> openssl genrsa 2048 > server.key
> openssl req -new -key server.key > server.csr
	pass android
> openssl x509 -req -in server.csr -signkey server.key -out server.crt -days 3650 -extfile v3.ext


### check key

> openssl x509 -in server.crt -text -noout


### charies using localhost test

ex) http://localhost:44917/api/v1/hoge/

need 

> adb forward tcp:44917 tcp:44917