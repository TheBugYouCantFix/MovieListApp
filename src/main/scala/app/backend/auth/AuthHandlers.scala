package app.backend

import app.backend.data.repositories.UserRepo
import app.domain
import app.domain.Credentials
import zio.*


object AuthHandlers:
  def loginHandler(credentials: Credentials): ZIO[UserRepo, domain.Error, Unit] =
    ???


