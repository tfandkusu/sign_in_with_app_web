[![CircleCI](https://circleci.com/gh/tfandkusu/sign_in_with_apple_web/tree/master.svg?style=svg)](https://circleci.com/gh/tfandkusu/sign_in_with_apple_web/tree/master)

# Sign in with Apple test Web page

Try Sign in with Apple for Web page.
It can deploy from CircleCI to Google App Engine for Java.

[Demo page](https://siwatest-dot-tfandkusu.appspot.com/login)

# How to deploy

Set environment variables.
If you use CircleCI, these variables are set to `Environment Variables` in the project settings.

|  Name  |  Value  |
| ---- | ---- |
| APPLE_CLIENT_ID | Identifer of Services IDs in Apple Developer |
| GOOGLE_PROJECT_ID  |  GCP Project ID  |
| GOOGLE_MODULE | Service name for Google App Engine |
| GCP_SERVICE_ACCOUNT_KEY | Base64 encoded GCP Service Account Key for App Engine Deploy (Only for CircleCI) |

If you use CircleCI, push the `release` branch to deploy to Google App Engine.

If you deploy it from local host, 

```sh
python set_credentials.py
./gradlew appengineDeploy
```

