package backend.data

import com.augustnagro.magnum.{DbCon, Transactor}
import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

val dataSource: DataSource = {
  val hikari = new HikariDataSource()
  hikari.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb")
  hikari.setUsername("user")
  hikari.setPassword("password")
  hikari
}
