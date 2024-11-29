package fr.cytech.icc

import org.apache.pekko.actor.typed.{ ActorRef, Behavior }
import org.apache.pekko.actor.typed.scaladsl.Behaviors

enum RoomListMessage {
  case CreateRoom(name: String)
  case GetRoom(name: String, replyTo: ActorRef[Option[ActorRef[Message]]])
  case ListRooms(replyTo: ActorRef[Map[String, ActorRef[Message]]])  
}

object RoomListActor {

  import RoomListMessage.*

  def apply(rooms: Map[String, ActorRef[Message]] = Map.empty): Behavior[RoomListMessage] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case CreateRoom(name) =>
          val acref = context.spawn(RoomActor(name), name)
          val entry = name -> acref
          apply(rooms + entry)
        case GetRoom(name, replyTo) =>
          replyTo ! rooms.get(name)
          Behaviors.same
        case ListRooms(replyTo) =>
          replyTo ! rooms  
          Behaviors.same
      }
    }
  }
}