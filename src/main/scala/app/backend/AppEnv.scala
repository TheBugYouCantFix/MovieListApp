package app.backend

import db.repositories.*
import auth.jwt.JwtService

type AppEnv = MovieRepo & UserRepo & JwtService 