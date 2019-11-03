"Insert credentials from environment variable from CircleCI."
import os

client_id = os.environ['APPLE_CLIENT_ID']
project_id = os.environ['GOOGLE_PROJECT_ID']
module = os.environ['GOOGLE_MODULE']

def replace(path, map):
    with open(path) as f:
        src = f.read()
        for key in map.keys():
            value = map[key]
            src = src.replace(key,value)
    with open(path, "w") as f:
        f.write(src)

replace('src/main/kotlin/com/tfandkusu/siwatest/IdTokenVerifier.kt',
    {'your.client.id': client_id})

replace('src/main/resources/templates/login.html',{
        'your.client.id': client_id,
        'your_project_id': project_id,
        'your_module': module
    })

replace('src/main/webapp/WEB-INF/appengine-web.xml',
    {'your_module': module})

replace('build.gradle',
    {'your_project_id': project_id})
