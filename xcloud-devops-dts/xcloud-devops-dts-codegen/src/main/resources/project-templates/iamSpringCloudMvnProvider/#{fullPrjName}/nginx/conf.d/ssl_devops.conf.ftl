#
# ${projectDescription} (HTTPS/SSL) configuration.
#

server {
    listen 443;
    server_name ${proServiceHost};
    include /etc/nginx/default.d/*.conf;
    location / {
       root /usr/share/nginx/html/${projectName?lower_case}-view-package/${projectName?lower_case}-view-${version}-bin;
       index index.html;
    }
}

server {
    listen 443;
    server_name ${proViewServiceHost};
    include /etc/nginx/default.d/*.conf;
    location / {
       proxy_pass http://localhost:${entryAppPort}; break;
    }
}
