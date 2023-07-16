import requests
import queries
import json
import logging

def testQuerya0f69bf6b52e488ca84751adaf3e9541(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQueryeab52cb97bb24d77ae17c833ad9fbbb1(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"TEST\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery94623255ec7a49df82f89927ebfa4cb8(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQuery0ca2b4d9eb604357ac268f0328dd86a1(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"200\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


def testQueryd3f568d444294d4abddd6db4988c1333(caplog):
    caplog.set_level(logging.WARNING)
    response = requests.post("http://localhost:4000/graphql",json={'query':"{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }"})
    response_as_dict = json.loads(response.text)
    measurement = queries.compareQueryResults(response_as_dict, "{ project(id: \"100\", ) {  id  name  description   owner {  id  name  age   }  }  }")
    if measurement["expectedPathLength"] > measurement["pathLengthFromResult"]:
        logging.warning(" Test hat nicht 100% Abdeckung ")
    assert response.status_code == 200


