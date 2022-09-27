### How to setup your database.

To correctly setup your database, you will need Postgres 10+ with the Postgis extension.

A Dockerfile is available : `docker/PostgisDockerfile`
An ready to use version is available at https://hub.docker.com/repository/docker/dontpanic57/postgis

Once installed and running, you can create a database called `elen`. Be sure to check the correct database name
on `conf/conf.d/jdbc.conf`

Then, execute the following command on your database.

```sql
    CREATE EXTENSION unaccent;
CREATE EXTENSION postgis;
```

The unaccent extension is necessary and will ensure that the project will correctly ignore the accents.

### How to setup with docker.

You can either deploy with docker swarm or docker but docker swarm is recommended.

For docker swarm. You will need to edit the file stack.yml to potentially change the settings.

```shell script
#> docker stack deploy -c stack.yml elen
```

By default the port 10060 is publicly exposed.

You can use a traefik frontend directly in docker and handle the HTTP(s) traffic.

### Environment variable to use.

In order to work properly in development or production, the project need some information. These information are
retrieved from environment variable and override some parameters of the project configuration
from `conf/application.conf`.

The variables are detailed bellow.

| Config key                          | Env variable                    | Default value                          | Comment                                                                                                                             |
|-------------------------------------|---------------------------------|----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| baseUrl                             | SERVER_BASE_URL                 | http://localhost:9000                  | REQUIRED - Default URL of the service. It is used to generate some absolute URL when a web-context is not available.                |
| play.http.secret.key                | PLAY_HTTP_SECRETKEY             | {RANDOM}                               | REQUIRED - A secret key that is used for securing the service cookies. Shall not be modified later !                                |
| settings.displayTimezone            | DISPLAY_TIMEZONE                | Europe/Paris                           | Timezone that will be used for all the time on the service. (That does not include the database as PostgreSQL does handle timezone) |
| settings.bookedCare.monthsInAdvance | BOOKED_CARE_MONTHS_IN_ADVANCE   | 3                                      | How many month in advance does the cares need to be generated (for recuring cares without end date)                                 |
| sendgrid.api-key                    | SENDGRID_API_KEY                |                                        | REQUIRED - The API of your Sendgrid account                                                                                         |
| sendgrid.from.email                 | SENDGRID_DEFAULT_FROM           |                                        | REQUIRED - The default email to use when sending emails                                                                             |
| sendgrid.from.name                  | SENDGRID_DEFAULT_NAME           | Elen - No reply                        | The default name to use                                                                                                             |
| email.validation.from.email         | EMAIL_VALIDATION_FROM_EMAIL     |                                        | Email address that will send the email validation emails.                                                                           |
| email.validation.from.name          | EMAIL_VALIDATION_FROM_NAME      | Elen - No reply                        | Name displayed as sender for the email validation emails.                                                                           |
| email.validation.tokenTtl           | EMAIL_VALIDATION_TTL            | 3600                                   | TTL of the token in seconds for the email validation emails.                                                                        |
| email.resetPassword.from.email      | EMAIL_RESET_PASSWORD_FROM_EMAIL |                                        | Email address that will send the email for a password reset.                                                                        |
| email.resetPassword.from.name       | EMAIL_RESET_PASSWORD_FROM_NAME  | Elen - No reply                        | Name displayed as sender for the reset password email.                                                                              |
| email.resetPassword.tokenTtl        | EMAIL_RESET_PASSWORD_TTL        | 3600                                   | TTL of the token in seconds for the reset password.                                                                                 |
| email.welcome.from.email            | EMAIL_WELCOME_FROM_EMAIL        |                                        | REQUIRED - Email address that will send the welcome email.                                                                          |
| email.welcome.from.name             | EMAIL_WELCOME_FROM_NAME         | Elen - Marwa                           | Name displayed as sender for the welcome emails.                                                                                    |
| email.welcome.replyTo.email         | EMAIL_WELCOME_REPLY_TO_EMAIL    | marwa@elen.agency                      | Email address to reply to when replying to the welcome email.                                                                       |
| email.welcome.replyTo.name          | EMAIL_WELCOME_REPLY_TO_NAME     | Marwa                                  | Name displayed for the reply to.                                                                                                    |
| email.careCreated.from.email        | EMAIL_CARE_CREATE_FROM_EMAIL    |                                        | REQUIRED - The email sending the new Care emails.                                                                                   |
| email.careCreated.from.name         | EMAIL_CARE_CREATE_FROM_NAME     | Elen - New Care Created !              | Name displayed for the new Care emails.                                                                                             |
| email.careCreated.to                | EMAIL_CARE_CREATE_TO            | marwa@elen.agency                      | REQUIRED - The email notified by a new Care being created                                                                           |
| google.apiKey                       | GOOGLE_API_KEY                  |                                        | REQUIRED - Google API Key (Check the google API section for more information)                                                       |
| google.apiKeyServerSide             | GOOGLE_API_KEY_SERVER_SIDE      |                                        | REQUIRED - Google API Key On the Server Side (Check the google API section for more information)                                    |
| pac4j.google.clientId               | PAC4J_GOOGLE_CLIENTID           |                                        | REQUIRED - Client ID for google OAuth                                                                                               |
| pac4j.google.secret                 | PAC4J_GOOGLE_SECRET             |                                        | REQUIRED - Secret for google OAuth                                                                                                  |
| pac4j.facebook.clientId             | PAC4J_FACEBOOK_CLIENTID         |                                        | REQUIRED - Client ID for facebook OAuth                                                                                             |
| pac4j.facebook.secret               | PAC4J_FACEBOOK_SECRET           |                                        | REQUIRED - Secret for facebook OAuth                                                                                                |
| db.default.driver                   | JDBC_DEFAULT_DRIVER             | org.postgis.DriverWrapperLW            | Driver JDBC for the database (Postgres)                                                                                             |
| db.default.url                      | JDBC_DEFAULT_URL                | jdbc:postgresql_lwgis://127.0.0.1/elen | JDBC connexion URL                                                                                                                  |
| db.default.username                 | JDBC_DEFAULT_USERNAME           | postgres                               | Postgres username                                                                                                                   |
| db.default.password                 | JDBC_DEFAULT_PASSWORD           | toor                                   | Postgres password                                                                                                                   |
| redis.host                          | PLAY_REDIS_HOST                 | 127.0.0.1                              | REQUIRED - Redis host                                                                                                               |
| redis.port                          | PLAY_REDIS_PORT                 | 6379                                   | REQUIRED - Redis port                                                                                                               |
| redis.password                      | PLAY_REDIS_PASSWORD             |                                        | Redis password                                                                                                                      |
| redis.defaultdb                     | PLAY_REDIS_DEFAULT_DB           | 1                                      | Redis database used for temporary informations                                                                                      |
| play.cache.redis.database           | PLAY_CACHE_REDIS_DATABASE       | 0                                      | Redis database used by PAC4J cache                                                                                                  |

### PAC4J and the authentication

The local authentication should work out of the box but Google and Facebook require you to create some clients.

##### Google

You will need to create a project on the google console. (https://console.cloud.google.com/)

Then on the credentials page (https://console.cloud.google.com/apis/credentials) you will need to create an OAuth 2.0
Client ID AND an API Key.

The client ID and secret need to be set on the environment variable `PAC4J_GOOGLE_CLIENTID` and `PAC4J_GOOGLE_SECRET`.

The API Key will need to be set on the environment variable `GOOGLE_API_KEY`.

You will also need to enable the proper APIs libraries (https://console.cloud.google.com/apis/library). Under the `Maps`
category, enable the following APIs :

- Geocoding API
- Maps Embed API
- Maps Javascript API
- Places API

You will also need to restrict your API key usage to avoid problems.

##### Facebook

You will need a facebook developer account. (https://developers.facebook.com/)

Once logged in, create an App. The name of your app will be displayed to the users.

The client ID for facebook is your APP ID which is available on the top of the page when the app is selected. The secret
is available from the page `Settings/General`.

The client ID and secret need to set on the environment variable `PAC4J_FACEBOOK_CLIENTID` and `PAC4J_FACEBOOK_SECRET`.

By default only the account linked to the developer account will be able to log in the application. You can create some
test users. But if you want your service to be publicly available, you will need to verify your company and then have
your application verified by Facebook.

### Nginx as a front webserver

It's easy to setup an Nginx server as a front webserver. Here is an example about how to do it with the SSL using let's
encrypt.

```
upstream elen-prototype.sector5.fr {
    server 127.0.0.1:10060;
}

server {
    if ($host = elen-prototype.sector5.fr) {
        return 301 https://$host$request_uri;
    } # managed by Certbot

    listen 80;
    listen [::]:80;
    server_name elen-prototype.sector5.fr;
    # enforce https
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name elen-prototype.sector5.fr;

    ssl_certificate /etc/letsencrypt/live/elen-prototype.sector5.fr/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/elen-prototype.sector5.fr/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

    access_log /var/log/nginx/elen-prototype.sector5.fr.access.log;
    error_log /var/log/nginx/elen-prototype.sector5.fr.error.log;

    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header X-Robots-Tag none;
    add_header X-Download-Options noopen;
    add_header X-Permitted-Cross-Domain-Policies none;
    add_header X-Frame-Options SAMEORIGIN;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header Referrer-Policy "same-origin" always;

    location / {
        proxy_http_version      1.1;

        proxy_set_header        Host $host;
        proxy_set_header        X-Real-IP $remote_addr;
        proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header        X-Forwarded-Proto $scheme;

        proxy_pass              http://elen-prototype.sector5.fr;
        proxy_redirect          http://elen-prototype.sector5.fr https://elen-prototype.sector5.fr;
    }
}
```
