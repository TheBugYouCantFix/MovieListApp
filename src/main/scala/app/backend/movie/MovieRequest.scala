package app.backend.movie

import app.domain.{Movie, UserId}

// same as movie domain model but w/o uid (it will be taken from the auth header)
case class MovieRequest(
                         name: String,
                         rating: Int,
                         review: String
                       ):
  def toMovie(uid: UserId): Movie = Movie(uid, name, rating, review)