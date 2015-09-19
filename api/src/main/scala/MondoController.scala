package io.buildo.baseexample

import models._

import scalaz._
import Scalaz._
import scalaz.EitherT._

import scala.concurrent.ExecutionContext.Implicits.global

trait MondoControllerModule extends io.buildo.base.MonadicCtrlModule {
  object mondoController {
    var token: Option[String] = Some("519b3f259c1147b6873053a6042a7ecdedfc368c5bee33b06a2d2f6ae06a7e4f")

    val certificatePath = "/Users/utaal/Downloads/NV/aps_dev_credentials.p12"
    val certificatePassword = "nightvigilant"
    val sound = ""

    private lazy val service = {
      import com.notnoop.apns._
      val s = APNS.newService()
        .withCert(certificatePath, certificatePassword)
      if (/*sandbox*/ true)
        s
          .withSandboxDestination()
          .build()
      else
        s
          .withProductionDestination()
          .build()
    }

    def webhook(txnCreated: TransactionCreated): FutureCtrlFlow[Unit] = {
      println(txnCreated)
      import com.notnoop.apns._
      val notif = APNS.newPayload()
        .customField("amount", txnCreated.data.amount)
        .alertBody(s"Transaction! ${txnCreated.data.amount}")
        //.sound(sound)
        .build()
      token.map { t =>
        try {
          println(s"TOKEN: $t")
          service.push(t, notif)
        } catch {
          case e: Throwable => e.printStackTrace
        }
      }.getOrElse {
        println("no token")
      }
      ()
    }.point[FutureCtrlFlow]

    def registerToken(tokenBody: RegisterTokenBody): FutureCtrlFlow[Unit] = {
      println(tokenBody)
      token = Some(tokenBody.token)
      ()
    }.point[FutureCtrlFlow]
    // def getAll: FutureCtrlFlow[List[Mondo]] = List(
    //   Mondo("Le Marze", 15),
    //   Mondo("Sunset Mondo", 22)).point[FutureCtrlFlow]

    // def getByCoolnessAndSize(coolness: String, size: Option[Int]): FutureCtrlFlow[List[Mondo]] = List(
    //   Mondo("Le Marze", 15),
    //   Mondo("Sunset Mondo", 22)).point[FutureCtrlFlow]

    // def getById(id: Int): FutureCtrlFlow[Mondo] = 
    //   Mondo("Le Marze", 15).point[FutureCtrlFlow]

    // def create(camping: Mondo): FutureCtrlFlow[Mondo] =
    //   camping.point[FutureCtrlFlow]

  }
}
