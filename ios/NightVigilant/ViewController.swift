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
  @IBOutlet weak var slider: UISlider!
  @IBOutlet weak var spentLabel: UILabel!
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    gauge.rate = 10
    spentLabel.text = String(format: "%.2f£", gauge.rate)
    slider.value = sliderValueFromRate(gauge.rate)
  }
  
  private func sliderValueFromRate(r: CGFloat) -> Float {
    return Float(r / self.gauge.maxValue)
  }
  
  private func rateFromSliderValue(v: Float) -> CGFloat {
    return CGFloat(v) * self.gauge.maxValue
  }

  @IBAction func sliderValueChanged(sender: UISlider) {
    gauge.rate = rateFromSliderValue(sender.value)
    spentLabel.text = String(format: "%.2f£", gauge.rate)
  }
  
  @IBAction func goalChanged(sender: UITextField) {
    guard let maxValue = Float(sender.text ?? "") else { return }
    gauge.maxValue = CGFloat(maxValue)
  }
  
  func textFieldShouldReturn(textField: UITextField) -> Bool {
    textField.resignFirstResponder()
    return false
  }
}

