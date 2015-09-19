package io.buildo.baseexample

trait RouterModule extends io.buildo.base.MonadicCtrlRouterModule
      with io.buildo.base.ConfigModule
      with JsonSerializerModule
      
      with MondoRouterModule {

  import ExampleJsonProtocol._

  class RouterActorImpl extends RouterActorImplBase with Router
  override def routerClass = classOf[RouterActorImpl]

  trait Router extends RouterBase {
    implicit val implicitActorRefFactory = actorRefFactory

    import spray.routing._
    import spray.routing.Directives._

    val route = {
      mondoRoute
    }
  }

}
