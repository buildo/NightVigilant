package io.buildo.baseexample

package object models {
  case class RegisterTokenBody(token: String)
  case class SetBudgetBody(budget: Double)
  case class SetSpentBody(spent: Double)
  case class Status(spent: Double, budget: Double)
}
