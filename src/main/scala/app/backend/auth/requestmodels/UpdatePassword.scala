package app.backend.auth.requestmodels

import app.domain.{Password, UserId}

// describes the HTTP request
case class UpdatePasswordRequest(
                           password: Password,
                           newPassword: Password 
                         )

// describes the operations in the handler
case class UpdatePasswordOperation(
                                  userId: UserId,
                                  newPassword: Password
                                ) 
