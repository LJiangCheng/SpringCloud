import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        url('/hello') {
            queryParameters {
                parameter("name", "zhangsan")
            }
        }

    }
    response {
        status 200
        body("""
  {
    "code": "000000"
  }
  """)
        headers {
            header('Content-Type': 'application/json')
        }
    }
}