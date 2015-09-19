//
//  Router.swift
//  hailadoc
//
//  Created by Gabriele Petronella on 3/5/15.
//  Copyright (c) 2015 buildo. All rights reserved.
//

import Alamofire
import Foundation

let token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaSI6Im9hdXRoY2xpZW50XzAwMDA5MEN5ekpXNkNLZWZxNngyeWYiLCJleHAiOjE0NDI2NzY0ODcsImlhdCI6MTQ0MjY1NDg4NywianRpIjoidG9rXzAwMDA5MEVQcW0ybGVxekUzVUd0SDciLCJ1aSI6InVzZXJfMDAwMDh3Q2c0Z3JTeTQ0bzViVzhxUCIsInYiOiIxIn0.AySL5aknbj9DGJJ3JJv54rMWYq5emYTyDhqi9MsU2Q4"

enum Router: URLRequestConvertible {
  static var baseURLString: String {
    return "https://api.getmondo.co.uk"
  }

//MARK: Routes

  case ListTransactions

//MARK: Request builder

  var URLRequest: NSMutableURLRequest {
    let result: (method: Alamofire.Method, path: String, parameters: [String: AnyObject]?) = {
      switch self {
        
      case .ListTransactions:
        return (.GET, "transactions", ["account_id": "acc_00008wCg4hfRyE3iacu3kn"])

      }
    }()

    let URL = NSURL(string: Router.baseURLString)!
    let mutableURLRequest = NSMutableURLRequest(URL: URL.URLByAppendingPathComponent(result.path))
    mutableURLRequest.HTTPMethod = result.method.rawValue

    mutableURLRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")

    return ParameterEncoding.URL.encode(mutableURLRequest, parameters: result.parameters).0
  }
}
