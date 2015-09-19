package io.buildo.baseexample.models
case class TransactionData(
  amount: Int,
  created: String,
  currency: String,
  description: String,
  id: String)
case class TransactionCreated(
  data: TransactionData)
