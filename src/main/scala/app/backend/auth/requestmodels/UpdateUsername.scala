package app.backend.auth.requestmodels

import app.domain.{Password, Username, UserId}

case class UpdateUsernameRequest(
                         password: Password,
                         newUsername: Username
                         ) 

case class UpdateUsernameOperation(
                                  userId: UserId,
                                  newUsername: Username
                                  ) 