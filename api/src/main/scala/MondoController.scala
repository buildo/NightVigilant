package io.buildo.baseexample

import models._

import scalaz._
import Scalaz._
import scalaz.EitherT._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MondoControllerModule extends io.buildo.base.MonadicCtrlModule {
  class SprayClientSR(actorSystem: akka.actor.ActorSystem, mondoToken: String) {
    import spray.http._
    import spray.client.pipelining._
    import spray.json._
    import spray.httpx.SprayJsonSupport._

    private implicit val system = actorSystem

    //val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val pipeline: HttpRequest => Future[HttpResponse] = (
      addHeader("Authorization", s"Bearer $mondoToken")
      ~> sendReceive
    )
    val dispatcher = system.dispatcher
  }

  def actorSystem: akka.actor.ActorSystem
  object mondoController {
    implicit class IntCentPimp(a: Int) {
      def cents: Double = a.toDouble / 100
    }
    val mondoToken: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaSI6Im9hdXRoY2xpZW50XzAwMDA5MEN5ekpXNkNLZWZxNngyeWYiLCJleHAiOjE0NDI2OTY2ODYsImlhdCI6MTQ0MjY3NTA4NiwianRpIjoidG9rXzAwMDA5MEV0dGFOZ1RPU0l4MU51NnIiLCJ1aSI6InVzZXJfMDAwMDh3Q0VaWEx0WVpYeWY3bUhjdiIsInYiOiIxIn0.GtUuQ8bpq_D6atBe_b3C9yoepDRjLUsilOmJKp-9Fj4"
    var token: Option[String] = Some("20c36901bdc728693cd9961b71a68c741bb0179802b9d82749fd06303a363474")
    var currentSpent: Double = 13.4
    var budget: Double = 100

    val certificatePath = "/Users/utaal/Downloads/NV/aps_dev_credentials.p12"
    val certificatePassword = "nightvigilant"
    val sound = ""
    var lastTxn: Option[String] = None

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
      if (txnCreated.data.amount < 0) {
        lastTxn = Some(txnCreated.data.id)
        println(s"lastTxn $lastTxn")
        currentSpent -= txnCreated.data.amount.cents
        println(s"""
          amount: ${txnCreated.data.amount}
          spent: ${currentSpent}
          remaining: ${budget - currentSpent}
          budget: ${budget}
        """)
        val notif = APNS.newPayload()
          .customField("amount", txnCreated.data.amount)
          .customField("spent", currentSpent)
          .customField("remaining", budget - currentSpent)
          .customField("budget", budget)
          .alertBody(s"You have £${budget - currentSpent} remaining")
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
      }
      ()
    }.point[FutureCtrlFlow]

    def registerToken(tokenBody: RegisterTokenBody): FutureCtrlFlow[Unit] = {
      println(tokenBody)
      token = Some(tokenBody.token)
      ()
    }.point[FutureCtrlFlow]

    def setBudget(setBudget: SetBudgetBody): FutureCtrlFlow[Unit] = {
      println(s"Set budget: ${setBudget.budget}")
      budget = setBudget.budget
      ()
    }.point[FutureCtrlFlow]

    def setSpent(setSpent: SetSpentBody): FutureCtrlFlow[Unit] = {
      println(s"Set spent: ${setSpent.spent}")
      println(s"""
        spent: ${currentSpent}
        remaining: ${budget - currentSpent}
        budget: ${budget}
      """)
      currentSpent = setSpent.spent
      ()
    }.point[FutureCtrlFlow]

    def status: FutureCtrlFlow[Status] = Status(
      spent = currentSpent,
      budget = budget).point[FutureCtrlFlow]

    def lastTransaction: FutureCtrlFlow[LastTransaction] = {
      import spray.http._
      import spray.client.pipelining._
      import spray.json._

      val scsr = new SprayClientSR(actorSystem, mondoToken)

      eitherT {
        lastTxn.map { lt =>
          val response: Future[HttpResponse] = scsr.pipeline(Get(s"https://api.getmondo.co.uk/transactions/$lt?expand[]=merchant"))
          response.map (t => \/-(LastTransaction(t.entity.asString.parseJson))) : Future[CtrlFlow[LastTransaction]]
        }.getOrElse {
          Future.successful(-\/(CtrlError.NotFound)) : Future[CtrlFlow[LastTransaction]]
        }
      }
    }
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
