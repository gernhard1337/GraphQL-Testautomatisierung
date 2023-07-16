import requests
import queries
import json
import logging

def testQuery1a6bec87857b4a5598668ba832d67099(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery22a9b60a6c9545a8a928e8721027c393(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery3f4295de9eba486bb42e95bca672f95b(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQueryc113eb82d36f42909516e24ab6f854e2(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQueryc0fdac11622d42c3823d796719a9cf5b(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


