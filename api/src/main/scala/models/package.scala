package io.buildo.baseexample
import spray.json.JsValue

package object models {
  case class RegisterTokenBody(token: String)
  case class SetBudgetBody(budget: Double)
  case class SetSpentBody(spent: Double)
  case class Status(spent: Double, budget: Double, remaining: Double)
  case class LastTransaction(data: JsValue)
}
