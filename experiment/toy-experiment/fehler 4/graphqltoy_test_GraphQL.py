import requests
import queries
import json
import logging

def testQuerycad7df24eff44561bf5d94023ebb644f(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"2\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery0796595d3d8b49d5a936fbdf54d60f56(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery1b8bd5a52f5b4339ac378df333aebab8(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuerye944a8623f4f466b808422daf9b0c55f(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery22b0d829175648239afb528fd3175e23(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"1\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


