//
//  AppDelegate.swift
//  NightVigilant
//
//  Created by Gabriele Petronella on 9/19/15.
//  Copyright © 2015 buildo. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

  var window: UIWindow?


  func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
    
    API.transactions().startWithNext { next in
      print(next)
    }
    
    let settings = UIUserNotificationSettings(forTypes: [.Badge, .Sound, .Alert], categories: nil)
    UIApplication.sharedApplication().registerUserNotificationSettings(settings)
    
    return true
  }

  func application(application: UIApplication, didRegisterUserNotificationSettings notificationSettings: UIUserNotificationSettings) {
    application.registerForRemoteNotifications()
  }

  func application(application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: NSData) {
    let tokenString = deviceToken.description
      .stringByReplacingOccurrencesOfString("<", withString: "")
      .stringByReplacingOccurrencesOfString(">", withString: "")
      .stringByReplacingOccurrencesOfString(" ", withString: "")
    print(deviceToken.description)
    print("Registered for remote notifications with device token: \(tokenString)")
    API.registerNotificationToken(tokenString).start()
  }
  
}

