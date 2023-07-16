import requests
import queries
import json
import logging

def testQuery31e9afc09ce34fe1bb3ef5f15c6ee78e(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery83653ba468754236b09917da84adb35a(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery049855824ce446799283d27296bb67fe(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery96b5761768864ea49f9d19df91274a95(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery683b9111807a491b89a6ba88f2038aa2(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


