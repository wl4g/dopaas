#
# HTTPS/SSL header configuration.
#

ssl on;
ssl_certificate cert/all.${proTopDomain}.pem;
ssl_certificate_key cert/all.${proTopDomain}.key;
ssl_session_timeout 5m;
ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
ssl_prefer_server_ciphers on;
