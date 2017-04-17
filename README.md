# SSLTest

see 
   http://exception-think.hatenablog.com/entry/20170217/1487341800


## BKS-v1 key create(support old bks key)

portecle-1.9/portecle.jar run

Open your bks file with the password and portecle

Do Tools>>Change Keystore Type>>BKS-v1

Save the file


## BKS key create

   http://blog.crazybob.org/2010/02/android-trusting-ssl-certificates.html

   mycert.pem using upper site

   create_bks.sh run

## der key create

	http://qiita.com/tkc24@github/items/eb9cdb978e143284de7c


openssl genrsa 2048 > server.key
openssl req -new -key server.key > server.csr
	pass android
openssl x509 -req -in server.csr -signkey server.key -out server.crt -days 3650 -extfile v3.ext


### check key

penssl x509 -in server.crt -text -noout