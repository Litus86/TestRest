# TestRest
This is simple project demonstration work a RestApi
Project develop with tools like idea, java 1.8 and maven. For deploy project are use tomcat 8.5. In project realise CRUD methods.

# Examples
Examples are help how work with api when you use cmd
"presetTitle" accept string type,  temperature is int and  time is string


# POST (CREATE)
curl --request POST --data "presetTitle=Test&temperature=92&time=25min" localhost:8080/TestRest/configs/
# GET (READ)
curl localhost:8080/TestRest/configs/?id=1
# PUT (UPDATE)
curl --request PUT --data "id=1#presetTitle=test1&temperature=67&time=26min" localhost:8080/TestRest/configs/
# DELETE (UPDATE)
curl --request DELETE localhost:8080/TestRest/configs/?id=1
