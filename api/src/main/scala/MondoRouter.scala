package io.buildo.baseexample

import io.buildo.base.annotation.publishroute

import models._

import spray.routing._
import spray.json._
import spray.routing.Directives._
import spray.httpx.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

trait MondoRouterModule extends io.buildo.base.MonadicCtrlRouterModule
  with io.buildo.base.MonadicRouterHelperModule
  with io.buildo.base.ConfigModule
  with JsonSerializerModule
  
  with MondoControllerModule {

  import ExampleJsonProtocol._
  import RouterHelpers._

  @publishroute
  val mondoRoute = {
    (post & path("register") & entity(as[RegisterTokenBody])) (returns[Unit].ctrl(mondoController.registerToken _)) ~
    (post & path("webhook") & entity(as[TransactionCreated])) (returns[Unit].ctrl(mondoController.webhook _)) ~
    (post & path("setBudget") & entity(as[SetBudgetBody])) (returns[Unit].ctrl(mondoController.setBudget _)) ~
    (post & path("setSpent") & entity(as[SetSpentBody])) (returns[Unit].ctrl(mondoController.setSpent _)) ~
    (get & path("status")) (returns[Status].ctrl(mondoController.status _))
      // (get & pathEnd & parameters('coolness.as[String], 'size.as[Int].?) /**
      //   get campings matching the requested coolness and size
      //   @param coolness how cool it is
      //   @param size the number of tents
      // */) (returns[List[Mondo]].ctrl(campingController.getByCoolnessAndSize _))
      // (get & path(IntNumber) /**
      //   get a camping by id
      // */) (returns[Mondo].ctrl(campingController.getById _)) ~
      // (post & pathEnd & entity(as[Mondo]) /**
      //   create a camping
      // */) (returns[Mondo].ctrl(campingController.create _))
  }
}
