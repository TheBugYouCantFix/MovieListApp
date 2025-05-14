package backend.auth

case class User(
               id: Long,
               username: String,
               passwordHash: String
               ) 


