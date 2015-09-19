//
//  ViewController.swift
//  NightVigilant
//
//  Created by Gabriele Petronella on 9/19/15.
//  Copyright © 2015 buildo. All rights reserved.
//

import UIKit
import GaugeKit
import Cartography

class ViewController: UIViewController, UITextFieldDelegate {

  @IBOutlet weak var gauge: Gauge!
  @IBOutlet weak var spentLabel: UILabel!
  @IBOutlet weak var cardLockedMessage: UILabel!
  @IBOutlet weak var budgetTextView: UITextField!
  @IBOutlet weak var lastExpenseLabel: UILabel!
  @IBOutlet weak var remainingLabel: UILabel!
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    API.getStatus().startWithNext { status in
      guard let spent = status["spent"] as? Float else { return }
      guard let budget = status["budget"] as? Float else { return }
      guard let remaining = status["remaining"] as? Float else { return }
      self.gauge.rate = CGFloat(spent)
      self.spentLabel.text = String(format: "£%.2f", spent)
      self.gauge.maxValue = CGFloat(budget)
      self.budgetTextView.text = String(format: "%.2f", budget)
      self.remainingLabel.text = String(format: "(£%.2f remaining)", remaining)
    }
    
    API.getLastTransation().startWithNext { transaction in
      guard let merchant = transaction["merchant"] as? [String: AnyObject] else { return }
      guard let merchantName = merchant["name"] as? String else { return }
      guard let rawAmount = transaction["amount"] as? Int else { return }
      
      let amount = Int(-Float(rawAmount) / 100)
      
      self.lastExpenseLabel.text = "Latest expense:\n£\(amount) @ \(merchantName)"
    }
    
    NSNotificationCenter.defaultCenter().addObserver(self, selector: "didReceiveTransaction:", name: "Transaction", object: nil)
    
    gauge.rate = 0
    spentLabel.text = String(format: "£%.2f", gauge.rate)
    lastExpenseLabel.text = ""
  }
  
  func didReceiveTransaction(n: NSNotification) {
    guard let spent = n.userInfo?["spent"] as? Float else { return }
    guard let remaining = n.userInfo?["remaining"] as? Float else { return }
    gauge.rate = CGFloat(spent)
    spentLabel.text = String(format: "£%.2f", gauge.rate)
    remainingLabel.text = String(format: "(£%.2f remaining)", remaining)
    if gauge.rate >= gauge.maxValue {
      gauge.startColor = UIColor.redColor()
      spentLabel.textColor = UIColor.redColor()
      cardLockedMessage.hidden = false
    } else {
      gauge.startColor = UIColor.blueColor()
      spentLabel.textColor = UIColor.blackColor()
      cardLockedMessage.hidden = true
    }
    
    API.getLastTransation().startWithNext { transaction in
      guard let merchant = transaction["merchant"] as? [String: AnyObject] else { return }
      guard let merchantName = merchant["name"] as? String else { return }
      guard let rawAmount = transaction["amount"] as? Int else { return }
      
      let amount = Int(-Float(rawAmount) / 100)
      
      self.lastExpenseLabel.text = "Latest expense:\n£\(amount) @ \(merchantName)"
    }
  }
  
  @IBAction func goalChanged(sender: UITextField) {
    guard let maxValue = Float(sender.text ?? "") else { return }
    gauge.maxValue = CGFloat(maxValue)
    let remaining = gauge.maxValue - gauge.rate
    remainingLabel.text = String(format: "(£%.2f remaining)", remaining)
    API.setBudget(maxValue).start()
  }
  
  func textFieldShouldReturn(textField: UITextField) -> Bool {
    textField.resignFirstResponder()
    return false
  }
}

