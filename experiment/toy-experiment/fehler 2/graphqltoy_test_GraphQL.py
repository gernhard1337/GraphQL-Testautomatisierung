import requests
import queries
import json
import logging

def testQuerye5544ded3efc48d1950c99242a6f7a92(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery2862a85119d24777bb745c2f4fefba05(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery94c939ce07c74302b6d2412e706e8266(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery8bab6f0ac2a1454b89e26fd590ed9969(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery4f52a0371bfa4bb18beee842135d84d8(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


