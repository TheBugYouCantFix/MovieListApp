package app.backend.commons

import app.backend.auth.jwt.JwtService
import app.backend.db.repositories.{MovieRepo, UserRepo}

type AppEnv = MovieRepo & UserRepo & JwtService 