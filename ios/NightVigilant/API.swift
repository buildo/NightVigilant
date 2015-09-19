//
//  API.swift
//  NightVigilant
//
//  Created by Gabriele Petronella on 9/19/15.
//  Copyright © 2015 buildo. All rights reserved.
//

import ReactiveCocoa
import Alamofire


class API {
  
  class func transactions() -> SignalProducer<[String: AnyObject], NSError> {
    return request(Router.ListTransactions).rac_responseJSON()
  }
  
  class func registerNotificationToken(token: String) -> SignalProducer<[String: AnyObject], NSError> {
    return request(.POST, "https://4c28a52a.ngrok.io/register", parameters: ["token": token], encoding: .JSON).rac_responseJSON()
  }
  
}

extension Request {

  func rac_jsonSignalHandler(observer: Signal<[String: AnyObject], NSError>.Observer, _ disposable: CompositeDisposable) {
    self.responseJSON { (req, res, result) in
      switch result {
      case .Failure(_, let error):
        sendError(observer, error as NSError)
      case .Success(let JSON):
        if let json = JSON as? [String: AnyObject] {
          sendNext(observer, json)
          sendCompleted(observer)
        }
      }
    }
    return
  }

  func rac_responseJSON() -> SignalProducer<[String: AnyObject], NSError> {
    return SignalProducer<[String: AnyObject], NSError>(self.rac_jsonSignalHandler)
  }
  
  
}